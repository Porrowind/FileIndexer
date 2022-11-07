package solo.egorov.file_indexer.core.tokenizer.filter.character;

/**
 * Interface for character filtering when tokenizing the text
 */
public interface CharacterFilter
{
    /**
     * Is character accepted or should be skipped
     *
     * @param ch character
     * @return false if should be skipped, true - otherwise
     */
    boolean isAccepted(char ch);

    /**
     * Is character a separator character
     *
     * @param ch character
     * @return true if character is a separator, false - otherwise
     */
    boolean isSeparator(char ch);
}
