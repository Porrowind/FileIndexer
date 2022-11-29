package solo.egorov.file_indexer.core.event;

/**
 * {@link solo.egorov.file_indexer.core.FileIndexer} event bus
 */
public interface FileIndexerEventBus
{
    /**
     * Subscribe to event
     *
     * @param handler to handle events
     * @throws FileIndexerEventBusException in case subscription failed
     */
    void subscribe(FileIndexerEventHandler handler) throws FileIndexerEventBusException;

    /**
     * Unsubscribe from event
     *
     * @param handler used to handle events
     * @throws FileIndexerEventBusException in case unsubscription failed
     */
    void unsubscribe(FileIndexerEventHandler handler) throws FileIndexerEventBusException;
}
