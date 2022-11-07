package solo.egorov.file_indexer.core.tokenizer.filter.character;

/**
 * Default {@link CharacterFilter}
 */
public class DefaultCharacterFilter implements CharacterFilter
{
    @Override
    public boolean isAccepted(char ch)
    {
        return Character.isLetterOrDigit(ch);
    }

    @Override
    public boolean isSeparator(char ch)
    {
        return !isAccepted(ch);
    }
}
