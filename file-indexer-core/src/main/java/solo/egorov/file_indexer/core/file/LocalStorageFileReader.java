package solo.egorov.file_indexer.core.file;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * {@link FileReader} implementation for the local files.
 *
 * Acquires read lock when reading the file.
 */
public class LocalStorageFileReader implements FileReader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalStorageFileReader.class);

    @Override
    public FileInputStream readFile(String path) throws FileReaderException
    {
        if (StringUtils.isBlank(path))
        {
            throw new FileReaderException("Empty path provided");
        }

        FileInputStream fileInputStream = null;

        try
        {
            fileInputStream = new FileInputStream(path);

            fileInputStream.getChannel().lock(0, Long.MAX_VALUE, true);
            return fileInputStream;
        }
        catch (IOException ioe)
        {
            closeFileInputStreamSilently(fileInputStream, path);

            throw new FileReaderException("Failed to lock file: " + path, ioe);
        }
        catch (Exception e)
        {
            closeFileInputStreamSilently(fileInputStream, path);

            throw new FileReaderException("Failed to read local file: " + path, e);
        }
    }

    private void closeFileInputStreamSilently(FileInputStream fileInputStream, String path)
    {
        if (fileInputStream != null)
        {
            try
            {
                fileInputStream.close();
            }
            catch (Exception e)
            {
                LOGGER.error("Failed to close FileInputStream for: " + path, e);
            }
        }
    }
}
