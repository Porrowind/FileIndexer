package solo.egorov.file_indexer.core;

import org.testng.annotations.Test;
import solo.egorov.file_indexer.core.listeners.FilesAddedListener;
import solo.egorov.file_indexer.core.listeners.FilesDeletedListener;
import solo.egorov.file_indexer.core.listeners.FilesDiscardedListener;

import java.util.List;

import static org.testng.AssertJUnit.*;

public class FileIndexerEventEngineTest extends AbstractFileIndexerTest
{
    public FileIndexerEventEngineTest()
    {
        super("event_engine_test");
    }

    @Test
    public void testFileAddedDeletedEvents()
    {
        String filename = "testAddedDeleted_1.txt";

        FilesAddedListener filesAddedListener = new FilesAddedListener(formatPath(filename));
        FilesDeletedListener filesDeletedListener = new FilesDeletedListener(formatPath(filename));

        fileIndexer.getEventBus().subscribe(filesAddedListener);
        fileIndexer.getEventBus().subscribe(filesDeletedListener);

        createTextFile(filename, "Some Text");
        fileIndexer.index(new FileIndexerOptions().setPath(formatPath(filename)));

        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));
        assertFalse(filesDeletedListener.isAllDeleted());

        Document document = fileIndexer.get(new FileIndexerOptions().setPath(formatPath(filename)));
        assertNotNull(document);
        assertEquals(document.getUri(), formatPath(filename));

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("Some Text"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath(filename));

        fileIndexer.delete(new FileIndexerOptions().setPath(formatPath(filename)));

        assertTrue(waitUntil(v -> filesDeletedListener.isAllDeleted()));

        document = fileIndexer.get(new FileIndexerOptions().setPath(formatPath(filename)));
        assertNull(document);

        documents = fileIndexer.search(new FileIndexerQuery("Some Text"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);
    }

    @Test
    public void testFileDiscardedEvent()
    {
        String filename = "testDiscarded_1.unknown";

        FilesDiscardedListener filesDiscardedListener = new FilesDiscardedListener(formatPath(filename));

        fileIndexer.getEventBus().subscribe(filesDiscardedListener);

        createTextFile(filename, "DiscardedText");
        fileIndexer.index(new FileIndexerOptions().setPath(formatPath(filename)));

        assertTrue(waitUntil(v -> filesDiscardedListener.isAllDiscarded()));
        assertTrue(filesDiscardedListener.isAllDiscarded());

        Document document = fileIndexer.get(new FileIndexerOptions().setPath(formatPath(filename)));
        if (document != null)
        {
            System.out.println(document.getUri());
        }
        assertNull(document);

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("DiscardedText"));
        assertNotNull(documents);
        assertEquals(documents.size(), 0);
    }

    @Test
    public void testUnsubscribe()
    {
        String filename1 = "testUnsubscribe_1.txt";
        String filename2 = "testUnsubscribe_2.txt";

        FilesAddedListener filesAddedListener1 = new FilesAddedListener(formatPath(filename1));
        FilesAddedListener filesAddedListener2 = new FilesAddedListener(formatPath(filename2));
        FilesAddedListener filesAddedListener3 = new FilesAddedListener(formatPath(filename1), formatPath(filename2));

        fileIndexer.getEventBus().subscribe(filesAddedListener1);
        fileIndexer.getEventBus().subscribe(filesAddedListener2);
        fileIndexer.getEventBus().subscribe(filesAddedListener3);

        createTextFile(filename1, "Some Text");
        fileIndexer.index(new FileIndexerOptions().setPath(formatPath(filename1)));

        assertTrue(waitUntil(v -> filesAddedListener1.isAllAdded()));
        assertFalse(filesAddedListener2.isAllAdded());
        assertFalse(filesAddedListener3.isAllAdded());

        fileIndexer.getEventBus().unsubscribe(filesAddedListener3);

        createTextFile(filename2, "Some Text");
        fileIndexer.index(new FileIndexerOptions().setPath(formatPath(filename2)));

        assertTrue(waitUntil(v -> filesAddedListener2.isAllAdded()));
        assertTrue(filesAddedListener1.isAllAdded());
        assertFalse(filesAddedListener3.isAllAdded());
    }

    @Test
    public void testMultipleSubscribers()
    {
        String filename1 = "testMultipleSubscribers_1.txt";
        String filename2 = "testMultipleSubscribers_2.txt";

        FilesAddedListener filesAddedListener1 = new FilesAddedListener(formatPath(filename1));
        FilesAddedListener filesAddedListener2 = new FilesAddedListener(formatPath(filename2));
        FilesAddedListener filesAddedListener3 = new FilesAddedListener(formatPath(filename1), formatPath(filename2));
        FilesAddedListener filesAddedListener4 = new FilesAddedListener(formatPath(filename1), formatPath(filename2));

        fileIndexer.getEventBus().subscribe(filesAddedListener1);
        fileIndexer.getEventBus().subscribe(filesAddedListener2);
        fileIndexer.getEventBus().subscribe(filesAddedListener3);
        fileIndexer.getEventBus().subscribe(filesAddedListener4);

        createTextFile(filename1, "Some Text");
        createTextFile(filename2, "Some Text");
        fileIndexer.index(new FileIndexerOptions().setPath(formatPath(filename1)));
        fileIndexer.index(new FileIndexerOptions().setPath(formatPath(filename2)));

        assertTrue(waitUntil(v -> filesAddedListener1.isAllAdded()));
        assertTrue(waitUntil(v -> filesAddedListener2.isAllAdded()));
        assertTrue(waitUntil(v -> filesAddedListener3.isAllAdded()));
        assertTrue(waitUntil(v -> filesAddedListener4.isAllAdded()));

        assertTrue(filesAddedListener1.isAllAdded());
        assertTrue(filesAddedListener2.isAllAdded());
        assertTrue(filesAddedListener3.isAllAdded());
        assertTrue(filesAddedListener4.isAllAdded());
    }
}
