package solo.egorov.file_indexer.core.default_impl.event;

import solo.egorov.file_indexer.core.event.FileIndexerEvent;

import java.util.Set;

class FileIndexerEventWrapper
{
    private final FileIndexerEvent event;
    private final Set<FileIndexerEventHandlerReference> eventHandlers;

    public FileIndexerEventWrapper(FileIndexerEvent event, Set<FileIndexerEventHandlerReference> eventHandlers)
    {
        this.event = event;
        this.eventHandlers = eventHandlers;
    }

    public FileIndexerEvent getEvent()
    {
        return event;
    }

    public Set<FileIndexerEventHandlerReference> getEventHandlers()
    {
        return eventHandlers;
    }
}
