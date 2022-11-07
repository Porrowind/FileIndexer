package solo.egorov.file_indexer.app;

public class ValidationException extends ApplicationException
{
    public ValidationException() {}

    public ValidationException(String message)
    {
        super(message);
    }

    public ValidationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ValidationException(Throwable cause)
    {
        super(cause);
    }
}
