package solo.egorov.file_indexer.app.configuration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ApplicationException;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

class ApplicationConfigurationReader
{
    private static final String DEFAULT_CONFIGURATION_RESOURCE = "default_configuration";

    public ApplicationConfiguration readFromCommandLine(ApplicationConfiguration configuration, String[] args)
    {
        try
        {
            if (configuration == null)
            {
                configuration = new ApplicationConfiguration();
            }

            return readFromArgs(configuration, args);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to read configuration from command line arguments", e);
        }
    }

    public ApplicationConfiguration readFromFile(ApplicationConfiguration configuration, String path)
    {
        try
        {
            if (configuration == null)
            {
                configuration = new ApplicationConfiguration();
            }

            String configurationString = FileUtils.readFileToString(new File(path), Charset.defaultCharset());

            return readFromString(configuration, configurationString);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to read configuration from file: " + path, e);
        }
    }

    public ApplicationConfiguration readDefaultConfiguration()
    {
        return readDefaultConfiguration(null);
    }

    public ApplicationConfiguration readDefaultConfiguration(ApplicationConfiguration configuration)
    {
        try
        {
            if (configuration == null)
            {
                configuration = new ApplicationConfiguration();
            }

            InputStream defaultConfigurationStream = this.getClass().getClassLoader()
                .getResourceAsStream(DEFAULT_CONFIGURATION_RESOURCE);

            String defaultConfigurationString = IOUtils.toString(defaultConfigurationStream, Charset.defaultCharset());

            return readFromString(configuration, defaultConfigurationString);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to read default configuration", e);
        }
    }

    private ApplicationConfiguration readFromString(ApplicationConfiguration configuration, String configurationString)
    {
        if (StringUtils.isBlank(configurationString))
        {
            return configuration;
        }

        return readFromArgs(configuration, configurationString.split("\\r?\\n"));
    }

    private ApplicationConfiguration readFromArgs(ApplicationConfiguration configuration, String[] args)
    {
        if (args == null)
        {
            return configuration;
        }

        for (String arg : args)
        {
            String[] configurationParameter = arg.split("=");

            if (configurationParameter.length == 2)
            {
                configuration.putParameter(configurationParameter[0], configurationParameter[1]);
            }
        }

        return configuration;
    }
}
