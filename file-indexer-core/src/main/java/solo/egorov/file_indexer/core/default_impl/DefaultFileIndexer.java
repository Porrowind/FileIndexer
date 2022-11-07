package solo.egorov.file_indexer.core.default_impl;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.FileIndexerOptions;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerConfiguration;
import solo.egorov.file_indexer.core.FileIndexerException;
import solo.egorov.file_indexer.core.file.FileReader;
import solo.egorov.file_indexer.core.file.LocalStorageFileReader;
import solo.egorov.file_indexer.core.file.filter.CompositeFileFilter;
import solo.egorov.file_indexer.core.file.filter.FileExtensionFilter;
import solo.egorov.file_indexer.core.file.filter.FileFilter;
import solo.egorov.file_indexer.core.file.filter.FileSizeFilter;
import solo.egorov.file_indexer.core.file.filter.HiddenFilesFilter;
import solo.egorov.file_indexer.core.lock.KeyBasedLock;
import solo.egorov.file_indexer.core.FileIndexerQuery;
import solo.egorov.file_indexer.core.query.DefaultQueryProcessor;
import solo.egorov.file_indexer.core.query.QueryProcessor;
import solo.egorov.file_indexer.core.storage.IndexStorage;
import solo.egorov.file_indexer.core.storage.memory.InMemoryStorage;
import solo.egorov.file_indexer.core.text.TextExtractorFactory;
import solo.egorov.file_indexer.core.text.TextHashCalculator;
import solo.egorov.file_indexer.core.tokenizer.DefaultStreamTokenizer;
import solo.egorov.file_indexer.core.tokenizer.DefaultStringTokenizer;
import solo.egorov.file_indexer.core.tokenizer.StringTokenizer;
import solo.egorov.file_indexer.core.tokenizer.filter.character.CharacterFilter;
import solo.egorov.file_indexer.core.tokenizer.filter.character.DefaultCharacterFilter;
import solo.egorov.file_indexer.core.tokenizer.filter.character.QueryCharacterFilter;
import solo.egorov.file_indexer.core.tokenizer.filter.token.CompositeTokenFilter;
import solo.egorov.file_indexer.core.tokenizer.filter.token.TokenFilter;
import solo.egorov.file_indexer.core.tokenizer.filter.token.TokenLengthFilter;
import solo.egorov.file_indexer.core.watcher.DefaultIndexWatcher;
import solo.egorov.file_indexer.core.watcher.IndexWatcher;
import solo.egorov.file_indexer.core.watcher.IndexWatcherFileRecord;
import solo.egorov.file_indexer.core.watcher.IndexWatcherFolderRecord;
import solo.egorov.file_indexer.core.watcher.IndexWatcherRegistry;
import solo.egorov.file_indexer.core.watcher.IndexWatcherRegistryState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link FileIndexer}
 */
public class DefaultFileIndexer implements FileIndexer
{
    private final FileIndexerConfiguration configuration;

    private final FileReader fileReader;
    private final IndexStorage indexStorage;
    private final TextHashCalculator hashCalculator;
    private final KeyBasedLock fileLock;

    private final DefaultFileIndexerQueue defaultFileIndexerQueue;
    private final List<DefaultFileIndexerWorker> defaultFileIndexerWorkers;
    private final ExecutorService indexWorkersService;

    private final IndexWatcherRegistry indexWatcherRegistry;
    private final IndexWatcher indexWatcher;

    private final StringTokenizer fileTokenizer;
    private final StringTokenizer queryTokenizer;
    private final FileFilter fileFilter;
    private final TextExtractorFactory textExtractorFactory;

    private final QueryProcessor queryProcessor;

    public DefaultFileIndexer(FileIndexerConfiguration configuration)
    {
        this.configuration = configuration;

        this.fileReader = configuration.getFileReader() != null ? configuration.getFileReader() : new LocalStorageFileReader();
        this.fileLock = new KeyBasedLock();

        this.indexStorage = configuration.getIndexStorage() != null ? configuration.getIndexStorage() : new InMemoryStorage();
        this.queryProcessor = configuration.getQueryProcessor() != null ? configuration.getQueryProcessor() : new DefaultQueryProcessor();
        this.defaultFileIndexerQueue = new DefaultFileIndexerQueue();
        this.defaultFileIndexerWorkers = new ArrayList<>();
        this.indexWorkersService = Executors.newFixedThreadPool(configuration.getWorkerThreadsCount());
        this.textExtractorFactory = configuration.getTextExtractorFactory() != null ? configuration.getTextExtractorFactory() : TextExtractorFactory.defaults();
        this.fileFilter = getFileFilter(configuration, this.textExtractorFactory);

        this.fileTokenizer = new DefaultStringTokenizer(
            new DefaultStreamTokenizer(
                getCharacterFilter(configuration),
                getTokenFilter(configuration),
                configuration.getCharset()
            )
        );

        this.queryTokenizer = new DefaultStringTokenizer(
            new DefaultStreamTokenizer(
                new QueryCharacterFilter(getCharacterFilter(configuration)),
                getTokenFilter(configuration),
                configuration.getCharset()
            )
        );

        this.hashCalculator = new TextHashCalculator();

        if (configuration.getIndexWatcherConfiguration().isEnabled())
        {
            this.indexWatcherRegistry = new IndexWatcherRegistry();
            this.indexWatcher = new DefaultIndexWatcher(
                configuration.getIndexWatcherConfiguration(),
                indexWatcherRegistry,
                this,
                fileFilter
            );
        }
        else
        {
            this.indexWatcherRegistry = null;
            this.indexWatcher = null;
        }
    }

    private CharacterFilter getCharacterFilter(FileIndexerConfiguration configuration)
    {
        return configuration.getCharacterFilter() != null
            ? configuration.getCharacterFilter()
            : new DefaultCharacterFilter();
    }

    private TokenFilter getTokenFilter(FileIndexerConfiguration configuration)
    {
        CompositeTokenFilter compositeFilter = new CompositeTokenFilter();
        compositeFilter.addFilter(new TokenLengthFilter(configuration.getMinTokenLength(), configuration.getMaxTokenLength()));

        if (configuration.getTokenFilter() != null)
        {
            compositeFilter.addFilter(configuration.getTokenFilter());
        }

        return compositeFilter;
    }

    private FileFilter getFileFilter(FileIndexerConfiguration configuration, TextExtractorFactory textExtractorFactory)
    {
        CompositeFileFilter compositeFilter = new CompositeFileFilter();
        compositeFilter.addFilter(new FileSizeFilter(configuration.getMaxFileSize()));

        if (!configuration.isProcessHiddenFiles())
        {
            compositeFilter.addFilter(new HiddenFilesFilter());
        }

        compositeFilter.addFilter(
            new FileExtensionFilter(
                textExtractorFactory.getRegisteredExtensions(),
                configuration.isProcessFilesWithNoExtension(),
                configuration.isProcessFilesWithUnknownExtension()
            )
        );

        if (configuration.getFileFilter() != null)
        {
            compositeFilter.addFilter(configuration.getFileFilter());
        }

        return compositeFilter;
    }

    @Override
    public void start()
    {
        if (indexWatcher != null)
        {
            indexWatcher.start();
        }

        startWorkers();
    }

    @Override
    public void stop()
    {
        if (indexWatcher != null)
        {
            indexWatcher.stop();
        }

        stopWorkers();
    }

    @Override
    public void index(FileIndexerOptions options)
    {
        options.setPath(StringUtils.trim(options.getPath()));

        if (StringUtils.isEmpty(options.getPath()))
        {
            throw new FileIndexerException("Empty path provided");
        }

        File file = new File(options.getPath());

        if (!file.exists())
        {
            throw new FileIndexerException("File not found: " + options.getPath());
        }

        if (file.isDirectory())
        {
            indexDirectory(options.getPath(), options, 1);
        }
        else
        {
            indexSingleFile(options.getPath());
        }
    }

    @Override
    public void delete(FileIndexerOptions options) //TODO Folders
    {
        options.setPath(StringUtils.trim(options.getPath()));

        if (StringUtils.isEmpty(options.getPath()))
        {
            throw new FileIndexerException("Empty path provided");
        }

        defaultFileIndexerQueue.addDeleteTask(options.getPath());
    }

    @Override
    public List<Document> search(FileIndexerQuery query)
    {
        return search(query, this.queryProcessor);
    }

    @Override
    public List<Document> search(FileIndexerQuery query, QueryProcessor queryProcessor)
    {
        return queryProcessor.process(query, indexStorage, queryTokenizer);
    }

    @Override
    public void cleanup()
    {
        indexStorage.cleanup();
    }

    private void indexSingleFile(String path)
    {
        if (indexWatcherRegistry != null)
        {
            indexWatcherRegistry.setFileRecord(
                path,
                new IndexWatcherFileRecord(
                    path,
                    IndexWatcherRegistryState.New,
                    -1
                )
            );
        }

        defaultFileIndexerQueue.addAddTask(path);
    }

    private void indexDirectory(String sPath, FileIndexerOptions options, int currentDepth)
    {
        try
        {
            Path path = Paths.get(sPath);

            Files.list(path)
                .filter(Files::isRegularFile)
                .filter(p -> fileFilter.isAccepted(p.toString()))
                .forEach(filePath -> indexSingleFile(filePath.toString()));

            if (indexWatcherRegistry != null)
            {
                indexWatcherRegistry.setFolderRecord(
                    sPath,
                    new IndexWatcherFolderRecord(sPath, IndexWatcherRegistryState.Active)
                );
            }

            if (options.isRecursiveIndex() && (options.getRecursiveIndexDepth() < 0 || currentDepth < options.getRecursiveIndexDepth()))
            {
                Files.list(path)
                    .filter(Files::isDirectory)
                    .filter(p -> !new File(p.toString()).isHidden()) //TODO
                    .map(Path::toString)
                    .forEach(p -> indexDirectory(p, options, currentDepth + 1));
            }
        }
        catch (IOException ioe)
        {
            throw new FileIndexerException("File  : " + options.getPath(), ioe);
        }
    }

    private void startWorkers()
    {
        for (int i = 0; i < configuration.getWorkerThreadsCount(); i++)
        {
            DefaultFileIndexerWorker defaultFileIndexerWorker = new DefaultFileIndexerWorker(
                defaultFileIndexerQueue,
                fileLock,
                fileReader,
                indexStorage,
                hashCalculator,
                indexWatcherRegistry,
                fileTokenizer,
                textExtractorFactory
            );

            defaultFileIndexerWorkers.add(defaultFileIndexerWorker);
            indexWorkersService.execute(defaultFileIndexerWorker);
        }
    }

    private void stopWorkers()
    {
        for (DefaultFileIndexerWorker defaultFileIndexerWorker : defaultFileIndexerWorkers)
        {
            defaultFileIndexerWorker.stop();
        }

        try
        {
            indexWorkersService.shutdown();
            indexWorkersService.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException ie)
        {
            throw new FileIndexerException("Failed to stop the index workers", ie);
        }
    }
}
