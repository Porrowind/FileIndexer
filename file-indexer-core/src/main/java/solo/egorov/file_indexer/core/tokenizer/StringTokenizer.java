package solo.egorov.file_indexer.core.tokenizer;

import solo.egorov.file_indexer.core.IndexedText;

/**
 * Interface for {@link String} tokenizing
 */
public interface StringTokenizer extends Tokenizer<String>
{
    @Override
    IndexedText tokenize(String text) throws TokenizerException;

    IndexedText tokenize(String text, boolean ordered) throws TokenizerException;
}
