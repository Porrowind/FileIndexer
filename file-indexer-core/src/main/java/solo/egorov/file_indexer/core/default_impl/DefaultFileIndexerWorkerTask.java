package solo.egorov.file_indexer.core.default_impl;

import java.util.Objects;

class DefaultFileIndexerWorkerTask
{
    public enum IndexWorkerTaskType
    {
        Add,
        Delete
    }

    private final String path;
    private final IndexWorkerTaskType taskType;
    private final long submittedTimestamp;

    private DefaultFileIndexerWorkerTask(IndexWorkerTaskType taskType, String path)
    {
        this.taskType = taskType;
        this.path = path;
        this.submittedTimestamp = System.currentTimeMillis();
    }

    public static DefaultFileIndexerWorkerTask newAddTask(String path)
    {
        return new DefaultFileIndexerWorkerTask(IndexWorkerTaskType.Add, path);
    }

    public static DefaultFileIndexerWorkerTask newDeleteTask(String path)
    {
        return new DefaultFileIndexerWorkerTask(IndexWorkerTaskType.Delete, path);
    }

    public String getPath()
    {
        return path;
    }

    public boolean isAddTask()
    {
        return taskType == IndexWorkerTaskType.Add;
    }

    public boolean isDeleteTask()
    {
        return taskType == IndexWorkerTaskType.Delete;
    }

    public long getSubmittedTimestamp()
    {
        return submittedTimestamp;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        DefaultFileIndexerWorkerTask that = (DefaultFileIndexerWorkerTask) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(path);
    }
}
