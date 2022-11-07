package solo.egorov.file_indexer.core.watcher;

/**
 * Interface for index watcher
 *
 * Scans paths added to index:
 *   - Scans folders for added/deleted files, if there are some - adds or removes them from the index
 *   - Scans files if they were modified, if so - updates them in index
 */
public interface IndexWatcher
{
    /**
     * Start index watcher
     */
    void start();

    /**
     * Stop index watcher
     */
    void stop();
}
