package solo.egorov.file_indexer.app;

import solo.egorov.file_indexer.core.FileIndexerException;

public class ApplicationException extends FileIndexerException
{
    public ApplicationException() {}

    public ApplicationException(String message)
    {
        super(message);
    }

    public ApplicationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ApplicationException(Throwable cause)
    {
        super(cause);
    }
}
