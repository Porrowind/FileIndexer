package solo.egorov.file_indexer.core.text;

import solo.egorov.file_indexer.core.FileIndexerException;

public class TextExtractorException extends FileIndexerException
{
    public TextExtractorException() {}

    public TextExtractorException(String message)
    {
        super(message);
    }

    public TextExtractorException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TextExtractorException(Throwable cause)
    {
        super(cause);
    }
}
