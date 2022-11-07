package solo.egorov.file_indexer.core.text;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides {@link TextExtractor} implementations for different types of files
 */
public class TextExtractorFactory
{
    private static final TextExtractor TXT_EXTRACTOR = new TxtDocumentTextExtractor();
    private static final TextExtractor DOC_EXTRACTOR = new DocDocumentTextExtractor();
    private static final TextExtractor DOCX_EXTRACTOR = new DocxDocumentTextExtractor();

    private final Map<String, TextExtractor> registeredExtensions;

    private TextExtractor defaultExtractor = TXT_EXTRACTOR;

    private TextExtractorFactory()
    {
        registeredExtensions = new HashMap<>();
    }

    /**
     * Empty factory generator method
     *
     * @return Empty factory
     */
    public static TextExtractorFactory empty()
    {
        return new TextExtractorFactory();
    }

    /**
     * Generator method for a factory with a set of default extractors
     *
     * @return Default factory
     */
    public static TextExtractorFactory defaults()
    {
        return new TextExtractorFactory()
            .registerExtension("txt", TXT_EXTRACTOR)
            .registerExtension("doc", DOC_EXTRACTOR)
            .registerExtension("docx", DOCX_EXTRACTOR);
    }

    /**
     * Register extractor to file extension
     *
     * @param extension file extension
     * @param textExtractor text extractor to process the file extension
     */
    public TextExtractorFactory registerExtension(String extension, TextExtractor textExtractor)
    {
        if (StringUtils.isNotBlank(extension) && textExtractor != null)
        {
            registeredExtensions.put(StringUtils.trim(extension), textExtractor);
        }

        return this;
    }

    /**
     * Get set of registered file extensions
     *
     * @return Set of registered file extensions
     */
    public Set<String> getRegisteredExtensions()
    {
        return registeredExtensions.keySet();
    }

    /**
     * Get {@link TextExtractor} for file extension
     *
     * @param extension file extension
     * @return {@link TextExtractor} if registered for that extension, null otherwise
     */
    public TextExtractor getForFileExtension(String extension)
    {
        return registeredExtensions.get(extension);
    }

    /**
     * Get {@link TextExtractor} for file extension
     *
     * @param extension file extension
     * @return {@link TextExtractor} if registered for that extension, default extractor otherwise
     */
    public TextExtractor getForFileExtensionOrDefault(String extension)
    {
        TextExtractor extractor = getForFileExtension(extension);

        return extractor != null ? extractor : getDefaultExtractor();
    }

    /**
     * Set default extractor
     *
     * @param defaultExtractor New default extractor
     */
    public TextExtractorFactory setDefaultExtractor(TextExtractor defaultExtractor)
    {
        this.defaultExtractor = defaultExtractor;
        return this;
    }

    /**
     * Get default extractor
     *
     * @return Default Extractor
     */
    public TextExtractor getDefaultExtractor()
    {
        return defaultExtractor;
    }
}
