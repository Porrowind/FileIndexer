package solo.egorov.file_indexer.core.tokenizer;

import solo.egorov.file_indexer.core.IndexedText;
import solo.egorov.file_indexer.core.tokenizer.filter.character.CharacterFilter;
import solo.egorov.file_indexer.core.tokenizer.filter.token.TokenFilter;

import java.nio.charset.Charset;

/**
 * Interface for text tokenizing
 *
 * @param <T> Text container type
 */
public interface Tokenizer<T>
{
    /**
     * Index text. Order will not be kept.
     *
     * @param text Text to index
     * @return Indexed text as {@link IndexedText}
     * @throws TokenizerException in case of any exception
     */
    IndexedText tokenize(T text) throws TokenizerException;

    /**
     * Index text.
     *
     * @param text Text to index
     * @param ordered Should keep the tokens order or not
     * @return Indexed text as {@link IndexedText}
     * @throws TokenizerException in case of any exception
     */
    IndexedText tokenize(T text, boolean ordered) throws TokenizerException;

    /**
     * Get used {@link CharacterFilter}
     *
     * @return Used filter
     */
    CharacterFilter getCharacterFilter();

    /**
     * Get used {@link TokenFilter}
     *
     * @return Used filter
     */
    TokenFilter getTokenFilter();

    /**
     * Get used {@link Charset}
     *
     * @return Used charset
     */
    Charset getCharset();
}
