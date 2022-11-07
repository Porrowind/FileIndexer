package solo.egorov.file_indexer.app.file;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.configuration.ApplicationConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileTraveler
{
    private Path currentPath;

    public FileTraveler()
    {
        this("/");
    }

    public FileTraveler(String initialPath)
    {
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
            .map(Path::toString)
            .sorted()
            .collect(Collectors.toList());
    }

    public List<String> listFiles() throws IOException
    {
        return Files.list(currentPath)
            .filter(Files::isRegularFile)
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
