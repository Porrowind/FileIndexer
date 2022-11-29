package solo.egorov.file_indexer.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import solo.egorov.file_indexer.core.default_impl.DefaultFileIndexer;
import solo.egorov.file_indexer.core.generator.GeneratedTextConfig;
import solo.egorov.file_indexer.core.generator.TextGenerator;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractFileIndexerTest
{
    private final String testRootPath;
    private final boolean useSingleIndexer;
    private final boolean useNewIndexerPerMethod;

    protected FileIndexer fileIndexer;

    public AbstractFileIndexerTest(String testRootPath)
    {
        this(testRootPath, true);
    }

    public AbstractFileIndexerTest(String testRootPath, boolean useSingleIndexer)
    {
        this(testRootPath, useSingleIndexer, false);
    }

    public AbstractFileIndexerTest(String testRootPath, boolean useSingleIndexer, boolean useNewIndexerPerMethod)
    {
        this.testRootPath = testRootPath;
        this.useSingleIndexer = useSingleIndexer;
        this.useNewIndexerPerMethod = useNewIndexerPerMethod;
    }

    @BeforeClass
    protected void init()
    {
        createFolder(StringUtils.EMPTY);

        if (useSingleIndexer)
        {
            this.fileIndexer = initFileIndexer();
            this.fileIndexer.start();
        }
    }

    @AfterClass
    protected void destroy()
    {
        if (useSingleIndexer && this.fileIndexer != null)
        {
            this.fileIndexer.stop();
        }

        deleteFolder(StringUtils.EMPTY);
    }

    @BeforeMethod
    protected void onBeforeMethod()
    {
        if (useNewIndexerPerMethod)
        {
            this.fileIndexer = initFileIndexer();
            this.fileIndexer.start();
        }
    }

    @AfterMethod
    protected void onAfterMethod()
    {
        if (useNewIndexerPerMethod && fileIndexer != null)
        {
            this.fileIndexer.stop();
        }
    }

    protected boolean waitUntil(Predicate<Void> predicate)
    {
        return waitUntil(predicate, 5000L);
    }

    protected boolean waitUntil(Predicate<Void> predicate, long waitMsec)
    {
        long endTime = System.currentTimeMillis() + waitMsec;

        while (System.currentTimeMillis() < endTime)
        {
            if (predicate.test(null))
            {
                return true;
            }

            try
            {
                Thread.sleep(5L);
            }
            catch (Exception e)
            {
                return false;
            }
        }

        return false;
    }

    protected boolean documentWithPathPresented(List<Document> documents, String path)
    {
        if (path == null || documents == null || documents.isEmpty())
        {
            return false;
        }

        for (Document document : documents)
        {
            if (path.equals(document.getUri()))
            {
                return true;
            }
        }

        return false;
    }

    protected String formatPath(String path)
    {
        if (!path.startsWith("/"))
        {
            path = "/" + path;
        }

        return new File(testRootPath + path).getAbsolutePath();
    }

    protected void copyFileFromResources(String resourcePath, String targetPath)
    {
        try
        {
            FileUtils.copyInputStreamToFile(getResourceAsInputStream(resourcePath), new File(formatPath(targetPath)));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to copy file from resources: " + resourcePath, e);
        }
    }

    protected void createFile(String filePath, byte[] content)
    {
        try
        {
            FileUtils.writeByteArrayToFile(new File(formatPath(filePath)), content);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create file: " + filePath, e);
        }
    }

    protected void createTextFile(String filePath, String text)
    {
        createTextFile(filePath, text, Charset.defaultCharset());
    }

    protected void generateTextFile(String filePath, GeneratedTextConfig config)
    {
        createTextFile(filePath, TextGenerator.generateText(config), Charset.defaultCharset());
    }

    protected void createTextFile(String filePath, String text, Charset charset)
    {
        try
        {
            FileUtils.write(new File(formatPath(filePath)), text, charset);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create text file: " + filePath, e);
        }
    }

    protected void deleteFile(String filePath)
    {
        try
        {
            new File(filePath).delete();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }

    protected void createFolder(String folderPath)
    {
        createFolder(new File(formatPath(folderPath)));
    }

    protected void deleteFolder(String folderPath)
    {
        deleteFolder(new File(formatPath(folderPath)), true, 0);
    }

    protected void cleanupFolder(String folderPath)
    {
        deleteFolder(new File(formatPath(folderPath)), false, 0);
    }

    protected FileIndexer initFileIndexer()
    {
        return new DefaultFileIndexer(initFileIndexerConfiguration());
    }

    protected FileIndexerConfiguration initFileIndexerConfiguration()
    {
        return new FileIndexerConfiguration();
    }

    private boolean createFolder(File folder)
    {
        if (folder == null || folder.exists())
        {
            return false;
        }

        return folder.mkdirs();
    }

    private boolean deleteFolder(File folder, boolean deleteRoot, int depth)
    {
        if (folder == null || !folder.exists())
        {
            return false;
        }

        File[] allContents = folder.listFiles();
        if (allContents != null)
        {
            for (File file : allContents)
            {
                deleteFolder(file, deleteRoot, depth + 1);
            }
        }

        return (depth > 0 || deleteRoot)
            ? folder.delete()
            : true;
    }

    private InputStream getResourceAsInputStream(String resourcePath)
    {
        return this.getClass()
            .getClassLoader()
            .getResourceAsStream(resourcePath);
    }
}
