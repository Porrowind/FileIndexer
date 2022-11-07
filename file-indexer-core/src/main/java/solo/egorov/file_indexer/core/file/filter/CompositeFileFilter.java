package solo.egorov.file_indexer.core.file.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Filter the files with multiple filters
 * Runs each of internal filters, if all accepts the file - then accept
 */
public class CompositeFileFilter implements FileFilter
{
    private final List<FileFilter> filters;

    public CompositeFileFilter()
    {
        this(new ArrayList<>());
    }

    public CompositeFileFilter(List<FileFilter> filters)
    {
        this.filters = filters;
    }

    @Override
    public boolean isAccepted(String path)
    {
        for (FileFilter filter : filters)
        {
            if (!filter.isAccepted(path))
            {
                return false;
            }
        }

        return true;
    }

    public CompositeFileFilter addFilter(FileFilter filter)
    {
        if (filter != null)
        {
            filters.add(filter);
        }

        return this;
    }

    public CompositeFileFilter addFilters(Collection<FileFilter> filters)
    {
        if (filters != null && !filters.isEmpty())
        {
            filters.forEach(this::addFilter);
        }

        return this;
    }
}
