package solo.egorov.file_indexer.core;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class SimpleTest extends AbstractFileIndexerTest
{
    public SimpleTest()
    {
        super("simple_test");
    }

    @Test
    public void simpleTest()
        throws Exception
    {
        createTextFile("test.txt", "Mikhail Egorov");

        FileIndexerOptions options = new FileIndexerOptions().setPath(formatPath("test.txt"));

        fileIndexer.index(options);
        Thread.sleep(200L);
        List<Document> documents = fileIndexer.search(new FileIndexerQuery().setSearchText("Egorov"));
        assertEquals(documents.size(), 1);
        documents = fileIndexer.search(new FileIndexerQuery().setSearchText("Ivanov"));
        assertEquals(documents.size(), 0);

        Document d = fileIndexer.get(options);
        assertNotNull(d);
    }
}
