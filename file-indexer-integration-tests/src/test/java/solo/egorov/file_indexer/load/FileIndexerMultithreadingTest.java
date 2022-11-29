package solo.egorov.file_indexer.load;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import solo.egorov.file_indexer.core.AbstractFileIndexerTest;
import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerOptions;
import solo.egorov.file_indexer.core.FileIndexerQuery;
import solo.egorov.file_indexer.core.generator.GeneratedTextConfig;
import solo.egorov.file_indexer.core.generator.GeneratedTextConfigToken;
import solo.egorov.file_indexer.core.generator.TextGenerator;
import solo.egorov.file_indexer.core.listeners.FilesAddedListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.*;

public class FileIndexerMultithreadingTest extends AbstractFileIndexerTest
{
    public FileIndexerMultithreadingTest()
    {
        super("multithreading_test");
    }

    @Test
    @Parameters({"foldersCount", "filesCount", "wordsCount", "poolSize", "threadsCount"})
    public void testMultipleThreadsWorkingWithIndex
    (
        @Optional("5") int foldersCount,
        @Optional("50") int filesCount,
        @Optional("300") int wordsCount,
        @Optional("30000") int poolSize,
        @Optional("16") int threadsCount
    ) throws Exception
    {
        String[] tokenPool = TextGenerator.generateTokenPool(
            new GeneratedTextConfig()
                .setMinTokenLength(5)
                .setMaxTokenLength(10)
                .setAlphabet(GeneratedTextConfig.ALPHABET_NUMBERS)
                .setRandomTokensCount(poolSize)
        );

        String rootFolder = "testMultipleThreadsWorkingWithIndex";
        createFolder(rootFolder);

        Set<String> filenames = new HashSet<>();
        for (int i = 0; i < foldersCount; i++)
        {
            String subfolder = rootFolder + "/" + rootFolder + i;

            createFolder(subfolder);

            for (int j = 0; j < filesCount; j++)
            {
                String filename = subfolder + "/test" + j + ".txt";
                String payload = "test" + i + "a" + j + " payload" + i + "a" + j;

                filenames.add(formatPath(filename));
                generateTextFile(
                    filename,
                    new GeneratedTextConfig()
                        .setRandomTokensPool(tokenPool)
                        .setRandomTokensCount(wordsCount)
                        .addRequiredToken(new GeneratedTextConfigToken(payload))
                );
            }
        }

        FilesAddedListener filesAddedListener1 = new FilesAddedListener(new HashSet<>(filenames));
        fileIndexer.getEventBus().subscribe(filesAddedListener1);

        List<IndexTask> indexTasks = new ArrayList<>();
        for (int i = 0; i < threadsCount; i++)
        {
            indexTasks.add(new IndexTask(fileIndexer, filesAddedListener1, foldersCount, filesCount));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (IndexTask indexTask : indexTasks)
        {
            futures.add(executorService.submit(indexTask));
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(90000L, TimeUnit.MILLISECONDS));

        for (int i = 0; i < threadsCount; i++)
        {
            assertNotNull(futures.get(0));
            assertNotNull(futures.get(0).get());
            assertTrue(futures.get(0).get());
        }
    }

    private final class IndexTask implements Callable<Boolean>
    {
        private final FileIndexer fileIndexer;
        private final FilesAddedListener filesAddedListener;
        private final int foldersCount;
        private final int filesCount;

        public IndexTask(FileIndexer fileIndexer, FilesAddedListener filesAddedListener, int foldersCount, int filesCount)
        {
            this.fileIndexer = fileIndexer;
            this.filesAddedListener = filesAddedListener;
            this.foldersCount = foldersCount;
            this.filesCount = filesCount;
        }

        @Override
        public Boolean call()
        {
            String rootFolder = "testMultipleThreadsWorkingWithIndex";
            fileIndexer.index(new FileIndexerOptions(formatPath(rootFolder)).setRecursiveIndex(true));

            if (!waitUntil(v -> filesAddedListener.isAllAdded(), 90000L))
            {
                return false;
            }

            for (int i = 0; i < foldersCount; i++)
            {
                String subfolder = rootFolder + "/" + rootFolder + i;
                for (int j = 0; j < filesCount; j++)
                {
                    String filename = subfolder + "/test" + j + ".txt";
                    String payload = "test" + i + "a" + j + " payload" + i + "a" + j;

                    List<Document> documents = fileIndexer.search(new FileIndexerQuery(payload));

                    if (documents == null)
                    {
                        return false;
                    }

                    if (documents.size() != 1)
                    {
                        return false;
                    }

                    if (!StringUtils.equals(documents.get(0).getUri(), formatPath(filename)))
                    {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}
