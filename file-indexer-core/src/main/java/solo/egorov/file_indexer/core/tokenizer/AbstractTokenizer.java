package solo.egorov.file_indexer.core.tokenizer;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.core.IndexedText;
import solo.egorov.file_indexer.core.tokenizer.filter.character.CharacterFilter;
import solo.egorov.file_indexer.core.tokenizer.filter.token.TokenFilter;

import java.nio.charset.Charset;

abstract class AbstractTokenizer<T> implements Tokenizer<T>
{
    private final CharacterFilter characterFilter;
    private final TokenFilter tokenFilter;
    private final Charset charset;

    public AbstractTokenizer(CharacterFilter characterFilter, TokenFilter tokenFilter, Charset charset)
    {
        this.characterFilter = characterFilter;
        this.tokenFilter = tokenFilter;
        this.charset = charset;
    }

    @Override
    public IndexedText tokenize(T text) throws TokenizerException
    {
        return tokenize(text, false);
    }

    @Override
    public CharacterFilter getCharacterFilter()
    {
        return characterFilter;
    }

    @Override
    public TokenFilter getTokenFilter()
    {
        return tokenFilter;
    }

    @Override
    public Charset getCharset()
    {
        return charset;
    }

    protected String normalizeText(String text)
    {
        if (StringUtils.isBlank(text))
        {
            return StringUtils.EMPTY;
        }

        text = StringUtils.trim(text);
        return text.toLowerCase();
    }
}
