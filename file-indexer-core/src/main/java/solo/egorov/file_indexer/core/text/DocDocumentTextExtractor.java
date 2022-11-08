package solo.egorov.file_indexer.core.text;

import org.apache.poi.hwpf.HWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts text from .doc files
 */
public class DocDocumentTextExtractor implements TextExtractor
{
    private static final Logger LOG = LoggerFactory.getLogger(DocDocumentTextExtractor.class);

    @Override
    public String extract(InputStream rawStream) throws TextExtractorException
    {
        try
        {
            HWPFDocument document = new HWPFDocument(rawStream);

            return document.getText().toString();
        }
        catch (IOException ioe)
        {
            throw new TextExtractorException("Failed to extract text from the Doc file");
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
                    LOG.error("Failed to close Doc file input stream", e);
                }
            }
        }
    }
}
