package solo.egorov.file_indexer.core.tokenizer.filter.token;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter the token against the set of stop words
 */
public class StopWordsTokenFilter implements TokenFilter
{
    private final Set<String> stopWords;

    public StopWordsTokenFilter(Collection<String> stopWords)
    {
        this.stopWords = new HashSet<>(stopWords);
    }

    @Override
    public boolean isAccepted(String token)
    {
        return stopWords.contains(token);
    }
}
