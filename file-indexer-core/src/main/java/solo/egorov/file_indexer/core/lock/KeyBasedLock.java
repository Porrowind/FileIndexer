package solo.egorov.file_indexer.core.lock;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple implementation for a shared key-based lock
 */
public class KeyBasedLock
{
    private final Set<String> lockedKeys = new HashSet<>();

    /**
     * Acquire lock
     *
     * @param key String key to lock
     * @return
     *      true  - if lock acquired
     *      false - if lock was already by somebody else
     */
    public synchronized boolean lock(String key)
    {
        if (lockedKeys.contains(key))
        {
            return false;
        }

        lockedKeys.add(key);

        return true;
    }

    /**
     * Release lock
     *
     * @param key String key to unlock
     */
    public synchronized void unlock(String key)
    {
        lockedKeys.remove(key);
    }
}
