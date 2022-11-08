package solo.egorov.file_indexer.core.tokenizer;

import solo.egorov.file_indexer.core.IndexedText;

import java.io.ByteArrayInputStream;

public class DefaultStringTokenizer extends AbstractTokenizer<String> implements StringTokenizer
{
    private final StreamTokenizer streamTokenizer;

    public DefaultStringTokenizer(StreamTokenizer streamTokenizer)
    {
        super(
            streamTokenizer.getCharacterFilter(),
            streamTokenizer.getTokenFilter(),
            streamTokenizer.getCharset()
        );

        this.streamTokenizer = streamTokenizer;
    }

    @Override
    public IndexedText tokenize(String text, boolean ordered) throws TokenizerException
    {
        try (ByteArrayInputStream asd = new ByteArrayInputStream(text.getBytes(getCharset())))
        {
            return streamTokenizer.tokenize(asd, ordered);
        }
        catch (Exception e)
        {
            throw new TokenizerException("Failed to tokenize string", e);
        }
    }
}
