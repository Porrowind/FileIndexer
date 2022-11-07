package solo.egorov.file_indexer.core.text;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Extracts text from .docx files
 */
public class DocxDocumentTextExtractor implements TextExtractor
{
    @Override
    public String extract(InputStream rawStream)
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
