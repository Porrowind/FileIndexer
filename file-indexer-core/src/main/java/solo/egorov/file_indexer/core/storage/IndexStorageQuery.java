package solo.egorov.file_indexer.core.storage;

import org.apache.commons.io.FilenameUtils;

/**
 * Query to search documents in storage
 * For wildcard queries:
 *   - Use * for a sequence of symbols
 */
public class IndexStorageQuery
{
    /**
     * Raw token text
     */
    private final String token;

    public IndexStorageQuery(String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return token;
    }

    public boolean isWildcard()
    {
        return token.contains("*");
    }

    public boolean matches(String s)
    {
        return FilenameUtils.wildcardMatch(s, token);
    }
}
