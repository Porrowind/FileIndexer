package solo.egorov.file_indexer.core.file.filter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter files by file extensions
 */
public class FileExtensionFilter implements FileFilter
{
    /**
     * Set of known extensions
     */
    private final Set<String> knownExtensions;

    /**
     * Should filter accept files without extensions
     */
    private final boolean acceptFilesWithNoExtension;

    /**
     * Should filter accept files with unknown extensions
     */
    private final boolean acceptFilesWithUnknownExtension;

    public FileExtensionFilter(Set<String> knownExtensions, boolean acceptFilesWithNoExtension, boolean acceptFilesWithUnknownExtension)
    {
        this.knownExtensions = new HashSet<>();
        if (knownExtensions != null)
        {
            this.knownExtensions.addAll(knownExtensions);
        }
        this.acceptFilesWithNoExtension = acceptFilesWithNoExtension;
        this.acceptFilesWithUnknownExtension = acceptFilesWithUnknownExtension;
    }

    @Override
    public boolean isAccepted(String path)
    {
        if (StringUtils.isBlank(path))
        {
            return true;
        }

        File file = new File(path);

        if (!file.exists())
        {
            return true;
        }

        String extension = FilenameUtils.getExtension(path);

        if (StringUtils.isBlank(extension))
        {
            return acceptFilesWithNoExtension;
        }

        return knownExtensions.contains(extension) || acceptFilesWithUnknownExtension;
    }
}
