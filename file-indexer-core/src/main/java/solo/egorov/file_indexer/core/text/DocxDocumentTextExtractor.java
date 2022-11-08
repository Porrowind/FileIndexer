package solo.egorov.file_indexer.core.text;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Extracts text from .docx files
 */
public class DocxDocumentTextExtractor implements TextExtractor
{
    private static final Logger LOG = LoggerFactory.getLogger(DocxDocumentTextExtractor.class);

    @Override
    public String extract(InputStream rawStream) throws TextExtractorException
    {
        try
        {
            XWPFDocument document = new XWPFDocument(rawStream);

            List<XWPFParagraph> paragraphs = document.getParagraphs();

            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph para : paragraphs) {
                sb.append(para.getText());
            }

            return sb.toString();
        }
        catch (IOException ioe)
        {
            throw new TextExtractorException("Failed to extract text from the Docx file");
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
                    LOG.error("Failed to close Docx file input stream", e);
                }
            }
        }
    }
}
