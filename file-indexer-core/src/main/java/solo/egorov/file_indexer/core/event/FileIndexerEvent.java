package solo.egorov.file_indexer.core.event;

/**
 * Base class for each event raised by the {@link solo.egorov.file_indexer.core.FileIndexer}
 */
public class FileIndexerEvent
{
    /**
     * Unique id of event
     */
    private long id;

    /**
     * Timestamp when event was raised
     */
    private long timestamp;

    public long getId()
    {
        return id;
    }

    public FileIndexerEvent setId(long id)
    {
        this.id = id;
        return this;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public FileIndexerEvent setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
        return this;
    }
}
