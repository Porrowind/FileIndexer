package solo.egorov.file_indexer.core.tokenizer.filter.token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Filter the tokens with multiple filters
 * Runs each of internal filters, if all accepts the token - then accept
 */
public class CompositeTokenFilter implements TokenFilter
{
    private final List<TokenFilter> filters;

    public CompositeTokenFilter()
    {
        this(new ArrayList<>());
    }

    public CompositeTokenFilter(List<TokenFilter> filters)
    {
        this.filters = filters;
    }

    @Override
    public boolean isAccepted(String s)
    {
        for (TokenFilter filter : filters)
        {
            if (!filter.isAccepted(s))
            {
                return false;
            }
        }

        return true;
    }

    public CompositeTokenFilter addFilter(TokenFilter filter)
    {
        if (filter != null)
        {
            filters.add(filter);
        }

        return this;
    }

    public CompositeTokenFilter addFilters(Collection<TokenFilter> filters)
    {
        if (filters != null && !filters.isEmpty())
        {
            filters.forEach(this::addFilter);
        }

        return this;
    }
}
