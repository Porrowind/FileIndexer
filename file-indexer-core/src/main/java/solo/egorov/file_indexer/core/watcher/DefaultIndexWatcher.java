package solo.egorov.file_indexer.core.watcher;

import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.file.filter.FileFilter;

public class DefaultIndexWatcher implements IndexWatcher
{
    private DefaultIndexWatcherWorker indexWatcherWorker;

    private final IndexWatcherConfiguration configuration;
    private final IndexWatcherRegistry registry;
    private final FileIndexer fileIndexer;
    private final FileFilter fileFilter;

    public DefaultIndexWatcher(IndexWatcherConfiguration configuration, IndexWatcherRegistry registry, FileIndexer fileIndexer, FileFilter fileFilter)
    {
        this.configuration = configuration;
        this.registry = registry;
        this.fileIndexer = fileIndexer;
        this.fileFilter = fileFilter;
    }

    @Override
    public synchronized void start()
    {
        if (indexWatcherWorker != null)
        {
            return;
        }

        indexWatcherWorker = new DefaultIndexWatcherWorker(
            configuration,
            registry,
            fileIndexer,
            fileFilter
        );

        new Thread(indexWatcherWorker).start();
    }

    @Override
    public synchronized void stop()
    {
        if (indexWatcherWorker != null)
        {
            indexWatcherWorker.stop();
            indexWatcherWorker = null;
        }
    }
}
