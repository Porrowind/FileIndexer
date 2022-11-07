package solo.egorov.file_indexer.core.file.filter;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class HiddenFilesFilter implements FileFilter
{
    @Override
    public boolean isAccepted(String path)
    {
        if (StringUtils.isBlank(path))
        {
            return true;
        }

        File file = new File(path);

        return !(file.exists() && file.isHidden());
    }
}
