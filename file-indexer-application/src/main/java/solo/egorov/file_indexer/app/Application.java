package solo.egorov.file_indexer.app;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solo.egorov.file_indexer.app.command.ApplicationCommandContainer;
import solo.egorov.file_indexer.app.command.ApplicationCommand;
import solo.egorov.file_indexer.app.command.ApplicationCommandResolver;
import solo.egorov.file_indexer.app.configuration.ApplicationConfiguration;
import solo.egorov.file_indexer.app.configuration.ApplicationConfigurationResolver;
import solo.egorov.file_indexer.app.configuration.StopWordsTokenFilterBuider;
import solo.egorov.file_indexer.app.file.FileTraveler;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerConfiguration;
import solo.egorov.file_indexer.core.default_impl.DefaultFileIndexer;
import solo.egorov.file_indexer.core.tokenizer.filter.token.StopWordsTokenFilter;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Scanner;

public class Application
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        ApplicationCommandResolver applicationCommandResolver = new ApplicationCommandResolver();
        ApplicationConfigurationResolver applicationConfigurationResolver = new ApplicationConfigurationResolver();
        ApplicationConfiguration applicationConfiguration = applicationConfigurationResolver.resolve(args);

        ApplicationContext applicationContext = new ApplicationContext()
            .setFileTraveler(new FileTraveler(applicationConfiguration.isProcessHiddenFiles()))
            .setFileIndexer(createFileIndexer(applicationConfiguration));

        applicationContext.getFileIndexer().start();

        System.out.println("FileIndexer started. Print \"help\" for help...");
        while (true)
        {
            System.out.print(applicationContext.getFileTraveler().getCurrentPath() + "> ");
            String command = scanner.nextLine();

            try
            {
                ApplicationCommandContainer applicationCommandContainer = applicationCommandResolver.resolve(command);
                ApplicationCommand applicationCommand = applicationCommandContainer.getCommand();

                Map<String, String> commandArgs = applicationCommandContainer.getCommandArgs();
                ActionResult actionResult = applicationCommand.execute(applicationContext, commandArgs);

                if (StringUtils.isNotBlank(actionResult.getMessage()))
                {
                    System.out.println(actionResult.getMessage());
                }

                if (actionResult.isExit())
                {
                    return;
                }
            }
            catch (Exception e)
            {
                LOGGER.error("Exception occurred: " + e.getMessage(), e);
                System.out.println("Exception occurred: " + e.getMessage());
            }
        }
    }

    private static FileIndexer createFileIndexer(ApplicationConfiguration configuration)
    {
        FileIndexerConfiguration fileIndexerConfiguration = new FileIndexerConfiguration();

        if (configuration.getWorkerThreadsCount() != null)
        {
            fileIndexerConfiguration.setWorkerThreadsCount(configuration.getWorkerThreadsCount());
        }

        if (configuration.getMinTokenLength() != null)
        {
            fileIndexerConfiguration.setMinTokenLength(configuration.getMinTokenLength());
        }

        if (configuration.getMaxTokenLength() != null)
        {
            fileIndexerConfiguration.setMaxTokenLength(configuration.getMaxTokenLength());
        }

        if (configuration.getMaxFileSize() != null)
        {
            fileIndexerConfiguration.setMaxFileSize(configuration.getMaxFileSize());
        }

        if (configuration.isProcessHiddenFiles() != null)
        {
            fileIndexerConfiguration.setProcessHiddenFiles(configuration.isProcessHiddenFiles());
        }

        if (configuration.isProcessFilesWithNoExtension() != null)
        {
            fileIndexerConfiguration.setProcessFilesWithNoExtension(configuration.isProcessFilesWithNoExtension());
        }

        if (configuration.isProcessFilesWithUnknownExtension() != null)
        {
            fileIndexerConfiguration.setProcessFilesWithUnknownExtension(configuration.isProcessFilesWithUnknownExtension());
        }

        if (configuration.getCharset() != null)
        {
            fileIndexerConfiguration.setCharset(Charset.forName(configuration.getCharset()));
        }

        if (configuration.isWatcherEnabled() != null)
        {
            fileIndexerConfiguration.getIndexWatcherConfiguration()
                .setEnabled(configuration.isWatcherEnabled());
        }

        if (configuration.getWatcherTimeout() != null)
        {
            fileIndexerConfiguration.getIndexWatcherConfiguration()
                .setTimeout(configuration.getWatcherTimeout());
        }

        if (configuration.getWatcherFileTimeout() != null)
        {
            fileIndexerConfiguration.getIndexWatcherConfiguration()
                .setFileTimeout(configuration.getWatcherFileTimeout());
        }

        StopWordsTokenFilter stopWordsTokenFilter = new StopWordsTokenFilterBuider()
            .build(configuration);
        fileIndexerConfiguration.setTokenFilter(stopWordsTokenFilter);

        return new DefaultFileIndexer(fileIndexerConfiguration);
    }
}
