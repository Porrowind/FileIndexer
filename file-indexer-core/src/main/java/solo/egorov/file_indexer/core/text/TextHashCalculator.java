package solo.egorov.file_indexer.core.text;

import solo.egorov.file_indexer.core.FileIndexerException;

import java.security.MessageDigest;

/**
 * Calculates hash from text
 */
public class TextHashCalculator
{
    public byte[] calculateHash(String text)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(text.getBytes());
            return messageDigest.digest();
        }
        catch (Exception e)
        {
            throw new FileIndexerException("Cannot generate a digest", e);
        }
    }
}
