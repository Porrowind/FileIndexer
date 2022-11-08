package solo.egorov.file_indexer.core.text;

import java.io.InputStream;

/**
 * Interface for extracting text from files
 */
public interface TextExtractor
{
    /**
     * Extract text from a file
     *
     * @param rawStream {@link InputStream} with a file content
     * @return String representation of a file
     */
    String extract(InputStream rawStream) throws TextExtractorException;
}
