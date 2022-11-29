package solo.egorov.file_indexer.core.default_impl.watcher;

public class IndexWatcherFileRecord
{
    private final String path;
    private final IndexWatcherRegistryState registryState;
    private final long lastModified;

    public IndexWatcherFileRecord(String path, IndexWatcherRegistryState registryState, long lastModified)
    {
        this.path = path;
        this.registryState = registryState;
        this.lastModified = lastModified;
    }

    public String getPath()
    {
        return path;
    }

    public IndexWatcherRegistryState getRegistryState()
    {
        return registryState;
    }

    public long getLastModified()
    {
        return lastModified;
    }
}
