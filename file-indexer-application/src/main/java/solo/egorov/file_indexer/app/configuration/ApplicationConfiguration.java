package solo.egorov.file_indexer.app.configuration;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ApplicationConfiguration
{
    private static final String CONFIGURATION_PATH = "configurationPath";
    private static final String STOP_WORDS_PATH = "stopWordsPath";
    private static final String WORKER_THREADS_COUNT = "workerThreadsCount";
    private static final String MIN_TOKEN_LENGTH = "minTokenLength";
    private static final String MAX_TOKEN_LENGTH = "maxTokenLength";
    private static final String MAX_FILE_SIZE = "maxFileSize";
    private static final String PROCESS_HIDDEN_FILES = "processHiddenFiles";
    private static final String PROCESS_FILES_WITH_NO_EXTENSION = "processFilesWithNoExtension";
    private static final String PROCESS_FILES_WITH_UNKNOWN_EXTENSION = "processFilesWithUnknownExtension";
    private static final String CHARSET = "charset";
    private static final String WATCHER_ENABLED = "watcher.enabled";
    private static final String WATCHER_TIMEOUT = "watcher.timeout";
    private static final String WATCHER_FILE_TIMEOUT = "watcher.fileTimeout";

    private final Map<String, String> parameters = new HashMap<>();

    public ApplicationConfiguration putParameter(String key, String value)
    {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value))
        {
            parameters.put(key, value);
        }

        return this;
    }

    public ApplicationConfiguration putParameters(Map<String, String> parameters)
    {
        if (parameters != null)
        {
            parameters.forEach(this::putParameter);
        }

        return this;
    }

    public String getConfigurationPath()
    {
        return getString(CONFIGURATION_PATH);
    }

    public String getStopWordsPath()
    {
        return getString(CONFIGURATION_PATH);
    }

    public Integer getWorkerThreadsCount()
    {
        return getInteger(WORKER_THREADS_COUNT);
    }

    public Integer getMinTokenLength()
    {
        return getInteger(MIN_TOKEN_LENGTH);
    }

    public Integer getMaxTokenLength()
    {
        return getInteger(MAX_TOKEN_LENGTH);
    }

    public Long getMaxFileSize()
    {
        return getLong(MAX_FILE_SIZE);
    }

    public Boolean isProcessHiddenFiles()
    {
        return getBoolean(PROCESS_HIDDEN_FILES);
    }

    public Boolean isProcessFilesWithNoExtension()
    {
        return getBoolean(PROCESS_FILES_WITH_NO_EXTENSION);
    }

    public Boolean isProcessFilesWithUnknownExtension()
    {
        return getBoolean(PROCESS_FILES_WITH_UNKNOWN_EXTENSION);
    }

    public String getCharset()
    {
        return getString(CHARSET);
    }

    public Boolean isWatcherEnabled()
    {
        return getBoolean(WATCHER_ENABLED);
    }

    public Long getWatcherTimeout()
    {
        return getLong(WATCHER_TIMEOUT);
    }

    public Long getWatcherFileTimeout()
    {
        return getLong(WATCHER_FILE_TIMEOUT);
    }

    private String getString(String key)
    {
        String value = StringUtils.trim(parameters.get(key));

        if (StringUtils.isEmpty(value))
        {
            return null;
        }

        return value;
    }

    private Boolean getBoolean(String key)
    {
        return BooleanUtils.toBooleanObject(getString(key));
    }

    private Integer getInteger(String key)
    {
        try
        {
            return Integer.parseInt(getString(key));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private Long getLong(String key)
    {
        try
        {
            return Long.parseLong(getString(key));
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
