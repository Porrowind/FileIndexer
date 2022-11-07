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
     */
    void add(Document document);

    /**
     * Get document hash from storage
     *
     * @param uri Document external identifier
     * @return document hash if found
     */
    byte[] getDocumentHash(String uri);

    /**
     * Find documents by query
     *
     * @param storageQuery Query to search
     * @return List of matching documents if any
     */
    List<Document> get(IndexStorageQuery storageQuery);

    /**
     * Delete document from storage
     * @param uri Document external identifier
     */
    void delete(String uri);

    /**
     * Run storage cleanup
     */
    void cleanup();
}
