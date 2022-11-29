package solo.egorov.file_indexer.core.default_impl.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solo.egorov.file_indexer.core.event.FileIndexerEvent;
import solo.egorov.file_indexer.core.event.FileIndexerEventHandler;

import java.util.Set;

class DefaultFileIndexerEventBusWorker implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultFileIndexerEventBusWorker.class);

    private final DefaultFileIndexerEventQueue queue;

    private volatile boolean stopped = false;

    public DefaultFileIndexerEventBusWorker(DefaultFileIndexerEventQueue queue)
    {
        this.queue = queue;
    }

    @Override
    public void run()
    {
        while (!isStopped())
        {
            try
            {
                FileIndexerEventWrapper eventWrapper = queue.getNextEvent();

                if (eventWrapper != null
                    && eventWrapper.getEvent() != null
                    && eventWrapper.getEventHandlers() != null && !eventWrapper.getEventHandlers().isEmpty())
                {
                    FileIndexerEvent event = eventWrapper.getEvent();
                    Set<FileIndexerEventHandlerReference> eventHandlersReferences = eventWrapper.getEventHandlers();

                    for (FileIndexerEventHandlerReference eventHandlerReference : eventHandlersReferences)
                    {
                        if (isStopped())
                        {
                            return;
                        }

                        try
                        {
                            FileIndexerEventHandler eventHandler = eventHandlerReference.get();

                            if (eventHandler != null && eventHandler.canHandle(event))
                            {
                                eventHandler.handle(event);
                            }
                        }
                        catch (Exception e)
                        {
                            LOG.error("[IndexEventBusWorker]: " + e.getMessage(), e);
                        }
                    }
                }

                timeout();
            }
            catch (Exception e)
            {
                LOG.error("[IndexEventBusWorker]: " + e.getMessage(), e);
            }
        }
    }

    private void timeout() throws InterruptedException
    {
        Thread.sleep(5L);
    }

    private synchronized boolean isStopped()
    {
        return stopped;
    }

    public synchronized void stop()
    {
        this.stopped = true;
    }
}
