package solo.egorov.file_indexer.core;

import solo.egorov.file_indexer.core.event.FileIndexerEventBusConfiguration;
import solo.egorov.file_indexer.core.file.FileReader;
import solo.egorov.file_indexer.core.file.filter.FileFilter;
import solo.egorov.file_indexer.core.query.QueryProcessor;
import solo.egorov.file_indexer.core.storage.IndexStorage;
import solo.egorov.file_indexer.core.text.TextExtractorFactory;
import solo.egorov.file_indexer.core.tokenizer.filter.character.CharacterFilter;
import solo.egorov.file_indexer.core.tokenizer.filter.token.TokenFilter;
import solo.egorov.file_indexer.core.watcher.IndexWatcherConfiguration;

import java.nio.charset.Charset;

/**
 * {@link FileIndexer} configuration
 */
@SuppressWarnings("unused")
public class FileIndexerConfiguration
{
    private static final int DEFAULT_WORKER_THREADS_COUNT = 4;
    private static final int DEFAULT_MIN_TOKEN_LENGTH = 3;
    private static final int DEFAULT_MAX_TOKEN_LENGTH = 128;
    private static final long DEFAULT_MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final boolean DEFAULT_PROCESS_HIDDEN_FILES = false;
    private static final boolean DEFAULT_PROCESS_FILES_WITH_NO_EXTENSION = false;
    private static final boolean DEFAULT_PROCESS_FILES_WITH_UNKNOWN_EXTENSION = false;

    /**
     * {@link solo.egorov.file_indexer.core.watcher.IndexWatcher} configuration
     */
    private final IndexWatcherConfiguration indexWatcherConfiguration = new IndexWatcherConfiguration();

    /**
     * {@link solo.egorov.file_indexer.core.event.FileIndexerEventBus} configuration
     */
    private final FileIndexerEventBusConfiguration eventBusConfiguration = new FileIndexerEventBusConfiguration();

    /**
     * Amount of threads to process files in parallel
     * Default is {@value DEFAULT_WORKER_THREADS_COUNT}
     */
    private int workerThreadsCount = DEFAULT_WORKER_THREADS_COUNT;

    /**
     * Minimum token length to be indexed
     * Default is {@value DEFAULT_MIN_TOKEN_LENGTH}
     */
    private int minTokenLength = DEFAULT_MIN_TOKEN_LENGTH;

    /**
     * Maximum token length to be indexed
     * Default is {@value DEFAULT_MAX_TOKEN_LENGTH}
     */
    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

    /**
     * Maximum file size to be processed
     * Default is {@value DEFAULT_MAX_FILE_SIZE}
     */
    private long maxFileSize = DEFAULT_MAX_FILE_SIZE;

    /**
     * Process hidden files or not
     * Default is {@value DEFAULT_PROCESS_HIDDEN_FILES}
     */
    private boolean processHiddenFiles = DEFAULT_PROCESS_HIDDEN_FILES;

    /**
     * Process files without extension or not
     * Default is {@value DEFAULT_PROCESS_FILES_WITH_NO_EXTENSION}
     */
    private boolean processFilesWithNoExtension = DEFAULT_PROCESS_FILES_WITH_NO_EXTENSION;

    /**
     * Process files with unknown extension or not
     * Default is {@value DEFAULT_PROCESS_FILES_WITH_NO_EXTENSION}
     */
    private boolean processFilesWithUnknownExtension = DEFAULT_PROCESS_FILES_WITH_UNKNOWN_EXTENSION;

    /**
     * Custom {@link TextExtractorFactory}
     */
    private TextExtractorFactory textExtractorFactory;

    /**
     * Custom {@link CharacterFilter}
     */
    private CharacterFilter characterFilter;

    /**
     * Custom {@link TokenFilter}
     */
    private TokenFilter tokenFilter;

    /**
     * Custom {@link FileFilter}
     */
    private FileFilter fileFilter;

    /**
     * Custom {@link FileReader}
     */
    private FileReader fileReader;

    /**
     * Custom {@link IndexStorage}
     */
    private IndexStorage indexStorage;

    /**
     * Custom {@link QueryProcessor}
     */
    private QueryProcessor queryProcessor;

    /**
     * Charset to be used
     */
    private Charset charset = Charset.defaultCharset();

    public IndexWatcherConfiguration getIndexWatcherConfiguration()
    {
        return indexWatcherConfiguration;
    }

    public FileIndexerEventBusConfiguration getEventBusConfiguration()
    {
        return eventBusConfiguration;
    }

    public int getWorkerThreadsCount()
    {
        return workerThreadsCount;
    }

    public FileIndexerConfiguration setWorkerThreadsCount(int workerThreadsCount)
    {
        if (workerThreadsCount < 1)
        {
            workerThreadsCount = 1;
        }

        if (workerThreadsCount > 32)
        {
            workerThreadsCount = 32;
        }

        this.workerThreadsCount = workerThreadsCount;
        return this;
    }

    public int getMinTokenLength()
    {
        return minTokenLength;
    }

    public FileIndexerConfiguration setMinTokenLength(int minTokenLength)
    {
        this.minTokenLength = minTokenLength;
        return this;
    }

    public int getMaxTokenLength()
    {
        return maxTokenLength;
    }

    public FileIndexerConfiguration setMaxTokenLength(int maxTokenLength)
    {
        this.maxTokenLength = maxTokenLength;
        return this;
    }

    public long getMaxFileSize()
    {
        return maxFileSize;
    }

    public FileIndexerConfiguration setMaxFileSize(long maxFileSize)
    {
        this.maxFileSize = maxFileSize;
        return this;
    }

    public boolean isProcessHiddenFiles()
    {
        return processHiddenFiles;
    }

    public FileIndexerConfiguration setProcessHiddenFiles(boolean processHiddenFiles)
    {
        this.processHiddenFiles = processHiddenFiles;
        return this;
    }

    public boolean isProcessFilesWithNoExtension()
    {
        return processFilesWithNoExtension;
    }

    public FileIndexerConfiguration setProcessFilesWithNoExtension(boolean processFilesWithNoExtension)
    {
        this.processFilesWithNoExtension = processFilesWithNoExtension;
        return this;
    }

    public boolean isProcessFilesWithUnknownExtension()
    {
        return processFilesWithUnknownExtension;
    }

    public FileIndexerConfiguration setProcessFilesWithUnknownExtension(boolean processFilesWithUnknownExtension)
    {
        this.processFilesWithUnknownExtension = processFilesWithUnknownExtension;
        return this;
    }

    public TextExtractorFactory getTextExtractorFactory()
    {
        return textExtractorFactory;
    }

    public FileIndexerConfiguration setTextExtractorFactory(TextExtractorFactory textExtractorFactory)
    {
        this.textExtractorFactory = textExtractorFactory;
        return this;
    }

    public CharacterFilter getCharacterFilter()
    {
        return characterFilter;
    }

    public FileIndexerConfiguration setCharacterFilter(CharacterFilter characterFilter)
    {
        this.characterFilter = characterFilter;
        return this;
    }

    public TokenFilter getTokenFilter()
    {
        return tokenFilter;
    }

    public FileIndexerConfiguration setTokenFilter(TokenFilter tokenFilter)
    {
        this.tokenFilter = tokenFilter;
        return this;
    }

    public FileFilter getFileFilter()
    {
        return fileFilter;
    }

    public FileIndexerConfiguration setFileFilter(FileFilter fileFilter)
    {
        this.fileFilter = fileFilter;
        return this;
    }

    public FileReader getFileReader()
    {
        return fileReader;
    }

    public FileIndexerConfiguration setFileReader(FileReader fileReader)
    {
        this.fileReader = fileReader;
        return this;
    }

    public IndexStorage getIndexStorage()
    {
        return indexStorage;
    }

    public FileIndexerConfiguration setIndexStorage(IndexStorage indexStorage)
    {
        this.indexStorage = indexStorage;
        return this;
    }

    public QueryProcessor getQueryProcessor()
    {
        return queryProcessor;
    }

    public FileIndexerConfiguration setQueryProcessor(QueryProcessor queryProcessor)
    {
        this.queryProcessor = queryProcessor;
        return this;
    }

    public Charset getCharset()
    {
        return charset;
    }

    public FileIndexerConfiguration setCharset(Charset charset)
    {
        this.charset = charset;
        return this;
    }
}
