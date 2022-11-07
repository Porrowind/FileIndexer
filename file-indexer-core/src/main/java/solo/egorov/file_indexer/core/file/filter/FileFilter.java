package solo.egorov.file_indexer.core.file.filter;

/**
 * Interface for filtering the files before processing
 */
public interface FileFilter
{
    /**
     * Should process the file or not
     *
     * @param path path to the file
     * @return true if should process, false - otherwise
     */
    boolean isAccepted(String path);
}
