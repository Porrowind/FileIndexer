package solo.egorov.file_indexer.core.listeners;

import solo.egorov.file_indexer.core.event.FileDeletedEvent;
import solo.egorov.file_indexer.core.event.FileIndexerEvent;
import solo.egorov.file_indexer.core.event.FileIndexerEventHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FilesDeletedListener implements FileIndexerEventHandler
{
    private final Set<String> filePaths;

    public FilesDeletedListener(Set<String> filePaths)
    {
        this.filePaths = filePaths;
    }

    public FilesDeletedListener(String... filePaths)
    {
        this.filePaths = new HashSet<String>() {{
            Arrays.stream(filePaths).forEach(this::add);
        }};
    }

    @Override
    public void handle(FileIndexerEvent event)
    {
        FileDeletedEvent fileDeletedEvent = (FileDeletedEvent) event;

        onFileDeleted(fileDeletedEvent.getPath());
    }

    @Override
    public <E extends FileIndexerEvent> Class getHandledEventClass()
    {
        return FileDeletedEvent.class;
    }

    private synchronized void onFileDeleted(String filePath)
    {
        this.filePaths.remove(filePath);
    }

    public synchronized boolean isAllDeleted()
    {
        return this.filePaths.size() == 0;
    }
}
