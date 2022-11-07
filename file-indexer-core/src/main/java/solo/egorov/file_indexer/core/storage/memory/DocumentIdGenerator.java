package solo.egorov.file_indexer.core.storage.memory;

class DocumentIdGenerator
{
    private volatile long nextId = 1;

    synchronized long getNextId()
    {
        return nextId++;
    }
}
