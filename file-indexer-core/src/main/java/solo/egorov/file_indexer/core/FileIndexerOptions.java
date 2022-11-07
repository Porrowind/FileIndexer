package solo.egorov.file_indexer.core;

/**
 * Options for indexing the file
 */
public class FileIndexerOptions
{
    /**
     * Path to the file
     */
    private String path;

    /**
     * In case the file to index is a folder. Should we perform recursive indexing of it's subfolders or not
     * Default is false
     */
    private boolean recursiveIndex = false;

    /**
     * Maximum depth for recursive indexing of folders
     * Default is 10
     */
    private int recursiveIndexDepth = 10;

    public FileIndexerOptions() {}

    public FileIndexerOptions(String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }

    public FileIndexerOptions setPath(String path)
    {
        this.path = path;
        return this;
    }

    public boolean isRecursiveIndex()
    {
        return recursiveIndex;
    }

    public FileIndexerOptions setRecursiveIndex(boolean recursiveIndex)
    {
        this.recursiveIndex = recursiveIndex;
        return this;
    }

    public int getRecursiveIndexDepth()
    {
        return recursiveIndexDepth;
    }

    public FileIndexerOptions setRecursiveIndexDepth(int recursiveIndexDepth)
    {
        this.recursiveIndexDepth = recursiveIndexDepth;
        return this;
    }
}
