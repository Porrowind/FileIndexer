package solo.egorov.file_indexer.core.event;

/**
 * Event raised when the file is deleted from index
 */
public class FileDeletedEvent extends FileProcessedEvent
{
    public FileDeletedEvent(String path)
    {
        super(path);
    }
}
