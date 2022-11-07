package solo.egorov.file_indexer.core.tokenizer;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.core.IndexedText;
import solo.egorov.file_indexer.core.Token;
import solo.egorov.file_indexer.core.tokenizer.filter.character.CharacterFilter;
import solo.egorov.file_indexer.core.tokenizer.filter.token.TokenFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

//TODO: TARGET -> IndexWriter
public class DefaultStreamTokenizer extends AbstractTokenizer<InputStream> implements StreamTokenizer
{
    private static final int DEFAULT_CHUNK_SIZE = 4 * 1024;

    private final int chunkSize;

    public DefaultStreamTokenizer(CharacterFilter characterFilter, TokenFilter tokenFilter, Charset charset)
    {
        this(characterFilter, tokenFilter, charset, DEFAULT_CHUNK_SIZE);
    }

    public DefaultStreamTokenizer(CharacterFilter characterFilter, TokenFilter tokenFilter, Charset charset, int chunkSize)
    {
        super(characterFilter, tokenFilter, charset);
        this.chunkSize = chunkSize;
    }

    @Override
    public IndexedText tokenize(InputStream inputStream, boolean ordered) throws TokenizerException
    {
        IndexedText indexedText = new IndexedText(ordered);
        long currentPosition = 0;
        StringBuilder currentToken = new StringBuilder();

        Reader reader = new InputStreamReader(inputStream, getCharset());
        char[] chunk = new char[chunkSize];

        try
        {
            while((reader.read(chunk))>=0)
            {
                for (char ch : chunk)
                {
                    if (getCharacterFilter().isSeparator(ch))
                    {
                        String text = normalizeText(currentToken.toString());
                        if (StringUtils.isNotEmpty(text))
                        {
                            indexedText.addToken(new Token(text, currentPosition));
                        }

                        currentToken.setLength(0);
                        currentPosition++;
                    }
                    else
                    {
                        if (getCharacterFilter().isAccepted(ch))
                        {
                            currentToken.append(ch);
                        }
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            throw new TokenizerException("Failed to read stream to tokens", ioe);
        }

        return indexedText;
    }
}
