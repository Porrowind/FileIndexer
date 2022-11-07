package solo.egorov.file_indexer.app;

import solo.egorov.file_indexer.app.file.FileTraveler;
import solo.egorov.file_indexer.core.FileIndexer;

public class ApplicationContext
{
    private FileTraveler fileTraveler;
    private FileIndexer fileIndexer;

    public FileTraveler getFileTraveler()
    {
        return fileTraveler;
    }

    public ApplicationContext setFileTraveler(FileTraveler fileTraveler)
    {
        this.fileTraveler = fileTraveler;
        return this;
    }

    public FileIndexer getFileIndexer()
    {
        return fileIndexer;
    }

    public ApplicationContext setFileIndexer(FileIndexer fileIndexer)
    {
        this.fileIndexer = fileIndexer;
        return this;
    }
}
