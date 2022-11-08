package solo.egorov.file_indexer.app.file;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileTraveler
{
    private Path currentPath;
    private boolean showHiddenFiles = true;

    public FileTraveler()
    {
        this("/", true);
    }

    public FileTraveler(Boolean showHiddenFiles)
    {
        this("/", showHiddenFiles);
    }

    public FileTraveler(String initialPath, Boolean showHiddenFiles)
    {
        if (showHiddenFiles != null)
        {
            this.showHiddenFiles = showHiddenFiles;
        }

        currentPath = Paths.get(initialPath);
    }

    public String getCurrentPath()
    {
        return currentPath.toString();
    }

    public String resolvePath(String path)
    {
        if (StringUtils.isBlank(path))
        {
            return currentPath.toString();
        }

        return currentPath.resolve(path).normalize().toAbsolutePath().toString();
    }

    public List<String> listDirs() throws IOException
    {
        return Files.list(currentPath)
            .filter(Files::isDirectory)
            .filter(f -> showHiddenFiles || !new File(f.toString()).isHidden())
            .map(Path::toString)
            .sorted()
            .collect(Collectors.toList());
    }

    public List<String> listFiles() throws IOException
    {
        return Files.list(currentPath)
            .filter(Files::isRegularFile)
            .filter(f -> showHiddenFiles || !new File(f.toString()).isHidden())
            .map(Path::toString)
            .sorted()
            .collect(Collectors.toList());
    }

    public void cd(String path)
    {
        if (StringUtils.isBlank(path))
        {
            return;
        }

        Path newPath = currentPath.resolve(path);
        if (Files.exists(newPath))
        {
            currentPath = newPath.normalize();
        }
    }
}
