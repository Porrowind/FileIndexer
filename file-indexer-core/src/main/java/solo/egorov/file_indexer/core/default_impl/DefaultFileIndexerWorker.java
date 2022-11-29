package solo.egorov.file_indexer.core.default_impl;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.IndexedText;
import solo.egorov.file_indexer.core.default_impl.event.DefaultFileIndexerEventBus;
import solo.egorov.file_indexer.core.event.FileAddedEvent;
import solo.egorov.file_indexer.core.event.FileDeletedEvent;
import solo.egorov.file_indexer.core.event.FileDiscardedEvent;
import solo.egorov.file_indexer.core.event.FileProcessedEvent;
import solo.egorov.file_indexer.core.event.FileProcessingErrorEvent;
import solo.egorov.file_indexer.core.file.FileReader;
import solo.egorov.file_indexer.core.lock.KeyBasedLock;
import solo.egorov.file_indexer.core.storage.IndexStorage;
import solo.egorov.file_indexer.core.text.TextExtractor;
import solo.egorov.file_indexer.core.text.TextExtractorFactory;
import solo.egorov.file_indexer.core.text.TextHashCalculator;
import solo.egorov.file_indexer.core.tokenizer.StringTokenizer;
import solo.egorov.file_indexer.core.default_impl.watcher.IndexWatcherFileRecord;
import solo.egorov.file_indexer.core.default_impl.watcher.IndexWatcherRegistry;
import solo.egorov.file_indexer.core.default_impl.watcher.IndexWatcherRegistryState;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

class DefaultFileIndexerWorker implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultFileIndexerWorker.class);

    private volatile boolean stopped = false;

    private final DefaultFileIndexerQueue queue;
    private final DefaultFileIndexerEventBus eventBus;
    private final KeyBasedLock lock;

    private final FileReader fileReader;
    private final IndexStorage indexStorage;
    private final TextHashCalculator hashCalculator;
    private final IndexWatcherRegistry indexWatcherRegistry;
    private final StringTokenizer stringTokenizer;
    private final TextExtractorFactory textExtractorFactory;

    public DefaultFileIndexerWorker
    (
        DefaultFileIndexerQueue queue,
        DefaultFileIndexerEventBus eventBus,
        KeyBasedLock lock,
        FileReader fileReader,
        IndexStorage indexStorage,
        TextHashCalculator hashCalculator,
        IndexWatcherRegistry indexWatcherRegistry,
        StringTokenizer stringTokenizer,
        TextExtractorFactory textExtractorFactory
    )
    {
        this.queue = queue;
        this.eventBus = eventBus;
        this.lock = lock;
        this.fileReader = fileReader;
        this.indexStorage = indexStorage;
        this.hashCalculator = hashCalculator;
        this.indexWatcherRegistry = indexWatcherRegistry;
        this.stringTokenizer = stringTokenizer;
        this.textExtractorFactory = textExtractorFactory;
    }

    @Override
    public void run()
    {
        while (!isStopped())
        {
            try
            {
                DefaultFileIndexerWorkerTask nextTask = queue.getNextTask();

                if (nextTask != null && lock.lock(nextTask.getPath()))
                {
                    long processingStartedTimestamp = System.currentTimeMillis();

                    FileProcessedEvent event = null;

                    if (nextTask.isDeleteTask())
                    {
                        event = processDelete(nextTask.getPath());
                    }
                    else if (nextTask.isAddTask())
                    {
                        event = processAdd(nextTask.getPath());
                    }

                    long processingFinishedTimestamp = System.currentTimeMillis();

                    if (event != null)
                    {
                        event.setSubmittedTimestamp(nextTask.getSubmittedTimestamp());
                        event.setStartedTimestamp(processingStartedTimestamp);
                        event.setFinishedTimestamp(processingFinishedTimestamp);

                        eventBus.publish(event);
                    }

                    lock.unlock(nextTask.getPath());
                }

                timeout();
            }
            catch (Exception e)
            {
                LOG.error("[IndexWorker]: " + e.getMessage(), e);
            }
        }
    }

    private FileProcessedEvent processAdd(String path)
    {
        try
        {
            LOG.debug("[IndexWorker] Started indexing the path: " + path);

            File file = new File(path);
            if (!file.exists() || !file.isFile())
            {
                if (indexWatcherRegistry != null)
                {
                    indexWatcherRegistry.deleteFileRecord(path);
                }

                indexStorage.delete(path);

                LOG.debug("[IndexWorker] Finished indexing the path, file is not exist: " + path);
                return new FileDiscardedEvent(path, "File does not exist");
            }

            InputStream fileStream = fileReader.readFile(path);

            TextExtractor textExtractor = textExtractorFactory.getForFileExtensionOrDefault(
                FilenameUtils.getExtension(path)
            );

            String fileText = textExtractor.extract(fileStream);
            byte[] textHash = hashCalculator.calculateHash(fileText);

            Document existingDocument = indexStorage.get(path);
            byte[] existingHash = existingDocument != null ? existingDocument.getHash() : null;

            if (textHash != null && existingHash != null && Arrays.equals(textHash, existingHash))
            {
                if (indexWatcherRegistry != null)
                {
                    indexWatcherRegistry.setFileRecord(
                        path,
                        new IndexWatcherFileRecord(
                            path,
                            IndexWatcherRegistryState.Active,
                            file.lastModified()
                        )
                    );
                }

                LOG.debug("[IndexWorker] Finished indexing the path, file is already indexed: " + path);
                return new FileDiscardedEvent("File is already indexed and didn't change");
            }

            IndexedText indexedText = stringTokenizer.tokenize(fileText);

            indexStorage.add(
                new Document(path)
                    .setDataIndex(indexedText)
                    .setHash(textHash)
            );

            if (indexWatcherRegistry != null)
            {
                indexWatcherRegistry.setFileRecord(
                    path,
                    new IndexWatcherFileRecord(
                        path,
                        IndexWatcherRegistryState.Active,
                        file.lastModified()
                    )
                );
            }

            LOG.debug("[IndexWorker] Finished indexing the path: " + path);
            return new FileAddedEvent(path);
        }
        catch (Exception e)
        {
            LOG.error("[IndexWorker] Failed to add file to index: " + path, e);
            return new FileProcessingErrorEvent(path, e.getMessage(), e);
        }
    }

    private FileProcessedEvent processDelete(String path)
    {
        try
        {
            LOG.debug("[IndexWorker] Started deleting the path from index: " + path);

            indexStorage.delete(path);

            if (indexWatcherRegistry != null)
            {
                IndexWatcherFileRecord fileRecord = indexWatcherRegistry.getFileRecord(path);

                if (fileRecord != null)
                {
                    indexWatcherRegistry.setFileRecord(
                        path,
                        new IndexWatcherFileRecord(
                            path,
                            IndexWatcherRegistryState.Deleted,
                            -1
                        )
                    );
                }
            }

            LOG.debug("[IndexWorker] Finished deleting the path from index: " + path);

            return new FileDeletedEvent(path);
        }
        catch (Exception e)
        {
            LOG.error("[IndexWorker] Failed to remove file from index: " + path, e);
            return new FileProcessingErrorEvent(path, e.getMessage(), e);
        }
    }

    private void timeout() throws InterruptedException
    {
        Thread.sleep(5L);
    }

    private synchronized boolean isStopped()
    {
        return stopped;
    }

    public synchronized void stop()
    {
        this.stopped = true;
    }
}
