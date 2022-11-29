package solo.egorov.file_indexer.core;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;
import solo.egorov.file_indexer.core.default_impl.DefaultFileIndexer;
import solo.egorov.file_indexer.core.listeners.FilesAddedListener;
import solo.egorov.file_indexer.core.listeners.FilesDiscardedListener;
import solo.egorov.file_indexer.core.text.TextExtractorFactory;
import solo.egorov.file_indexer.core.text.TxtDocumentTextExtractor;

import java.util.List;

import static org.testng.AssertJUnit.*;

public class FileIndexerFileFilterTest extends AbstractFileIndexerTest
{
    public FileIndexerFileFilterTest()
    {
        super("file_filter_test", false, false);
    }

    @Test
    public void testDefaultBehavior()
    {
        FileIndexer fileIndexer = new DefaultFileIndexer(
            new FileIndexerConfiguration()
        );
        fileIndexer.start();

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath("txt_sample_1.txt"),
            formatPath("txt_sample_2.txt"),
            formatPath("doc_sample_1.doc"),
            formatPath("large_doc_sample_1.doc"),
            formatPath("docx_sample_1.docx"),
            formatPath("large_docx_sample_1.docx")
        );
        copyFileFromResources("txt/txt_sample_1.txt", "txt_sample_1.txt");
        copyFileFromResources("txt/txt_sample_2.txt", "txt_sample_2.txt");
        copyFileFromResources("doc/doc_sample_1.doc", "doc_sample_1.doc");
        copyFileFromResources("doc/large_doc_sample_1.doc", "large_doc_sample_1.doc");
        copyFileFromResources("docx/docx_sample_1.docx", "docx_sample_1.docx");
        copyFileFromResources("docx/large_docx_sample_1.docx", "large_docx_sample_1.docx");

        FilesDiscardedListener filesDiscardedListener = new FilesDiscardedListener(
            formatPath("java_sample_1.java"),
            formatPath("no_extension_sample_1"),
            formatPath("no_extension_sample_2"),
            formatPath("tst_extension_sample_1.tst")
        );
        copyFileFromResources("other/java_sample_1.java", "java_sample_1.java");
        copyFileFromResources("other/no_extension_sample_1", "no_extension_sample_1");
        copyFileFromResources("other/no_extension_sample_2", "no_extension_sample_2");
        copyFileFromResources("other/tst_extension_sample_1.tst", "tst_extension_sample_1.tst");

        fileIndexer.getEventBus().subscribe(filesAddedListener);
        fileIndexer.getEventBus().subscribe(filesDiscardedListener);

        fileIndexer.index(new FileIndexerOptions(formatPath(StringUtils.EMPTY)));
        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));
        assertTrue(waitUntil(v -> filesDiscardedListener.isAllDiscarded()));

        fileIndexer.stop();
    }

    @Test
    public void testNoExtensionUnknownExtensionEnabled()
    {
        FileIndexer fileIndexer = new DefaultFileIndexer(
            new FileIndexerConfiguration()
                .setProcessFilesWithNoExtension(true)
                .setProcessFilesWithUnknownExtension(true)
        );
        fileIndexer.start();

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath("txt_sample_1.txt"),
            formatPath("txt_sample_2.txt"),
            formatPath("doc_sample_1.doc"),
            formatPath("docx_sample_1.docx"),
            formatPath("java_sample_1.java"),
            formatPath("no_extension_sample_1"),
            formatPath("no_extension_sample_2"),
            formatPath("tst_extension_sample_1.tst")
        );
        copyFileFromResources("txt/txt_sample_1.txt", "txt_sample_1.txt");
        copyFileFromResources("txt/txt_sample_2.txt", "txt_sample_2.txt");
        copyFileFromResources("doc/doc_sample_1.doc", "doc_sample_1.doc");
        copyFileFromResources("docx/docx_sample_1.docx", "docx_sample_1.docx");
        copyFileFromResources("other/java_sample_1.java", "java_sample_1.java");
        copyFileFromResources("other/no_extension_sample_1", "no_extension_sample_1");
        copyFileFromResources("other/no_extension_sample_2", "no_extension_sample_2");
        copyFileFromResources("other/tst_extension_sample_1.tst", "tst_extension_sample_1.tst");

        fileIndexer.getEventBus().subscribe(filesAddedListener);

        fileIndexer.index(new FileIndexerOptions(formatPath(StringUtils.EMPTY)));
        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("public interface FileIndexer"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath("java_sample_1.java"));

        fileIndexer.stop();
    }

    @Test
    public void testCustomExtensionProcessorRegistered()
    {
        FileIndexer fileIndexer = new DefaultFileIndexer(
            new FileIndexerConfiguration()
                .setTextExtractorFactory(
                    TextExtractorFactory.defaults()
                        .registerExtension("java", new TxtDocumentTextExtractor())
                )
        );
        fileIndexer.start();

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath("java_sample_1.java"),
            formatPath("txt_sample_1.txt"),
            formatPath("txt_sample_2.txt"),
            formatPath("doc_sample_1.doc"),
            formatPath("large_doc_sample_1.doc"),
            formatPath("docx_sample_1.docx"),
            formatPath("large_docx_sample_1.docx")
        );
        copyFileFromResources("other/java_sample_1.java", "java_sample_1.java");
        copyFileFromResources("txt/txt_sample_1.txt", "txt_sample_1.txt");
        copyFileFromResources("txt/txt_sample_2.txt", "txt_sample_2.txt");
        copyFileFromResources("doc/doc_sample_1.doc", "doc_sample_1.doc");
        copyFileFromResources("doc/large_doc_sample_1.doc", "large_doc_sample_1.doc");
        copyFileFromResources("docx/docx_sample_1.docx", "docx_sample_1.docx");
        copyFileFromResources("docx/large_docx_sample_1.docx", "large_docx_sample_1.docx");

        FilesDiscardedListener filesDiscardedListener = new FilesDiscardedListener(
            formatPath("no_extension_sample_1"),
            formatPath("no_extension_sample_2"),
            formatPath("tst_extension_sample_1.tst")
        );
        copyFileFromResources("other/no_extension_sample_1", "no_extension_sample_1");
        copyFileFromResources("other/no_extension_sample_2", "no_extension_sample_2");
        copyFileFromResources("other/tst_extension_sample_1.tst", "tst_extension_sample_1.tst");

        fileIndexer.getEventBus().subscribe(filesAddedListener);
        fileIndexer.getEventBus().subscribe(filesDiscardedListener);

        fileIndexer.index(new FileIndexerOptions(formatPath(StringUtils.EMPTY)));
        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));
        assertTrue(waitUntil(v -> filesDiscardedListener.isAllDiscarded()));

        List<Document> documents = fileIndexer.search(new FileIndexerQuery("public interface FileIndexer"));
        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(documents.get(0).getUri(), formatPath("java_sample_1.java"));

        fileIndexer.stop();
    }

    @Test
    public void testLargeFilesDiscarded()
    {
        FileIndexer fileIndexer = new DefaultFileIndexer(
            new FileIndexerConfiguration()
                .setMaxFileSize(1024 * 1024)
        );
        fileIndexer.start();

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath("doc_sample_1.doc"),
            formatPath("docx_sample_1.docx")
        );
        copyFileFromResources("doc/doc_sample_1.doc", "doc_sample_1.doc");
        copyFileFromResources("docx/docx_sample_1.docx", "docx_sample_1.docx");

        FilesDiscardedListener filesDiscardedListener = new FilesDiscardedListener(
            formatPath("large_doc_sample_1.doc"),
            formatPath("large_docx_sample_1.docx")
        );
        copyFileFromResources("doc/large_doc_sample_1.doc", "large_doc_sample_1.doc");
        copyFileFromResources("docx/large_docx_sample_1.docx", "large_docx_sample_1.docx");

        fileIndexer.getEventBus().subscribe(filesAddedListener);
        fileIndexer.getEventBus().subscribe(filesDiscardedListener);

        fileIndexer.index(new FileIndexerOptions(formatPath(StringUtils.EMPTY)));
        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));
        assertTrue(waitUntil(v -> filesDiscardedListener.isAllDiscarded()));

        fileIndexer.stop();
    }

    @Test
    public void testCustomFileFilter()
    {
        FileIndexer fileIndexer = new DefaultFileIndexer(
            new FileIndexerConfiguration()
                .setFileFilter(path -> StringUtils.endsWith(path, "txt_sample_1.txt"))
        );
        fileIndexer.start();

        FilesAddedListener filesAddedListener = new FilesAddedListener(
            formatPath("txt_sample_1.txt")
        );
        copyFileFromResources("txt/txt_sample_1.txt", "txt_sample_1.txt");

        FilesDiscardedListener filesDiscardedListener = new FilesDiscardedListener(
            formatPath("txt_sample_2.txt")
        );
        copyFileFromResources("txt/txt_sample_2.txt", "txt_sample_2.txt");

        fileIndexer.getEventBus().subscribe(filesAddedListener);
        fileIndexer.getEventBus().subscribe(filesDiscardedListener);

        fileIndexer.index(new FileIndexerOptions(formatPath(StringUtils.EMPTY)));
        assertTrue(waitUntil(v -> filesAddedListener.isAllAdded()));
        assertTrue(waitUntil(v -> filesDiscardedListener.isAllDiscarded()));

        fileIndexer.stop();
    }
}
