package solo.egorov.file_indexer.core;

import solo.egorov.file_indexer.core.query.QueryProcessor;
import solo.egorov.file_indexer.core.storage.IndexStorage;

import java.util.List;

/**
 * Interface for file indexing
 */
public interface FileIndexer
{
    /**
     * Start index
     */
    void start();

    /**
     * Stop index
     */
    void stop();

    /**
     * Add file to index
     *
     * @param options Indexing parameters
     */
    void index(FileIndexerOptions options);

    /**
     * Delete file from index
     *
     * @param options Indexing parameters
     */
    void delete(FileIndexerOptions options);

    /**
     * Perform search in index
     *
     * @param query Searching query
     * @return List of {@link Document} containing the search text
     */
    List<Document> search(FileIndexerQuery query);

    /**
     * Perform search in index with custom {@link QueryProcessor}
     *
     * @param query Searching query
     * @param queryProcessor Query processor to use
     * @return List of {@link Document} containing the search text
     */
    List<Document> search(FileIndexerQuery query, QueryProcessor queryProcessor);

    void cleanup();
}
