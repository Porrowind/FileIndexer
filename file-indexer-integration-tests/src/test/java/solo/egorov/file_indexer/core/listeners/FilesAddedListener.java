package solo.egorov.file_indexer.core.listeners;

import solo.egorov.file_indexer.core.event.FileAddedEvent;
import solo.egorov.file_indexer.core.event.FileIndexerEvent;
import solo.egorov.file_indexer.core.event.FileIndexerEventHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FilesAddedListener implements FileIndexerEventHandler
{
    private final Set<String> filePaths;

    public FilesAddedListener(Set<String> filePaths)
    {
        this.filePaths = filePaths;
    }

    public FilesAddedListener(String... filePaths)
    {
        this.filePaths = new HashSet<String>() {{
            Arrays.stream(filePaths).forEach(this::add);
        }};
    }

    @Override
    public void handle(FileIndexerEvent event)
    {
        FileAddedEvent fileAddedEvent = (FileAddedEvent) event;

        onFileAdded(fileAddedEvent.getPath());
    }

    @Override
    public <E extends FileIndexerEvent> Class getHandledEventClass()
    {
        return FileAddedEvent.class;
    }

    private synchronized void onFileAdded(String filePath)
    {
        this.filePaths.remove(filePath);
    }

    public synchronized boolean isAllAdded()
    {
        return this.filePaths.size() == 0;
    }
}
