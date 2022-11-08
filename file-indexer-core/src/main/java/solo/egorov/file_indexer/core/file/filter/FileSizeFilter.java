package solo.egorov.file_indexer.core.file.filter;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSizeFilter implements FileFilter
{
    public static final int ANY_LENGTH_ACCEPTED = -1;

    private final long maxFileSize;

    public FileSizeFilter(long maxFileSize)
    {
        this.maxFileSize = maxFileSize;
    }

    @Override
    public boolean isAccepted(String path)
    {
        if (maxFileSize <= 0 || StringUtils.isBlank(path))
        {
            return true;
        }

        try
        {
            File file = new File(path);

            return !(file.exists() && Files.size(Paths.get(path)) >= maxFileSize);
        }
        catch (IOException ioe)
        {
            return false; //Unknown file size
        }
    }
}
