package solo.egorov.file_indexer.core.watcher;

/**
 * {@link IndexWatcher} configuration
 */
public class IndexWatcherConfiguration
{
    private static final boolean DEFAULT_ENABLED = false;
    private static final long DEFAULT_TIMEOUT = 1000;
    private static final long DEFAULT_FILE_TIMEOUT = 1000 * 60 * 5; // 5 min

    /**
     * Is index watcher enabled.
     * Default is {@value DEFAULT_ENABLED}
     */
    private boolean enabled = DEFAULT_ENABLED;

    /**
     * Timeout between index watcher runs in msec
     * Default is {@value DEFAULT_TIMEOUT}
     */
    private long timeout = DEFAULT_TIMEOUT;

    /**
     * Timeout between processing of the same file in msec
     * Default is {@value DEFAULT_TIMEOUT}
     */
    private long fileTimeout = DEFAULT_FILE_TIMEOUT;

    public boolean isEnabled()
    {
        return enabled;
    }

    public IndexWatcherConfiguration setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public IndexWatcherConfiguration setTimeout(long timeout)
    {
        this.timeout = timeout;
        return this;
    }

    public long getFileTimeout()
    {
        return fileTimeout;
    }

    public IndexWatcherConfiguration setFileTimeout(long fileTimeout)
    {
        this.fileTimeout = fileTimeout;
        return this;
    }
}
