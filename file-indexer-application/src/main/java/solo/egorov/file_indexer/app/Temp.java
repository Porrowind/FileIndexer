package solo.egorov.file_indexer.app;

import solo.egorov.file_indexer.core.default_impl.DefaultFileIndexer;
import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerOptions;
import solo.egorov.file_indexer.core.FileIndexerConfiguration;
import solo.egorov.file_indexer.core.FileIndexerQuery;

import java.util.List;

public class Temp
{
    public static void main(String[] args) throws Exception
    {
        FileIndexerConfiguration configuration = new FileIndexerConfiguration().setWorkerThreadsCount(4);
        FileIndexer indexer = new DefaultFileIndexer(configuration);
        indexer.start();
        indexer.index(new FileIndexerOptions("C:\\idx").setRecursiveIndex(true));
        Thread.sleep(2000);

        /*indexer.index(new IndexOptions("C:\\idx\\CreateOperator.txt"));
        indexer.index(new IndexOptions("C:\\idx\\Credentials.txt"));
        indexer.index(new IndexOptions("C:\\idx\\temp1.txt"));
        indexer.index(new IndexOptions("C:\\idx\\temp2.txt"));
        indexer.index(new IndexOptions("C:\\idx\\temp3.txt"));
        indexer.index(new IndexOptions("C:\\idx\\Kek.docx"));*/

        List<Document> results = indexer.search(new FileIndexerQuery("MEgorov"));
        System.out.println(results);

        results = indexer.search(new FileIndexerQuery("Федерико"));
        System.out.println(results);

        results = indexer.search(new FileIndexerQuery("Федерико Кьеза лучший футболист"));
        System.out.println(results);

        indexer.stop();
    }
}
