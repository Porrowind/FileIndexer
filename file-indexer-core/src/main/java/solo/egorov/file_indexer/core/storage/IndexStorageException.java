package solo.egorov.file_indexer.core.storage;

import solo.egorov.file_indexer.core.FileIndexerException;

public class IndexStorageException extends FileIndexerException
{
    public IndexStorageException() {}

    public IndexStorageException(String message)
    {
        super(message);
    }

    public IndexStorageException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IndexStorageException(Throwable cause)
    {
        super(cause);
    }
}
