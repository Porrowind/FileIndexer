package solo.egorov.file_indexer.core;

import org.testng.annotations.Test;
import solo.egorov.file_indexer.core.default_impl.DefaultFileIndexer;
import solo.egorov.file_indexer.core.listeners.FilesAddedListener;
import solo.egorov.file_indexer.core.tokenizer.filter.character.CharacterFilter;
import solo.egorov.file_indexer.core.tokenizer.filter.token.StopWordsTokenFilter;

import java.util.HashSet;
import java.util.List;

import static org.testng.AssertJUnit.*;

public class FileIndexerTokenizerTest extends AbstractFileIndexerTest
{
    public FileIndexerTokenizerTest()
    {
        super("tokenizer_test", false, false);
    }

    @Test
    public void testDefault()
    {
        FileIndexer fileIndexer = new DefaultFileIndexer(
            new FileIndexerConfiguration()
        );
        fileIndexer.start();

        String filepath = "testDefault_1.txt";

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath(filepath)
        );
        createTextFile(filepath, "A.small-test file");

        fileIndexer.getEventBus().subscribe(filesAddedListener);
        fileIndexer.index(new FileIndexerOptions(formatPath(filepath)));

        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("a"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("small test file"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("small+test%file"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        fileIndexer.stop();
    }

    @Test
    public void testTokenLengthFilter()
    {
        FileIndexer fileIndexer = new DefaultFileIndexer(
            new FileIndexerConfiguration()
                .setMinTokenLength(5)
                .setMaxTokenLength(10)
        );
        fileIndexer.start();

        String filepath = "testTokenLengthFilter_1.txt";

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath(filepath)
        );
        createTextFile(filepath, "a aa aaa aaaa aaaaa aaaaaa aaaaaaa aaaaaaaa aaaaaaaaa aaaaaaaaaa aaaaaaaaaaa");

        fileIndexer.getEventBus().subscribe(filesAddedListener);
        fileIndexer.index(new FileIndexerOptions(formatPath(filepath)));

        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("a"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("aa"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("aaa"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("aaaa"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("aaaaa"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("aaaaaa"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("aaaaaaa"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("aaaaaaaa"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("aaaaaaaaa"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("aaaaaaaaaa"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("aaaaaaaaaaa"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        fileIndexer.stop();
    }

    @Test
    public void testStopWordsTokenFilter()
    {
        FileIndexer fileIndexer = new DefaultFileIndexer(
            new FileIndexerConfiguration()
                .setTokenFilter(new StopWordsTokenFilter(new HashSet<String>(){{
                    add("stop");
                    add("word");
                }}))
        );
        fileIndexer.start();

        String filepath = "testStopWordsTokenFilter_1.txt";

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath(filepath)
        );
        createTextFile(filepath, "allowed token stop word");

        fileIndexer.getEventBus().subscribe(filesAddedListener);
        fileIndexer.index(new FileIndexerOptions(formatPath(filepath)));

        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("stop"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("word"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("allowed"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("token"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        fileIndexer.stop();
    }

    @Test
    public void testCustomCharacterFilter()
    {
        FileIndexer fileIndexer = new DefaultFileIndexer(
            new FileIndexerConfiguration()
                .setCharacterFilter(new CharacterFilter()
                {
                    @Override
                    public boolean isAccepted(char ch)
                    {
                        return Character.isDigit(ch);
                    }

                    @Override
                    public boolean isSeparator(char ch)
                    {
                        return '.' == ch;
                    }
                })
        );
        fileIndexer.start();

        String filepath = "testCustomCharacterFilter_1.txt";

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath(filepath)
        );
        createTextFile(filepath, "100.101 102.103asd.104.one.105.test-some");

        fileIndexer.getEventBus().subscribe(filesAddedListener);
        fileIndexer.index(new FileIndexerOptions(formatPath(filepath)));

        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("100"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("101"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("101102"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("103asd"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("103"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("104"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("one"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("105"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        documents = fileIndexer.search(new FileIndexerQuery("test"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("some"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("103-104-105"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);

        documents = fileIndexer.search(new FileIndexerQuery("103.104.105"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filepath));

        fileIndexer.stop();
    }
}
