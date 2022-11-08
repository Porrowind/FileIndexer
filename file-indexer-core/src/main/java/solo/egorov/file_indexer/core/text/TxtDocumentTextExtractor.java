package solo.egorov.file_indexer.core.text;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts text from .txt files
 */
public class TxtDocumentTextExtractor implements TextExtractor
{
    private static final Logger LOG = LoggerFactory.getLogger(TxtDocumentTextExtractor.class);

    @Override
    public String extract(InputStream rawStream) throws TextExtractorException
    {
        try
        {
            return IOUtils.toString(rawStream);
        }
        catch (IOException ioe)
        {
            throw new TextExtractorException("Failed to extract text from the Text file");
        }
        finally
        {
            if (rawStream != null)
            {
                try
                {
                    rawStream.close();
                }
                catch (Exception e)
                {
                    LOG.error("Failed to close Text file input stream", e);
                }
            }
        }
    }
}
