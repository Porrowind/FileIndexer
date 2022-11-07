package solo.egorov.file_indexer.core.text;

import org.apache.poi.hwpf.HWPFDocument;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts text from .doc files
 */
public class DocDocumentTextExtractor implements TextExtractor
{
    @Override
    public String extract(InputStream rawStream)
    {
        try
        {
            HWPFDocument document = new HWPFDocument(rawStream);

            return document.getText().toString();
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
