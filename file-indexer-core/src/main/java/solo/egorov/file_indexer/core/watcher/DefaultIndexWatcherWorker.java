package solo.egorov.file_indexer.core.watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerOptions;
import solo.egorov.file_indexer.core.file.filter.FileFilter;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

class DefaultIndexWatcherWorker implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultIndexWatcherWorker.class);

    private volatile boolean stopped = false;

    private final IndexWatcherConfiguration configuration;
    private final IndexWatcherRegistry registry;
    private final FileIndexer fileIndexer;
    private final FileFilter fileFilter;

    public DefaultIndexWatcherWorker(IndexWatcherConfiguration configuration, IndexWatcherRegistry registry, FileIndexer fileIndexer, FileFilter fileFilter)
    {
        this.configuration = configuration;
        this.registry = registry;
        this.fileIndexer = fileIndexer;
        this.fileFilter = fileFilter;
    }

    public void run()
    {
        while (!isStopped())
        {
            try
            {
                processFolders();
                processFiles();
                timeout();
            }
            catch (Exception e)
            {
                LOG.error("[IndexWatcher]: " + e.getMessage(), e);
            }
        }
    }

    public void processFolders()
    {
        List<String> folderPaths = registry.getFolderPaths();

        for (String folderPath : folderPaths)
        {
            try
            {
                File folder = new File(folderPath);

                if (!folder.exists() || !folder.isDirectory())
                {
                    LOG.debug("[IndexWatcher]: Folder was removed: " + folderPath);

                    registry.deleteFolderRecord(folderPath);
                    continue;
                }

                Files.list(folder.toPath())
                    .filter(Files::isRegularFile)
                    .filter(p -> fileFilter.isAccepted(p.toString()))
                    .filter(path -> registry.getFileRecord(path.toString()) == null)
                    .forEach(path -> {
                        LOG.debug("[IndexWatcher]: New file was added: " + path);

                        registry.setFileRecord(
                            path.toString(),
                            new IndexWatcherFileRecord(
                                path.toString(),
                                IndexWatcherRegistryState.New,
                                path.toFile().lastModified()
                            )
                        );

                        fileIndexer.index(new FileIndexerOptions(path.toString()));
                    });
            }
            catch (Exception e)
            {
                LOG.error("[IndexWatcher]: Failed to process the folder: " + folderPath, e);
            }
        }
    }

    private void processFiles()
    {
        List<String> filePaths = registry.getFilePaths();

        for (String filePath : filePaths)
        {
            try
            {
                File file = new File(filePath);

                if (!file.exists() || !file.isFile())
                {
                    LOG.debug("[IndexWatcher]: File was removed: " + filePath);

                    fileIndexer.delete(new FileIndexerOptions(filePath));
                    registry.deleteFileRecord(filePath);
                    continue;
                }

                IndexWatcherFileRecord fileRecord = registry.getFileRecord(filePath);

                if (fileRecord == null
                    || fileRecord.getRegistryState() == IndexWatcherRegistryState.New
                    || fileRecord.getRegistryState() == IndexWatcherRegistryState.Deleted)
                {
                    continue;
                }

                if (file.lastModified() > fileRecord.getLastModified())
                {
                    LOG.debug("[IndexWatcher]: File was updated: " + filePath);

                    fileIndexer.index(new FileIndexerOptions(file.getAbsolutePath()));
                }
            }
            catch (Exception e)
            {
                LOG.error("[IndexWatcher]: Failed to process file: " + filePath, e);
            }
        }
    }

    private void timeout()
    {
        try
        {
            Thread.sleep(configuration.getTimeout());
        }
        catch (InterruptedException ie)
        {
            LOG.error("[IndexWatcher]: Interrupted", ie);
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
