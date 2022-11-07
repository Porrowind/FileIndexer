package solo.egorov.file_indexer.core.tokenizer.filter.token;

/**
 * Interface for token filtering before adding to index
 */
public interface TokenFilter
{
    /**
     * Should the token be accepted
     *
     * @param s token raw text
     * @return true if token should be accepted, false - otherwise
     */
    boolean isAccepted(String s);
}
