package solo.egorov.file_indexer.app;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solo.egorov.file_indexer.app.command.ResolvedApplicationCommand;
import solo.egorov.file_indexer.app.command.ApplicationCommand;
import solo.egorov.file_indexer.app.command.ApplicationCommandResolver;
import solo.egorov.file_indexer.app.configuration.ApplicationConfiguration;
import solo.egorov.file_indexer.app.file.FileTraveler;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerConfiguration;
import solo.egorov.file_indexer.core.default_impl.DefaultFileIndexer;

import java.util.Map;
import java.util.Scanner;

public class Application
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args)
    {
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();

        ApplicationContext applicationContext = new ApplicationContext()
            .setFileTraveler(new FileTraveler())
            .setFileIndexer(createFileIndexer(applicationConfiguration));

        ApplicationCommandResolver applicationCommandResolver = new ApplicationCommandResolver();

        Scanner scanner = new Scanner(System.in);

        applicationContext.getFileIndexer().start();
        while (true)
        {
            System.out.print(applicationContext.getFileTraveler().getCurrentPath() + "> ");
            String command = scanner.nextLine();

            try
            {
                ResolvedApplicationCommand resolvedApplicationCommand = applicationCommandResolver.resolve(command);
                ApplicationCommand applicationCommand = resolvedApplicationCommand.getApplicationCommand();

                Map<String, String> commandArgs = resolvedApplicationCommand.getCommandArgs();
                ActionResult actionResult = applicationCommand.execute(applicationContext, commandArgs);

                if (actionResult.isSuccess() && StringUtils.isNotBlank(actionResult.getMessage()))
                {
                    System.out.println(actionResult.getMessage());
                }

                if (applicationCommand.isExitCommand())
                {
                    return;
                }
            }
            catch (Exception e)
            {
                LOGGER.error("Exception occurred: " + e.getMessage(), e);
            }
        }
    }

    private static FileIndexer createFileIndexer(ApplicationConfiguration configuration)
    {
        FileIndexerConfiguration fileIndexerConfiguration = new FileIndexerConfiguration()
            .setWorkerThreadsCount(4);

        fileIndexerConfiguration.getIndexWatcherConfiguration()
            .setEnabled(true);

        return new DefaultFileIndexer(
            fileIndexerConfiguration
        );
    }
}
