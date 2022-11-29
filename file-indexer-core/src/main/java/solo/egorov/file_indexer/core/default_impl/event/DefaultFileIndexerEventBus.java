package solo.egorov.file_indexer.core.default_impl.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solo.egorov.file_indexer.core.FileIndexerException;
import solo.egorov.file_indexer.core.event.FileIndexerEvent;
import solo.egorov.file_indexer.core.event.FileIndexerEventBus;
import solo.egorov.file_indexer.core.event.FileIndexerEventBusConfiguration;
import solo.egorov.file_indexer.core.event.FileIndexerEventBusException;
import solo.egorov.file_indexer.core.event.FileIndexerEventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultFileIndexerEventBus implements FileIndexerEventBus
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultFileIndexerEventBus.class);

    private final FileIndexerEventBusConfiguration configuration;

    private final ReadWriteLock LOCK;

    private final EventIdGenerator eventIdGenerator;
    private final DefaultFileIndexerEventQueue eventQueue;
    private final Map<Class, Set<FileIndexerEventHandlerReference>> eventHandlers;

    private final List<DefaultFileIndexerEventBusWorker> eventBusWorkers;
    private final ExecutorService eventBusWorkersService;

    public DefaultFileIndexerEventBus(FileIndexerEventBusConfiguration configuration)
    {
        this.configuration = configuration;

        LOCK = new ReentrantReadWriteLock();

        eventIdGenerator = new EventIdGenerator();
        eventQueue = new DefaultFileIndexerEventQueue();
        eventHandlers = new HashMap<>();

        eventBusWorkers = new ArrayList<>();
        eventBusWorkersService = Executors.newFixedThreadPool(configuration.getWorkerThreadsCount());
    }

    public void start()
    {
        for (int i = 0; i < configuration.getWorkerThreadsCount(); i++)
        {
            DefaultFileIndexerEventBusWorker worker = new DefaultFileIndexerEventBusWorker(eventQueue);
            eventBusWorkers.add(worker);
            eventBusWorkersService.execute(worker);
        }
    }

    public void stop()
    {
        try
        {
            for (DefaultFileIndexerEventBusWorker eventBusWorker : eventBusWorkers)
            {
                eventBusWorker.stop();
            }

            eventBusWorkersService.shutdown();
            eventBusWorkersService.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (Exception e)
        {
            throw new FileIndexerException("Failed to stop index event workers", e);
        }
    }

    @Override
    public void subscribe(FileIndexerEventHandler handler)
        throws FileIndexerEventBusException
    {
        try
        {
            LOCK.writeLock().lock();
            Set<FileIndexerEventHandlerReference> newHandlers;
            Set<FileIndexerEventHandlerReference> existingHandlers = eventHandlers.get(handler.getHandledEventClass());

            if (existingHandlers == null)
            {
                newHandlers = new HashSet<>();
            }
            else
            {
                newHandlers = new HashSet<>(existingHandlers);
            }

            newHandlers.add(new FileIndexerEventHandlerReference(handler));
            eventHandlers.put(handler.getHandledEventClass(), newHandlers);
        }
        catch (Exception e)
        {
            throw new FileIndexerEventBusException("Failed to subscribe to event", e);
        }
        finally
        {
            try
            {
                LOCK.writeLock().unlock();
            }
            catch (Exception e)
            {
                LOG.error("Failed to release event bus lock", e);
            }
        }
    }

    @Override
    public void unsubscribe(FileIndexerEventHandler handler)
        throws FileIndexerEventBusException
    {
        try
        {
            LOCK.writeLock().lock();
            Set<FileIndexerEventHandlerReference> newHandlers;
            Set<FileIndexerEventHandlerReference> existingHandlers = eventHandlers.get(handler.getHandledEventClass());

            if (existingHandlers == null)
            {
                return;
            }
            else
            {
                newHandlers = new HashSet<>(existingHandlers);
            }

            newHandlers.remove(new FileIndexerEventHandlerReference(handler));
            eventHandlers.put(handler.getHandledEventClass(), newHandlers);
        }
        catch (Exception e)
        {
            throw new FileIndexerEventBusException("Failed to subscribe to event", e);
        }
        finally
        {
            try
            {
                LOCK.writeLock().unlock();
            }
            catch (Exception e)
            {
                LOG.error("Failed to release event bus lock", e);
            }
        }
    }

    public void publish(FileIndexerEvent event)
    {
        event.setId(eventIdGenerator.getNextId());
        event.setTimestamp(System.currentTimeMillis());

        eventQueue.publishEvent(new FileIndexerEventWrapper(event, eventHandlers.get(event.getClass())));
    }

    private static final class EventIdGenerator
    {
        private volatile long nextId = 1;

        synchronized long getNextId()
        {
            return nextId++;
        }
    }
}
