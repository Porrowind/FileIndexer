package solo.egorov.file_indexer.core.event;

import org.apache.commons.lang3.StringUtils;

/**
 * Event raised when the file processing was finished, and it was not added to index
 */
public class FileDiscardedEvent extends FileProcessedEvent
{
    private final String reason;

    public FileDiscardedEvent(String path)
    {
        this(path, StringUtils.EMPTY);
    }

    public FileDiscardedEvent(String path, String reason)
    {
        super(path);

        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }
}
