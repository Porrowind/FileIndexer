package solo.egorov.file_indexer.core.watcher;

public class IndexWatcherFolderRecord
{
    private final String path;
    private final IndexWatcherRegistryState registryState;

    public IndexWatcherFolderRecord(String path, IndexWatcherRegistryState registryState)
    {
        this.path = path;
        this.registryState = registryState;
    }

    public String getPath()
    {
        return path;
    }

    public IndexWatcherRegistryState getRegistryState()
    {
        return registryState;
    }
}
