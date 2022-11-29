package solo.egorov.file_indexer.core.event;

/**
 * Interface for event handling
 */
public interface FileIndexerEventHandler
{
    /**
     * Handle event
     *
     * @param event event to handle
     */
    void handle(FileIndexerEvent event);

    /**
     * Return event type class which this handler is able to process
     * @param <E>
     * @return
     */
    <E extends FileIndexerEvent> Class<E> getHandledEventClass();

    /**
     * Check if handler can handle this type of event
     *
     * @param event event to handle
     * @return true when can handle, false - otherwise
     */
    default boolean canHandle(FileIndexerEvent event)
    {
        return event != null && getHandledEventClass().isInstance(event);
    }
}
