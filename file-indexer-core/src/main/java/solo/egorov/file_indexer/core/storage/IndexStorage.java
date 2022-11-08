package solo.egorov.file_indexer.core.storage;

import solo.egorov.file_indexer.core.Document;

import java.util.List;

/**
 * Interface for index storage
 */
public interface IndexStorage
{
    /**
     * Add document to storage
     *
     * @param document Document to add
     * @throws IndexStorageException In case of any exception
     */
    void add(Document document) throws IndexStorageException;

    /**
     * Get document hash from storage
     *
     * @param uri Document external identifier
     * @return document hash if found
     * @throws IndexStorageException In case of any exception
     */
    byte[] getDocumentHash(String uri) throws IndexStorageException;

    /**
     * Find documents by query
     *
     * @param storageQuery Query to search
     * @return List of matching documents if any
     * @throws IndexStorageException In case of any exception
     */
    List<Document> get(IndexStorageQuery storageQuery) throws IndexStorageException;

    /**
     * Delete document from storage
     * @param uri Document external identifier
     * @throws IndexStorageException In case of any exception
     */
    void delete(String uri) throws IndexStorageException;

    /**
     * Run storage cleanup
     * @throws IndexStorageException In case of any exception
     */
    void cleanup() throws IndexStorageException;
}
