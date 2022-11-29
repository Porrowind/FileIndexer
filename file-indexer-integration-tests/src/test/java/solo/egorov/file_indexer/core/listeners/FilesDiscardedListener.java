package solo.egorov.file_indexer.core.listeners;

import solo.egorov.file_indexer.core.event.FileDiscardedEvent;
import solo.egorov.file_indexer.core.event.FileIndexerEvent;
import solo.egorov.file_indexer.core.event.FileIndexerEventHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FilesDiscardedListener implements FileIndexerEventHandler
{
    private final Set<String> filePaths;

    public FilesDiscardedListener(Set<String> filePaths)
    {
        this.filePaths = filePaths;
    }

    public FilesDiscardedListener(String... filePaths)
    {
        this.filePaths = new HashSet<String>() {{
            Arrays.stream(filePaths).forEach(this::add);
        }};
    }

    @Override
    public void handle(FileIndexerEvent event)
    {
        FileDiscardedEvent fileDiscardedEvent = (FileDiscardedEvent) event;

        onFileDiscarded(fileDiscardedEvent.getPath());
    }

    @Override
    public <E extends FileIndexerEvent> Class getHandledEventClass()
    {
        return FileDiscardedEvent.class;
    }

    private synchronized void onFileDiscarded(String filePath)
    {
        this.filePaths.remove(filePath);
    }

    public synchronized boolean isAllDiscarded()
    {
        return this.filePaths.size() == 0;
    }
}
