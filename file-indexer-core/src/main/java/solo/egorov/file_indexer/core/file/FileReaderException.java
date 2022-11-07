package solo.egorov.file_indexer.core.file;

import solo.egorov.file_indexer.core.FileIndexerException;

/**
 * Exception thrown by {@link FileReader}
 */
public class FileReaderException extends FileIndexerException
{
    public FileReaderException() {}

    public FileReaderException(String message)
    {
        super(message);
    }

    public FileReaderException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FileReaderException(Throwable cause)
    {
        super(cause);
    }
}
