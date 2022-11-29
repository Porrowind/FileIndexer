package solo.egorov.file_indexer.core.event;

/**
 * {@link FileIndexerEventBus} configuration
 */
public class FileIndexerEventBusConfiguration
{
    private static final int DEFAULT_WORKER_THREADS_COUNT = 1;

    /**
     * Number of threads to process events in parallel
     * Default is {@value DEFAULT_WORKER_THREADS_COUNT}
     */
    private int workerThreadsCount = DEFAULT_WORKER_THREADS_COUNT;

    public int getWorkerThreadsCount()
    {
        return workerThreadsCount;
    }

    public FileIndexerEventBusConfiguration setWorkerThreadsCount(int workerThreadsCount)
    {
        if (workerThreadsCount < 1)
        {
            workerThreadsCount = 1;
        }

        if (workerThreadsCount > 32)
        {
            workerThreadsCount = 32;
        }

        this.workerThreadsCount = workerThreadsCount;
        return this;
    }
}
