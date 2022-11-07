package solo.egorov.file_indexer.core.tokenizer.filter.character;

/**
 * {@link CharacterFilter} wrapper for indexing queries
 */
public class QueryCharacterFilter implements CharacterFilter
{
    private final CharacterFilter primaryFilter;

    public QueryCharacterFilter(CharacterFilter primaryFilter)
    {
        this.primaryFilter = primaryFilter;
    }

    @Override
    public boolean isAccepted(char ch)
    {
        return primaryFilter.isAccepted(ch) || isWildcardSymbol(ch);
    }

    @Override
    public boolean isSeparator(char ch)
    {
        return primaryFilter.isSeparator(ch) && !isWildcardSymbol(ch);
    }

    private boolean isWildcardSymbol(char ch)
    {
        return ch == '*';
    }
}
