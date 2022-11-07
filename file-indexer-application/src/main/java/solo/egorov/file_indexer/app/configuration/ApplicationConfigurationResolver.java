package solo.egorov.file_indexer.app.configuration;

import org.apache.commons.lang3.StringUtils;

public class ApplicationConfigurationResolver
{
    private final ApplicationConfigurationReader configurationReader = new ApplicationConfigurationReader();

    public ApplicationConfiguration resolve(String[] args)
    {
        ApplicationConfiguration configuration = configurationReader.readDefaultConfiguration();
        configurationReader.readFromCommandLine(configuration, args);

        if (StringUtils.isNotBlank(configuration.getConfigurationPath()))
        {
            configurationReader.readFromFile(configuration, configuration.getConfigurationPath());
            configurationReader.readFromCommandLine(configuration, args);
        }

        return configuration;
    }
}
