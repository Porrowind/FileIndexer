package solo.egorov.file_indexer.core.default_impl.event;

import solo.egorov.file_indexer.core.event.FileIndexerEventHandler;

import java.lang.ref.WeakReference;

class FileIndexerEventHandlerReference extends WeakReference<FileIndexerEventHandler>
{
    private final int hash;

    public FileIndexerEventHandlerReference(FileIndexerEventHandler referent)
    {
        super(referent);
        hash = referent.hashCode();
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof FileIndexerEventHandlerReference))
        {
            return false;
        }

        Object t = this.get();
        Object u = ((FileIndexerEventHandlerReference)obj).get();
        if (t == u)
        {
            return true;
        }
        if (t == null || u == null)
        {
            return false;
        }
        return t.equals(u);
    }
}
