package solo.egorov.file_indexer.core.event;

import org.apache.commons.lang3.StringUtils;

/**
 * Event raised when the file processing finished with error
 */
public class FileProcessingErrorEvent extends FileProcessedEvent
{
    /**
     * Error message
     */
    private final String message;

    /**
     * Exception thrown
     */
    private final Exception exception;

    public FileProcessingErrorEvent(String path)
    {
        this(path, StringUtils.EMPTY);
    }

    public FileProcessingErrorEvent(String path, String message)
    {
        this(path, message, null);
    }

    public FileProcessingErrorEvent(String path, String message, Exception exception)
    {
        super(path);

        this.message = message;
        this.exception = exception;
    }

    public String getMessage()
    {
        return message;
    }

    public Exception getException()
    {
        return exception;
    }
}
