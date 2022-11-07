package solo.egorov.file_indexer.core;

/**
 * Exception thrown by {@link FileIndexer}
 */
public class FileIndexerException extends RuntimeException
{
    public FileIndexerException() {}

    public FileIndexerException(String message)
    {
        super(message);
    }

    public FileIndexerException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FileIndexerException(Throwable cause)
    {
        super(cause);
    }
}
