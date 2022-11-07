package solo.egorov.file_indexer.core.text;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts text from .txt files
 */
public class TxtDocumentTextExtractor implements TextExtractor
{
    @Override
    public String extract(InputStream rawStream)
    {
        try
        {
            return IOUtils.toString(rawStream);
        }
        catch (IOException ioe)
        {
            return null;
        }
        finally
        {
            if (rawStream != null)
            {
                try
                {
                    rawStream.close();
                }
                catch (Exception e) {}
            }
        }
    }
}
