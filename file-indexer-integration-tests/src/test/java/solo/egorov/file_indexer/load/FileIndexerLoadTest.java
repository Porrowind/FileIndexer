package solo.egorov.file_indexer.load;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import solo.egorov.file_indexer.core.AbstractFileIndexerTest;
import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.FileIndexerOptions;
import solo.egorov.file_indexer.core.FileIndexerQuery;
import solo.egorov.file_indexer.core.generator.GeneratedTextConfig;
import solo.egorov.file_indexer.core.generator.GeneratedTextConfigToken;
import solo.egorov.file_indexer.core.generator.TextGenerator;
import solo.egorov.file_indexer.core.listeners.FilesAddedListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.testng.AssertJUnit.*;

public class FileIndexerLoadTest extends AbstractFileIndexerTest
{
    public FileIndexerLoadTest()
    {
        super("load_test");
    }

    @Test
    @Parameters({"filesCount", "wordsCount", "poolSize"})
    public void loadTest
    (
        @Optional("100") int filesCount,
        @Optional("1000") int wordsCount,
        @Optional("0") int poolSize
    )
    {
        String[] tokenPool = poolSize > 0
            ? TextGenerator.generateTokenPool(
                new GeneratedTextConfig()
                    .setMinTokenLength(5)
                    .setMaxTokenLength(10)
                    .setAlphabet(GeneratedTextConfig.ALPHABET_NUMBERS)
                    .setRandomTokensCount(poolSize)
            ) : null;

        String folder = "testManyLargeFilesWithFixedWordsPool";
        createFolder(folder);

        Set<String> filenames = new HashSet<>();
        for (int i = 0; i < filesCount; i++)
        {
            String filename = folder + "/test" + i + ".txt";
            String payload = "test" + i + " payload" + i;

            filenames.add(formatPath(filename));

            if (tokenPool != null)
            {
                generateTextFile(
                    filename,
                    new GeneratedTextConfig()
                        .setRandomTokensPool(tokenPool)
                        .setRandomTokensCount(wordsCount)
                        .addRequiredToken(new GeneratedTextConfigToken(payload))
                );
            }
            else
            {
                generateTextFile(
                    filename,
                    new GeneratedTextConfig()
                        .setMinTokenLength(5)
                        .setMaxTokenLength(10)
                        .setAlphabet(GeneratedTextConfig.ALPHABET_NUMBERS)
                        .setRandomTokensCount(wordsCount)
                        .addRequiredToken(new GeneratedTextConfigToken(payload))
                );
            }
        }

        FilesAddedListener filesAddedListener1 = new FilesAddedListener(new HashSet<>(filenames));
        fileIndexer.getEventBus().subscribe(filesAddedListener1);
        filenames.forEach(f -> fileIndexer.index(new FileIndexerOptions(f)));
        assertTrue(waitUntil(v -> filesAddedListener1.isAllAdded(), 90000L));

        for (int i = 0; i < filesCount; i++)
        {
            String filename = folder + "/test" + i + ".txt";
            String payload = "test" + i + " payload" + i;

            List<Document> documents = fileIndexer.search(new FileIndexerQuery(payload));
            assertNotNull(documents);
            assertEquals(documents.size(), 1);
            assertEquals(documents.get(0).getUri(), formatPath(filename));
        }
    }
}
