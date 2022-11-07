package solo.egorov.file_indexer.app.configuration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ApplicationException;
import solo.egorov.file_indexer.core.tokenizer.filter.token.StopWordsTokenFilter;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class StopWordsTokenFilterBuider
{
    private static final String DEFAULT_STOP_WORDS_RESOURCE = "default_stop_words";

    public StopWordsTokenFilter build(ApplicationConfiguration configuration)
    {
        Set<String> stopWords = configuration != null && StringUtils.isNotBlank(configuration.getStopWordsPath())
            ? readStopWordsFromFile(configuration.getStopWordsPath())
            : readDefaultStopWords();

        return new StopWordsTokenFilter(stopWords);
    }

    private Set<String> readDefaultStopWords()
    {
        try
        {
            InputStream defaultStopWordsStream = this.getClass().getClassLoader()
                .getResourceAsStream(DEFAULT_STOP_WORDS_RESOURCE);

            String defaultStopWordsString = IOUtils.toString(defaultStopWordsStream, Charset.defaultCharset());

            return readFromString(defaultStopWordsString);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to read default stop words");
        }
    }

    private Set<String> readStopWordsFromFile(String path)
    {
        try
        {
            String stopWordsString = FileUtils.readFileToString(new File(path), Charset.defaultCharset());

            return readFromString(stopWordsString);
        }
        catch (Exception e)
        {
            throw new ApplicationException("Failed to read stop words from file: " + path);
        }
    }

    private Set<String> readFromString(String stopWords)
    {
        Set<String> result = new HashSet<>();

        if (StringUtils.isBlank(stopWords))
        {
            return result;
        }

        String[] stopWordsLines = stopWords.split("\\r?\\n");

        for (String stopWordLine : stopWordsLines)
        {
            result.add(StringUtils.lowerCase(StringUtils.trim(stopWordLine)));
        }

        return result;
    }
}
