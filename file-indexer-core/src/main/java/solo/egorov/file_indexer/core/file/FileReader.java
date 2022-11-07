package solo.egorov.file_indexer.core.file;

import java.io.InputStream;

/**
 * Interface for reading the files
 */
public interface FileReader
{
    /**
     * Read file to {@link InputStream}
     *
     * @param path Path to the file
     * @return File content as an {@link InputStream}
     * @throws FileReaderException When any exception occurs
     */
    InputStream readFile(String path) throws FileReaderException;
}
