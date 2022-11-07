package solo.egorov.file_indexer.core.storage.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class TokenContainer
{
    private final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    private List<IndexRecord> tokenRecords = new ArrayList<>();

    public List<IndexRecord> get()
    {
        LOCK.readLock().lock();
        List<IndexRecord> copy = new ArrayList<>(tokenRecords);
        LOCK.readLock().unlock();

        return copy;
    }

    public TokenContainer add(IndexRecord indexRecord)
    {
        LOCK.writeLock().lock();
        this.tokenRecords.add(indexRecord);
        LOCK.writeLock().unlock();
        return this;
    }

    public TokenContainer addAll(Collection<IndexRecord> indexRecords)
    {
        LOCK.writeLock().lock();
        this.tokenRecords.addAll(indexRecords);
        LOCK.writeLock().unlock();
        return this;
    }

    public TokenContainer remove(IndexRecord indexRecord)
    {
        LOCK.writeLock().lock();
        this.tokenRecords.remove(indexRecord);
        LOCK.writeLock().unlock();
        return this;
    }

    public TokenContainer removeAll(Collection<IndexRecord> indexRecords)
    {
        LOCK.writeLock().lock();
        this.tokenRecords.removeAll(indexRecords);
        LOCK.writeLock().unlock();
        return this;
    }

    public TokenContainer set(List<IndexRecord> tokenRecords)
    {
        if (tokenRecords == null)
        {
            tokenRecords = Collections.emptyList();
        }

        LOCK.writeLock().lock();
        this.tokenRecords = tokenRecords;
        LOCK.writeLock().unlock();
        return this;
    }
}
