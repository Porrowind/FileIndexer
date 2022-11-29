package solo.egorov.file_indexer.core.event;

/**
 * Event raised when the file is added to index
 */
public class FileAddedEvent extends FileProcessedEvent
{
    public FileAddedEvent(String path)
    {
        super(path);
    }
}