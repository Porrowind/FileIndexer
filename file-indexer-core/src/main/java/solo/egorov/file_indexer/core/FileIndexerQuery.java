package solo.egorov.file_indexer.core;

/**
 * Query to search in index
 */
public class FileIndexerQuery
{
    /**
     * Raw query text
     */
    private String searchText;

    /**
     * Should query tokens keep their order
     */
    private boolean strict = true;

    public FileIndexerQuery() {}

    public FileIndexerQuery(String searchText)
    {
        this.searchText = searchText;
    }

    public String getSearchText()
    {
        return searchText;
    }

    public FileIndexerQuery setSearchText(String searchText)
    {
        this.searchText = searchText;
        return this;
    }

    public boolean isStrict()
    {
        return strict;
    }

    public FileIndexerQuery setStrict(boolean strict)
    {
        this.strict = strict;
        return this;
    }
}
