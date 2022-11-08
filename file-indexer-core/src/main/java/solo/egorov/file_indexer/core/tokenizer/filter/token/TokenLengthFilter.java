package solo.egorov.file_indexer.core.tokenizer.filter.token;

import org.apache.commons.lang3.StringUtils;

/**
 * Filter the token by it's sise
 */
public class TokenLengthFilter implements TokenFilter
{
    public static final int ANY_LENGTH_ACCEPTED = -1;

    private final int minLengthAccepted;
    private final int maxLengthAccepted;

    public TokenLengthFilter(int minLengthAccepted, int maxLengthAccepted)
    {
        this.minLengthAccepted = minLengthAccepted;
        this.maxLengthAccepted = maxLengthAccepted;
    }

    @Override
    public boolean isAccepted(String token)
    {
        int tokenLength = StringUtils.length(StringUtils.trim(token));

        return (minLengthAccepted < 0 || tokenLength >= minLengthAccepted)
            && (maxLengthAccepted < 0 || tokenLength < maxLengthAccepted);
    }
}
