package solo.egorov.file_indexer.core.default_impl;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.IndexedText;
import solo.egorov.file_indexer.core.file.FileReader;
import solo.egorov.file_indexer.core.lock.KeyBasedLock;
import solo.egorov.file_indexer.core.storage.IndexStorage;
import solo.egorov.file_indexer.core.text.TextExtractor;
import solo.egorov.file_indexer.core.text.TextExtractorFactory;
import solo.egorov.file_indexer.core.text.TextHashCalculator;
import solo.egorov.file_indexer.core.tokenizer.StringTokenizer;
import solo.egorov.file_indexer.core.watcher.IndexWatcherFileRecord;
import solo.egorov.file_indexer.core.watcher.IndexWatcherRegistry;
import solo.egorov.file_indexer.core.watcher.IndexWatcherRegistryState;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

class DefaultFileIndexerWorker implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultFileIndexerWorker.class);

    private volatile boolean stopped = false;

    private final DefaultFileIndexerQueue defaultFileIndexerQueue;
    private final KeyBasedLock lock;

    private final FileReader fileReader;
    private final IndexStorage indexStorage;
    private final TextHashCalculator hashCalculator;
    private final IndexWatcherRegistry indexWatcherRegistry;
    private final StringTokenizer stringTokenizer;
    private final TextExtractorFactory textExtractorFactory;

    public DefaultFileIndexerWorker(DefaultFileIndexerQueue defaultFileIndexerQueue, KeyBasedLock lock, FileReader fileReader, IndexStorage indexStorage, TextHashCalculator hashCalculator, IndexWatcherRegistry indexWatcherRegistry, StringTokenizer stringTokenizer, TextExtractorFactory textExtractorFactory)
    {
        this.defaultFileIndexerQueue = defaultFileIndexerQueue;
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
                DefaultFileIndexerWorkerTask nextTask = defaultFileIndexerQueue.getNextTask();

                if (nextTask != null && lock.lock(nextTask.getPath()))
                {
                    if (nextTask.isDeleteTask())
                    {
                        processDelete(nextTask.getPath());
                    }
                    else if (nextTask.isAddTask())
                    {
                        processAdd(nextTask.getPath());
                    }
                    lock.unlock(nextTask.getPath());
                }
            }
            catch (Exception e)
            {
                LOG.error("[IndexWorker]: " + e.getMessage(), e);
            }
        }
    }

    private void processAdd(String path)
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
                return;
            }

            InputStream fileStream = fileReader.readFile(path);

            TextExtractor textExtractor = textExtractorFactory.getForFileExtensionOrDefault(
                FilenameUtils.getExtension(path)
            );

            String fileText = textExtractor.extract(fileStream);

            byte[] textHash = hashCalculator.calculateHash(fileText);
            byte[] existingHash = indexStorage.getDocumentHash(path);

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
                return;
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
        }
        catch (Exception e)
        {
            LOG.error("[IndexWorker] Failed to add file to index: " + path, e);
        }
    }

    private void processDelete(String path)
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
        }
        catch (Exception e)
        {
            LOG.error("[IndexWorker] Failed to remove file from index: " + path, e);
        }
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
