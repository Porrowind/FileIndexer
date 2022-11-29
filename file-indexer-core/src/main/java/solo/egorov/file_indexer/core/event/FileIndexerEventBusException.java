package solo.egorov.file_indexer.core.event;

import solo.egorov.file_indexer.core.FileIndexerException;

/**
 * Exception raised by {@link FileIndexerEventBus}
 */
public class FileIndexerEventBusException extends FileIndexerException
{
    public FileIndexerEventBusException() {}

    public FileIndexerEventBusException(String message)
    {
        super(message);
    }

    public FileIndexerEventBusException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FileIndexerEventBusException(Throwable cause)
    {
        super(cause);
    }
}
