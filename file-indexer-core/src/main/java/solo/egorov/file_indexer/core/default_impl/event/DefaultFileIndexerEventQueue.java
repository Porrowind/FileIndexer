package solo.egorov.file_indexer.core.default_impl.event;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

class DefaultFileIndexerEventQueue
{
    private final Deque<FileIndexerEventWrapper> queue;

    public DefaultFileIndexerEventQueue()
    {
        this.queue = new ConcurrentLinkedDeque<>();
    }

    public synchronized FileIndexerEventWrapper getNextEvent()
    {
        if (queue.size() > 0)
        {
            return queue.pop();
        }

        return null;
    }

    public synchronized void publishEvent(FileIndexerEventWrapper event)
    {
        queue.add(event);
    }
}
