package solo.egorov.file_indexer.core;

import solo.egorov.file_indexer.core.query.QueryProcessor;

import java.util.List;

/**
 * Interface for file indexing
 */
public interface FileIndexer
{
    /**
     * Start index
     *
     * @throws FileIndexerException in case of any exception
     */
    void start() throws FileIndexerException;

    /**
     * Stop index
     *
     * @throws FileIndexerException in case of any exception
     */
    void stop() throws FileIndexerException;

    /**
     * Add file to index
     *
     * @param options Indexing parameters
     * @throws FileIndexerException in case of any exception
     */
    void index(FileIndexerOptions options) throws FileIndexerException;

    /**
     * Delete file from index
     *
     * @param options Indexing parameters
     * @throws FileIndexerException in case of any exception
     */
    void delete(FileIndexerOptions options) throws FileIndexerException;

    /**
     * Perform search in index
     *
     * @param query Searching query
     * @return List of {@link Document} containing the search text
     * @throws FileIndexerException in case of any exception
     */
    List<Document> search(FileIndexerQuery query) throws FileIndexerException;

    /**
     * Perform search in index with custom {@link QueryProcessor}
     *
     * @param query Searching query
     * @param queryProcessor Query processor to use
     * @return List of {@link Document} containing the search text
     * @throws FileIndexerException in case of any exception
     */
    List<Document> search(FileIndexerQuery query, QueryProcessor queryProcessor) throws FileIndexerException;

    /**
     * Run index cleanup
     *
     * @throws FileIndexerException in case of any exception
     */
    void cleanup() throws FileIndexerException;
}
