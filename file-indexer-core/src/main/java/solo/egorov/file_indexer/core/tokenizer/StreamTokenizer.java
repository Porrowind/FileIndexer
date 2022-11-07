package solo.egorov.file_indexer.core.tokenizer;

import solo.egorov.file_indexer.core.IndexedText;

import java.io.InputStream;

/**
 * Interface for {@link InputStream} tokenizing
 */
public interface StreamTokenizer extends Tokenizer<InputStream>
{
    @Override
    IndexedText tokenize(InputStream inputStream) throws TokenizerException;

    IndexedText tokenize(InputStream inputStream, boolean ordered) throws TokenizerException;
}
