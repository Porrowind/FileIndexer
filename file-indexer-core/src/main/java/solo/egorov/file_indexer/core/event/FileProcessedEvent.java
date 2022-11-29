package solo.egorov.file_indexer.core.event;

/**
 * Event raised when the file processing was finished
 */
public class FileProcessedEvent extends FileIndexerEvent
{
    /**
     * File path
     */
    private final String path;

    /**
     * Timestamp when the processing task was submitted
     */
    private long submittedTimestamp = -1;

    /**
     * Timestamp when the processing started
     */
    private long startedTimestamp = -1;

    /**
     * Timestamp when the processing finished
     */
    private long finishedTimestamp = -1;

    public FileProcessedEvent(String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }

    public long getSubmittedTimestamp()
    {
        return submittedTimestamp;
    }

    public FileProcessedEvent setSubmittedTimestamp(long submittedTimestamp)
    {
        this.submittedTimestamp = submittedTimestamp;
        return this;
    }

    public long getStartedTimestamp()
    {
        return startedTimestamp;
    }

    public FileProcessedEvent setStartedTimestamp(long startedTimestamp)
    {
        this.startedTimestamp = startedTimestamp;
        return this;
    }

    public long getFinishedTimestamp()
    {
        return finishedTimestamp;
    }

    public FileProcessedEvent setFinishedTimestamp(long finishedTimestamp)
    {
        this.finishedTimestamp = finishedTimestamp;
        return this;
    }
}
