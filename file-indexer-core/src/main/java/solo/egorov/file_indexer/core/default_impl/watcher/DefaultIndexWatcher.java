package solo.egorov.file_indexer.core.default_impl.watcher;

import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.file.filter.FileFilter;
import solo.egorov.file_indexer.core.watcher.IndexWatcher;
import solo.egorov.file_indexer.core.watcher.IndexWatcherConfiguration;

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
