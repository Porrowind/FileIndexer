package solo.egorov.file_indexer.core;

import org.testng.annotations.Test;
import solo.egorov.file_indexer.core.generator.GeneratedTextConfig;
import solo.egorov.file_indexer.core.generator.GeneratedTextConfigToken;
import solo.egorov.file_indexer.core.listeners.FilesAddedListener;
import solo.egorov.file_indexer.core.listeners.FilesDeletedListener;

import java.util.List;

import static org.testng.AssertJUnit.*;

public class FileIndexerMainTest extends AbstractFileIndexerTest
{
    public FileIndexerMainTest()
    {
        super("main_test", false, true);
    }

    @Test
    public void testFilesAddedAndDeleted()
    {
        String filename1 = "testFilesAddedAndDeleted_1.txt";
        String filename2 = "testFilesAddedAndDeleted_2.txt";

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath(filename1),
            formatPath(filename2)
        );
        copyFileFromResources("txt/txt_sample_1.txt", filename1);
        copyFileFromResources("txt/txt_sample_2.txt", filename2);

        fileIndexer.getEventBus().subscribe(filesAddedListener);

        fileIndexer.index(new FileIndexerOptions(formatPath(filename1)));
        fileIndexer.index(new FileIndexerOptions(formatPath(filename2)));
        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));

        Document document = fileIndexer.get(new FileIndexerOptions(formatPath(filename1)));
        assertNotNull(document);
        assertEquals(document.getUri(), formatPath(filename1));

        document = fileIndexer.get(new FileIndexerOptions(formatPath(filename2)));
        assertNotNull(document);
        assertEquals(document.getUri(), formatPath(filename2));

        FilesDeletedListener filesDeletedListener1 = new FilesDeletedListener(formatPath(filename1));
        fileIndexer.getEventBus().subscribe(filesDeletedListener1);
        fileIndexer.delete(new FileIndexerOptions(formatPath(filename1)));

        assertTrue(waitUntil(v -> filesDeletedListener1.isAllDeleted()));
        document = fileIndexer.get(new FileIndexerOptions(formatPath(filename1)));
        assertNull(document);

        document = fileIndexer.get(new FileIndexerOptions(formatPath(filename2)));
        assertNotNull(document);
        assertEquals(document.getUri(), formatPath(filename2));

        FilesDeletedListener filesDeletedListener2 = new FilesDeletedListener(formatPath(filename2));
        fileIndexer.getEventBus().subscribe(filesDeletedListener2);
        fileIndexer.delete(new FileIndexerOptions(formatPath(filename2)));

        assertTrue(waitUntil(v -> filesDeletedListener2.isAllDeleted()));
        document = fileIndexer.get(new FileIndexerOptions(formatPath(filename2)));
        assertNull(document);
    }

    @Test
    public void testFoldersAddedAndDeleted()
    {
        String rootFolder = "testFoldersAddedAndDeleted";
        String folder1 = rootFolder + "/testFoldersAddedAndDeleted_1";
        String folder2 = rootFolder + "/testFoldersAddedAndDeleted_2";

        FilesAddedListener rootFilesAddedListener = new FilesAddedListener(
            formatPath(rootFolder + "/txt_sample_1.txt"),
            formatPath(rootFolder + "/txt_sample_2.txt")
        );
        FilesAddedListener recursiveFilesListener = new FilesAddedListener(
            formatPath(folder1 + "/txt_sample_1.txt"),
            formatPath(folder1 + "/txt_sample_2.txt"),
            formatPath(folder2 + "/txt_sample_1.txt"),
            formatPath(folder2 + "/txt_sample_2.txt")
        );

        createFolder(rootFolder);
        createFolder(folder1);
        createFolder(folder2);
        copyFileFromResources("txt/txt_sample_1.txt", rootFolder + "/txt_sample_1.txt");
        copyFileFromResources("txt/txt_sample_2.txt", rootFolder + "/txt_sample_2.txt");
        copyFileFromResources("txt/txt_sample_1.txt", folder1 + "/txt_sample_1.txt");
        copyFileFromResources("txt/txt_sample_2.txt", folder1 + "/txt_sample_2.txt");
        copyFileFromResources("txt/txt_sample_1.txt", folder2 + "/txt_sample_1.txt");
        copyFileFromResources("txt/txt_sample_2.txt", folder2 + "/txt_sample_2.txt");

        fileIndexer.getEventBus().subscribe(rootFilesAddedListener);
        fileIndexer.getEventBus().subscribe(recursiveFilesListener);

        fileIndexer.index(new FileIndexerOptions(formatPath(rootFolder)));
        assertTrue(waitUntil(v -> rootFilesAddedListener.isAllAdded()));
        assertFalse(waitUntil(v -> recursiveFilesListener.isAllAdded(), 100L));

        fileIndexer.index(new FileIndexerOptions(formatPath(rootFolder)).setRecursiveIndex(true));
        assertTrue(waitUntil(v -> recursiveFilesListener.isAllAdded()));

        FilesDeletedListener filesDeletedListener1 = new FilesDeletedListener(
            formatPath(rootFolder + "/txt_sample_1.txt"),
            formatPath(rootFolder + "/txt_sample_2.txt"));
        fileIndexer.getEventBus().subscribe(filesDeletedListener1);
        fileIndexer.delete(new FileIndexerOptions(formatPath(rootFolder)));

        assertTrue(waitUntil(v -> filesDeletedListener1.isAllDeleted()));
        assertNull(fileIndexer.get(new FileIndexerOptions(formatPath(rootFolder + "/txt_sample_1.txt"))));
        assertNull(fileIndexer.get(new FileIndexerOptions(formatPath(rootFolder + "/txt_sample_2.txt"))));

        Document document = fileIndexer.get(new FileIndexerOptions(formatPath(folder1 + "/txt_sample_1.txt")));
        assertNotNull(document);
        assertEquals(document.getUri(), formatPath(folder1 + "/txt_sample_1.txt"));

        FilesDeletedListener filesDeletedListener2 = new FilesDeletedListener(
            formatPath(folder1 + "/txt_sample_1.txt"),
            formatPath(folder1 + "/txt_sample_2.txt"),
            formatPath(folder2 + "/txt_sample_1.txt"),
            formatPath(folder2 + "/txt_sample_2.txt")
        );
        fileIndexer.getEventBus().subscribe(filesDeletedListener2);
        fileIndexer.delete(new FileIndexerOptions(formatPath(folder1)));
        fileIndexer.delete(new FileIndexerOptions(formatPath(folder2)));

        assertTrue(waitUntil(v -> filesDeletedListener2.isAllDeleted()));
        assertNull(fileIndexer.get(new FileIndexerOptions(formatPath(folder1 + "/txt_sample_1.txt"))));
        assertNull(fileIndexer.get(new FileIndexerOptions(formatPath(folder1 + "/txt_sample_2.txt"))));
        assertNull(fileIndexer.get(new FileIndexerOptions(formatPath(folder2 + "/txt_sample_1.txt"))));
        assertNull(fileIndexer.get(new FileIndexerOptions(formatPath(folder2 + "/txt_sample_2.txt"))));
    }

    @Test
    public void testSimpleSearch()
    {
        String filename1 = "testSimpleSearch_1.txt";
        String filename2 = "testSimpleSearch_2.txt";

        generateTextFile(
            filename1,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Brazil"))
                .addRequiredToken(new GeneratedTextConfigToken("Switzerland"))
                .addRequiredToken(new GeneratedTextConfigToken("Football"))
        );

        generateTextFile(
            filename2,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Portugal"))
                .addRequiredToken(new GeneratedTextConfigToken("Uruguay"))
                .addRequiredToken(new GeneratedTextConfigToken("Football"))
        );

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath(filename1),
            formatPath(filename2)
        );
        fileIndexer.getEventBus().subscribe(filesAddedListener);

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        fileIndexer.index(new FileIndexerOptions(formatPath(filename1)));
        fileIndexer.index(new FileIndexerOptions(formatPath(filename2)));

        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));

        documents = fileIndexer.search(new FileIndexerQuery("Brazil"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename1));

        documents = fileIndexer.search(new FileIndexerQuery("Switzerland"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename1));

        documents = fileIndexer.search(new FileIndexerQuery("Portugal"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Uruguay"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 2);
        assertTrue(documentWithPathPresented(documents, formatPath(filename1)));
        assertTrue(documentWithPathPresented(documents, formatPath(filename2)));

        FilesDeletedListener filesDeletedListener1 = new FilesDeletedListener(formatPath(filename1));
        fileIndexer.getEventBus().subscribe(filesDeletedListener1);
        fileIndexer.delete(new FileIndexerOptions(formatPath(filename1)));
        assertTrue(waitUntil(v -> filesDeletedListener1.isAllDeleted()));

        documents = fileIndexer.search(new FileIndexerQuery("Brazil"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Switzerland"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Portugal"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Uruguay"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertFalse(documentWithPathPresented(documents, formatPath(filename1)));
        assertTrue(documentWithPathPresented(documents, formatPath(filename2)));

        FilesDeletedListener filesDeletedListener2 = new FilesDeletedListener(formatPath(filename2));
        fileIndexer.getEventBus().subscribe(filesDeletedListener2);
        fileIndexer.delete(new FileIndexerOptions(formatPath(filename2)));
        assertTrue(waitUntil(v -> filesDeletedListener2.isAllDeleted()));

        documents = fileIndexer.search(new FileIndexerQuery("Brazil"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Switzerland"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Portugal"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Uruguay"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Football"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);
    }

    @Test
    public void testCompositeSearch()
    {
        String filename1 = "testCompositeSearch_1.txt";
        String filename2 = "testCompositeSearch_2.txt";

        generateTextFile(
            filename1,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Brazil Casemiro Sandro"))
                .addRequiredToken(new GeneratedTextConfigToken("Switzerland Sommer Embolo"))
                .addRequiredToken(new GeneratedTextConfigToken("Football World Cup"))
        );

        generateTextFile(
            filename2,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .addRequiredToken(new GeneratedTextConfigToken("Portugal Ronaldo Cancelo"))
                .addRequiredToken(new GeneratedTextConfigToken("Uruguay Bentancur Valverde"))
                .addRequiredToken(new GeneratedTextConfigToken("Football World Cup"))
        );

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath(filename1),
            formatPath(filename2)
        );
        fileIndexer.getEventBus().subscribe(filesAddedListener);

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("Football World Cup"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        fileIndexer.index(new FileIndexerOptions(formatPath(filename1)));
        fileIndexer.index(new FileIndexerOptions(formatPath(filename2)));

        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));

        documents = fileIndexer.search(new FileIndexerQuery("Brazil Casemiro Sandro"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename1));

        documents = fileIndexer.search(new FileIndexerQuery("Brazil Sandro"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Brazil Sandro").setStrict(false));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename1));

        documents = fileIndexer.search(new FileIndexerQuery("Switzerland Sommer Embolo"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename1));

        documents = fileIndexer.search(new FileIndexerQuery("Portugal Ronaldo Cancelo"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Portugal Cancelo"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Portugal Cancelo").setStrict(false));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Uruguay Bentancur Valverde"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Football World Cup"));
        assertNotNull(documents);
        assertEquals(documents.size(), 2);
        assertTrue(documentWithPathPresented(documents, formatPath(filename1)));
        assertTrue(documentWithPathPresented(documents, formatPath(filename2)));

        FilesDeletedListener filesDeletedListener1 = new FilesDeletedListener(formatPath(filename1));
        fileIndexer.getEventBus().subscribe(filesDeletedListener1);
        fileIndexer.delete(new FileIndexerOptions(formatPath(filename1)));
        assertTrue(waitUntil(v -> filesDeletedListener1.isAllDeleted()));

        documents = fileIndexer.search(new FileIndexerQuery("Brazil Casemiro Sandro"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Switzerland Sommer Embolo"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Portugal Ronaldo Cancelo"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Uruguay Bentancur Valverde"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Football World Cup"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertFalse(documentWithPathPresented(documents, formatPath(filename1)));
        assertTrue(documentWithPathPresented(documents, formatPath(filename2)));

        FilesDeletedListener filesDeletedListener2 = new FilesDeletedListener(formatPath(filename2));
        fileIndexer.getEventBus().subscribe(filesDeletedListener2);
        fileIndexer.delete(new FileIndexerOptions(formatPath(filename2)));
        assertTrue(waitUntil(v -> filesDeletedListener2.isAllDeleted()));

        documents = fileIndexer.search(new FileIndexerQuery("Brazil Casemiro Sandro"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Switzerland Sommer Embolo"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Portugal Ronaldo Cancelo"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Uruguay Bentancur Valverde"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Football World Cup"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);
    }

    @Test
    public void testWildcardSearch()
    {
        String filename1 = "testCompositeSearch_1.txt";
        String filename2 = "testCompositeSearch_2.txt";

        generateTextFile(
            filename1,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .setAlphabet(GeneratedTextConfig.ALPHABET_NUMBERS)
                .addRequiredToken(new GeneratedTextConfigToken("Brazil Casemiro Sandro"))
                .addRequiredToken(new GeneratedTextConfigToken("Switzerland Sommer Embolo"))
                .addRequiredToken(new GeneratedTextConfigToken("Football World Cup"))
        );

        generateTextFile(
            filename2,
            new GeneratedTextConfig()
                .setRandomTokensCount(100)
                .setMinTokenLength(15)
                .setMaxTokenLength(20)
                .setAlphabet(GeneratedTextConfig.ALPHABET_NUMBERS)
                .addRequiredToken(new GeneratedTextConfigToken("Portugal Ronaldo Cancelo"))
                .addRequiredToken(new GeneratedTextConfigToken("Uruguay Bentancur Valverde"))
                .addRequiredToken(new GeneratedTextConfigToken("Football World Cup"))
        );

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath(filename1),
            formatPath(filename2)
        );
        fileIndexer.getEventBus().subscribe(filesAddedListener);

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("Foo*all"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        fileIndexer.index(new FileIndexerOptions(formatPath(filename1)));
        fileIndexer.index(new FileIndexerOptions(formatPath(filename2)));

        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));

        documents = fileIndexer.search(new FileIndexerQuery("Br*il Ca*ro Sa*ro"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename1));

        documents = fileIndexer.search(new FileIndexerQuery("Sw*nd So*er Em*lo"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename1));

        documents = fileIndexer.search(new FileIndexerQuery("*ugal Ro*do Ca*"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("*ay Be*ur Va*"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Fo*ll"));
        assertNotNull(documents);
        assertEquals(documents.size(), 2);
        assertTrue(documentWithPathPresented(documents, formatPath(filename1)));
        assertTrue(documentWithPathPresented(documents, formatPath(filename2)));

        FilesDeletedListener filesDeletedListener1 = new FilesDeletedListener(formatPath(filename1));
        fileIndexer.getEventBus().subscribe(filesDeletedListener1);
        fileIndexer.delete(new FileIndexerOptions(formatPath(filename1)));
        assertTrue(waitUntil(v -> filesDeletedListener1.isAllDeleted()));

        documents = fileIndexer.search(new FileIndexerQuery("Br*il Ca*ro Sa*ro"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Sw*nd So*er Em*lo"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("*ugal Ro*do Ca*"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("*ay Be*ur Va*"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Fo*ll"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertFalse(documentWithPathPresented(documents, formatPath(filename1)));
        assertTrue(documentWithPathPresented(documents, formatPath(filename2)));

        FilesDeletedListener filesDeletedListener2 = new FilesDeletedListener(formatPath(filename2));
        fileIndexer.getEventBus().subscribe(filesDeletedListener2);
        fileIndexer.delete(new FileIndexerOptions(formatPath(filename2)));
        assertTrue(waitUntil(v -> filesDeletedListener2.isAllDeleted()));

        documents = fileIndexer.search(new FileIndexerQuery("Br*il Ca*ro Sa*ro"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Sw*nd So*er Em*lo"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("*ugal Ro*do Ca*"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("*ay Be*ur Va*"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("Fo*ll"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);
    }

    @Test
    public void testSearchWithNonEnglishWords()
    {
        String filename1 = "testSearchWithNonEnglishWords_1.txt";
        String filename2 = "testSearchWithNonEnglishWords_2.txt";

        copyFileFromResources("txt/txt_sample_1.txt", filename1);
        copyFileFromResources("txt/txt_sample_2.txt", filename2);

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath(filename1),
            formatPath(filename2)
        );
        fileIndexer.getEventBus().subscribe(filesAddedListener);

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("Txt file sample"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        fileIndexer.index(new FileIndexerOptions(formatPath(filename1)));
        fileIndexer.index(new FileIndexerOptions(formatPath(filename2)));

        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));

        documents = fileIndexer.search(new FileIndexerQuery("Txt file sample"));
        assertNotNull(documents);
        assertEquals(documents.size(), 2);
        documentWithPathPresented(documents, formatPath(filename1));
        documentWithPathPresented(documents, formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Txt file sample number one"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        documentWithPathPresented(documents, formatPath(filename1));

        documents = fileIndexer.search(new FileIndexerQuery("Txt file sample number two"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        documentWithPathPresented(documents, formatPath(filename2));

        documents = fileIndexer.search(new FileIndexerQuery("Русский текст"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        documentWithPathPresented(documents, formatPath(filename1));
    }
}
