package solo.egorov.file_indexer.core.watcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IndexWatcherRegistry
{
    private final ReadWriteLock folderLock;
    private final ReadWriteLock fileLock;

    private final Map<String, IndexWatcherFolderRecord> folderRecords;
    private final Map<String, IndexWatcherFileRecord> fileRecords;

    public IndexWatcherRegistry()
    {
        this.folderLock = new ReentrantReadWriteLock();
        this.fileLock = new ReentrantReadWriteLock();

        this.folderRecords = new HashMap<>();
        this.fileRecords = new HashMap<>();
    }

    public List<String> getFolderPaths()
    {
        folderLock.readLock().lock();
        List<String> folderPaths = new ArrayList<>(folderRecords.keySet());
        folderLock.readLock().unlock();
        return folderPaths;
    }

    public IndexWatcherFolderRecord getFolderRecord(String path)
    {
        folderLock.readLock().lock();
        IndexWatcherFolderRecord record = folderRecords.get(path);
        folderLock.readLock().unlock();
        return record;
    }

    public void setFolderRecord(String path, IndexWatcherFolderRecord folderRecord)
    {
        folderLock.writeLock().lock();
        folderRecords.put(path, folderRecord);
        folderLock.writeLock().unlock();
    }

    public void deleteFolderRecord(String path)
    {
        folderLock.writeLock().lock();
        folderRecords.remove(path);
        folderLock.writeLock().unlock();
    }

    public List<String> getFilePaths()
    {
        fileLock.readLock().lock();
        List<String> filePaths = new ArrayList<>(fileRecords.keySet());
        fileLock.readLock().unlock();
        return filePaths;
    }

    public IndexWatcherFileRecord getFileRecord(String path)
    {
        fileLock.readLock().lock();
        IndexWatcherFileRecord record = fileRecords.get(path);
        fileLock.readLock().unlock();
        return record;
    }

    public void setFileRecord(String path, IndexWatcherFileRecord fileRecord)
    {
        fileLock.writeLock().lock();
        fileRecords.put(path, fileRecord);
        fileLock.writeLock().unlock();
    }

    public void deleteFileRecord(String path)
    {
        fileLock.writeLock().lock();
        fileRecords.remove(path);
        fileLock.writeLock().unlock();
    }
}
