package solo.egorov.file_indexer.core.tokenizer;

import solo.egorov.file_indexer.core.FileIndexerException;

public class TokenizerException extends FileIndexerException
{
    public TokenizerException() {}

    public TokenizerException(String message)
    {
        super(message);
    }

    public TokenizerException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TokenizerException(Throwable cause)
    {
        super(cause);
    }
}
