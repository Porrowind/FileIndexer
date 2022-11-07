package solo.egorov.file_indexer.core.watcher;

import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerOptions;
import solo.egorov.file_indexer.core.file.filter.FileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

class DefaultIndexWatcherWorker implements Runnable
{
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
            processFolders();
            processFiles();
            timeout();
        }
    }

    public void processFolders()
    {
        List<String> folderPaths = registry.getFolderPaths();

        for (String folderPath : folderPaths)
        {
            File folder = new File(folderPath);

            if (!folder.exists() || !folder.isDirectory())
            {
                registry.deleteFolderRecord(folderPath);
                continue;
            }

            try
            {
                Files.list(folder.toPath())
                    .filter(Files::isRegularFile)
                    .filter(p -> fileFilter.isAccepted(p.toString()))
                    .filter(path -> registry.getFileRecord(path.toString()) == null)
                    .forEach(path -> {
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
            catch (IOException ioe)
            {
                //TODO
            }
        }
    }

    private void processFiles()
    {
        List<String> filePaths = registry.getFilePaths();

        for (String filePath : filePaths)
        {
            File file = new File(filePath);

            if (!file.exists() || !file.isFile())
            {
                fileIndexer.delete(new FileIndexerOptions(filePath));
                registry.deleteFolderRecord(filePath);
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
                fileIndexer.index(new FileIndexerOptions(file.getAbsolutePath()));
            }
        }
    }

    private void timeout()
    {
        try
        {
            Thread.sleep(configuration.getTimeout());
        }
        catch (InterruptedException ie) {}
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
