package solo.egorov.file_indexer.core.default_impl;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

class DefaultFileIndexerQueue
{
    private final Deque<DefaultFileIndexerWorkerTask> queue;

    public DefaultFileIndexerQueue()
    {
        this.queue = new ConcurrentLinkedDeque<>();
    }

    public synchronized DefaultFileIndexerWorkerTask getNextTask()
    {
        if (queue.size() > 0)
        {
            return queue.pop();
        }

        return null;
    }

    public synchronized void addAddTask(String path)
    {
        DefaultFileIndexerWorkerTask task = DefaultFileIndexerWorkerTask.newAddTask(path);

        queue.remove(task);
        queue.add(task);
    }

    public synchronized void addDeleteTask(String path)
    {
        DefaultFileIndexerWorkerTask task = DefaultFileIndexerWorkerTask.newDeleteTask(path);

        queue.remove(task);
        queue.add(task);
    }
}
