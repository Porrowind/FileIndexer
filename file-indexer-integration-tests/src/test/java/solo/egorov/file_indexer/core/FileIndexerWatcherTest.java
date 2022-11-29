package solo.egorov.file_indexer.core;

import org.testng.annotations.Test;
import solo.egorov.file_indexer.core.generator.GeneratedTextConfig;
import solo.egorov.file_indexer.core.generator.GeneratedTextConfigToken;
import solo.egorov.file_indexer.core.listeners.FilesAddedListener;
import solo.egorov.file_indexer.core.listeners.FilesDeletedListener;

import java.util.List;

import static org.testng.AssertJUnit.*;
import static org.testng.AssertJUnit.assertEquals;

public class FileIndexerWatcherTest extends AbstractFileIndexerTest
{
    public FileIndexerWatcherTest()
    {
        super("index_watcher_test", false, true);
    }

    @Override
    protected FileIndexerConfiguration initFileIndexerConfiguration()
    {
        FileIndexerConfiguration configuration = super.initFileIndexerConfiguration();
        configuration.getIndexWatcherConfiguration().setEnabled(true);
        return configuration;
    }

    @Test
    public void testSingleFileWatcher()
    {
        String filename1 = "testSingleFileWatcher_1.txt";
        String filename2 = "testSingleFileWatcher_2.txt";

        generateTextFile(
            filename1,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Brazil Switzerland Football"))
        );

        generateTextFile(
            filename2,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Spain Germany Football"))
        );

        FilesAddedListener filesAddedListener1 = new FilesAddedListener(
            formatPath(filename1),
            formatPath(filename2)
        );
        fileIndexer.getEventBus().subscribe(filesAddedListener1);

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        fileIndexer.index(new FileIndexerOptions(formatPath(filename1)));
        fileIndexer.index(new FileIndexerOptions(formatPath(filename2)));

        assertTrue(waitUntil(v -> filesAddedListener1.isAllAdded()));

        documents = fileIndexer.search(new FileIndexerQuery("Brazil Switzerland Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename1));

        documents = fileIndexer.search(new FileIndexerQuery("Spain Germany Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Portugal Uruguay Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        FilesAddedListener filesAddedListener2 = new FilesAddedListener(formatPath(filename1));
        fileIndexer.getEventBus().subscribe(filesAddedListener2);

        generateTextFile(
            filename1,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Portugal Uruguay Football"))
        );

        assertTrue(waitUntil(v -> filesAddedListener2.isAllAdded()));

        documents = fileIndexer.search(new FileIndexerQuery("Brazil Switzerland Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Portugal Uruguay Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename1));

        FilesDeletedListener filesDeletedListener1 = new FilesDeletedListener(formatPath(filename2));
        fileIndexer.getEventBus().subscribe(filesDeletedListener1);
        deleteFile(formatPath(filename2));
        assertTrue(waitUntil(v -> filesDeletedListener1.isAllDeleted()));

        documents = fileIndexer.search(new FileIndexerQuery("Spain Germany Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        FilesAddedListener filesAddedListener3 = new FilesAddedListener(formatPath(filename2));
        fileIndexer.getEventBus().subscribe(filesAddedListener3);

        generateTextFile(
            filename2,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Belgium Morocco Football"))
        );

        fileIndexer.index(new FileIndexerOptions(formatPath(filename2)));
        assertTrue(waitUntil(v -> filesAddedListener3.isAllAdded()));

        documents = fileIndexer.search(new FileIndexerQuery("Belgium Morocco Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        FilesAddedListener filesAddedListener4 = new FilesAddedListener(formatPath(filename2));
        fileIndexer.getEventBus().subscribe(filesAddedListener4);

        generateTextFile(
            filename2,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Spain Germany Football"))
        );

        assertTrue(waitUntil(v -> filesAddedListener4.isAllAdded()));

        documents = fileIndexer.search(new FileIndexerQuery("Spain Germany Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 2);
        documentWithPathPresented(documents, formatPath(filename1));
        documentWithPathPresented(documents, formatPath(filename2));
    }

    @Test
    public void testFolderFileWatcher()
    {
        String rootFolder = "testFoldersAddedAndDeleted";
        String folder1 = rootFolder + "/testFoldersAddedAndDeleted_1";
        String folder2 = rootFolder + "/testFoldersAddedAndDeleted_2";

        String filename1 = rootFolder + "/test1.txt";
        String filename2 = rootFolder + "/test2.txt";
        String filename3 = folder1 + "/test1.txt";
        String filename4 = folder1 + "/test2.txt";
        String filename5 = folder2 + "/test1.txt";
        String filename6 = folder2 + "/test2.txt";

        FilesAddedListener filesAddedListener1 = new FilesAddedListener(
            formatPath(filename1)
        );

        FilesAddedListener filesAddedListener2 = new FilesAddedListener(
            formatPath(filename1),
            formatPath(filename2),
            formatPath(filename3),
            formatPath(filename4),
            formatPath(filename5),
            formatPath(filename6)
        );

        createFolder(rootFolder);
        createFolder(folder1);
        createFolder(folder2);

        fileIndexer.getEventBus().subscribe(filesAddedListener1);

        generateTextFile(
            filename1,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Brazil Switzerland Football"))
        );

        fileIndexer.index(new FileIndexerOptions(formatPath(rootFolder)).setRecursiveIndex(true));
        assertTrue(waitUntil(v -> filesAddedListener1.isAllAdded()));

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        documentWithPathPresented(documents, formatPath(filename1));

        fileIndexer.getEventBus().subscribe(filesAddedListener2);

        generateTextFile(
            filename1,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Argentina Mexico Football"))
        );

        generateTextFile(
            filename2,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Portugal Uruguay Football"))
        );

        generateTextFile(
            filename3,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Spain Germany Football"))
        );

        generateTextFile(
            filename4,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Belgium Morocco Football"))
        );

        generateTextFile(
            filename5,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Croatia Canada Football"))
        );

        generateTextFile(
            filename6,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("France Denmark Football"))
        );

        assertTrue(waitUntil(v -> filesAddedListener2.isAllAdded()));

        documents = fileIndexer.search(new FileIndexerQuery("Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 6);
        documentWithPathPresented(documents, formatPath(filename1));
        documentWithPathPresented(documents, formatPath(filename2));
        documentWithPathPresented(documents, formatPath(filename3));
        documentWithPathPresented(documents, formatPath(filename4));
        documentWithPathPresented(documents, formatPath(filename5));
        documentWithPathPresented(documents, formatPath(filename6));

        FilesDeletedListener filesDeletedListener1 = new FilesDeletedListener(
            formatPath(filename1),
            formatPath(filename2),
            formatPath(filename3),
            formatPath(filename4),
            formatPath(filename5),
            formatPath(filename6)
        );

        fileIndexer.getEventBus().subscribe(filesDeletedListener1);
        deleteFolder(rootFolder);
        assertTrue(waitUntil(v -> filesDeletedListener1.isAllDeleted()));

        documents = fileIndexer.search(new FileIndexerQuery("Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);
    }
}
