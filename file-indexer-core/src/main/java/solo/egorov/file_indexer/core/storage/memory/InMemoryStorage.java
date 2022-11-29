package solo.egorov.file_indexer.core.storage.memory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.IndexedText;
import solo.egorov.file_indexer.core.Token;
import solo.egorov.file_indexer.core.storage.IndexStorage;
import solo.egorov.file_indexer.core.storage.IndexStorageException;
import solo.egorov.file_indexer.core.storage.IndexStorageQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link IndexStorage}
 * Allows search with wildcard queries
 */
public class InMemoryStorage implements IndexStorage
{
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryStorage.class);

    private static final int MAX_WILDCARD_KEY_MATCHES = 10;

    private final ReadWriteLock DOCUMENT_RECORDS_LOCK = new ReentrantReadWriteLock();
    private final ReadWriteLock INDEX_RECORDS_LOCK = new ReentrantReadWriteLock();
    private final ReadWriteLock CLEANUP_LOCK = new ReentrantReadWriteLock();

    private final DocumentIdGenerator documentIdGenerator = new DocumentIdGenerator();
    private final Map<Long, DocumentRecord> activeDocumentRecordsById = new HashMap<>();
    private final Map<String, DocumentRecord> activeDocumentRecordsByUri = new HashMap<>();
    private final List<DocumentRecord> deletedDocumentRecords = new ArrayList<>();
    private final Map<String, TokenContainer> indexContainer = new HashMap<>();

    @Override
    public void add(Document document) throws IndexStorageException
    {
        try
        {
            if (!validateNewDocument(document))
            {
                return;
            }

            DocumentRecord newDocumentRecord = new DocumentRecord(
                document.getUri(), documentIdGenerator.getNextId(), document.getHash(), DocumentState.NEW
            );

            DocumentRecord existingDocumentRecord;

            DOCUMENT_RECORDS_LOCK.writeLock().lock();
            existingDocumentRecord = activeDocumentRecordsByUri.get(newDocumentRecord.getUri());

            if (existingDocumentRecord != null)
            {
                existingDocumentRecord.setDocumentState(DocumentState.DELETED);
                activeDocumentRecordsById.remove(existingDocumentRecord.getDocumentId());
            }

            activeDocumentRecordsById.put(newDocumentRecord.getDocumentId(), newDocumentRecord);
            activeDocumentRecordsByUri.put(newDocumentRecord.getUri(), newDocumentRecord);
            DOCUMENT_RECORDS_LOCK.writeLock().unlock();

            if (existingDocumentRecord != null)
            {
                CLEANUP_LOCK.writeLock().lock();
                deletedDocumentRecords.add(existingDocumentRecord);
                CLEANUP_LOCK.writeLock().unlock();
            }

            for (Token token : document.getDataIndex().getAllTokens())
            {
                INDEX_RECORDS_LOCK.readLock().lock();
                TokenContainer tokenContainer = indexContainer.get(token.getData());
                INDEX_RECORDS_LOCK.readLock().unlock();

                if (tokenContainer == null)
                {
                    INDEX_RECORDS_LOCK.writeLock().lock();
                    tokenContainer = indexContainer.get(token.getData());

                    if (tokenContainer == null)
                    {
                        tokenContainer = new TokenContainer();
                        indexContainer.put(token.getData(), tokenContainer);
                    }
                    INDEX_RECORDS_LOCK.writeLock().unlock();
                }

                tokenContainer.add(new IndexRecord(newDocumentRecord.getDocumentId(), token.getPositions()));
            }

            DOCUMENT_RECORDS_LOCK.writeLock().lock();
            newDocumentRecord.setDocumentState(DocumentState.ACTIVE);
            DOCUMENT_RECORDS_LOCK.writeLock().unlock();
        }
        catch (Exception e)
        {
            throw new IndexStorageException("Failed to add document to the storage", e);
        }
    }

    @Override
    public Document get(String uri) throws IndexStorageException
    {
        try
        {
            Document result = null;

            DOCUMENT_RECORDS_LOCK.readLock().lock();
            DocumentRecord documentRecord = activeDocumentRecordsByUri.get(uri);

            if (documentRecord != null && documentRecord.getHash() != null)
            {
                result = new Document(uri)
                    .setId(documentRecord.getDocumentId())
                    .setHash(Arrays.copyOf(documentRecord.getHash(), documentRecord.getHash().length));
            }
            DOCUMENT_RECORDS_LOCK.readLock().unlock();

            return result;
        }
        catch (Exception e)
        {
            throw new IndexStorageException("Failed to get document hash from the storage", e);
        }
    }

    @Override
    public List<Document> search(IndexStorageQuery storageQuery) throws IndexStorageException
    {
        try
        {
            return storageQuery.isWildcard()
                ? processWildcardQuery(storageQuery)
                : processSimpleQuery(storageQuery);
        }
        catch (Exception e)
        {
            throw new IndexStorageException("Failed to get documents from the storage", e);
        }
    }

    @Override
    public void delete(String uri) throws IndexStorageException
    {
        try
        {
            DOCUMENT_RECORDS_LOCK.writeLock().lock();
            DocumentRecord existingDocumentRecord = activeDocumentRecordsByUri.get(uri);

            if (existingDocumentRecord != null)
            {
                existingDocumentRecord.setDocumentState(DocumentState.DELETED);
                activeDocumentRecordsByUri.remove(uri);
                activeDocumentRecordsById.remove(existingDocumentRecord.getDocumentId());
                deletedDocumentRecords.add(existingDocumentRecord);
            }
            DOCUMENT_RECORDS_LOCK.writeLock().unlock();
        }
        catch (Exception e)
        {
            throw new IndexStorageException("Failed to delete document from the storage", e);
        }
    }

    @Override
    public void cleanup() throws IndexStorageException
    {

        List<DocumentRecord> documentRecordsToCleanup = null;

        try
        {
            CLEANUP_LOCK.writeLock().lock();
            documentRecordsToCleanup = new ArrayList<>(deletedDocumentRecords);
            deletedDocumentRecords.clear();
            CLEANUP_LOCK.writeLock().unlock();

            Set<Long> documentIdsToCleanup = documentRecordsToCleanup.stream()
                .map(DocumentRecord::getDocumentId)
                .collect(Collectors.toSet());

            Set<String> keys = getKeys();

            for (String key : keys)
            {
                List<IndexRecord> recordsToRemove = new ArrayList<>();

                INDEX_RECORDS_LOCK.readLock().lock();
                TokenContainer tokenContainer = indexContainer.get(key);
                INDEX_RECORDS_LOCK.readLock().unlock();

                List<IndexRecord> indexRecords = tokenContainer.get();
                for (IndexRecord indexRecord : indexRecords)
                {
                    if (documentIdsToCleanup.contains(indexRecord.getDocumentId()))
                    {
                        recordsToRemove.add(indexRecord);
                    }
                }

                tokenContainer.removeAll(recordsToRemove);
            }
        }
        catch (Exception e)
        {
            if (documentRecordsToCleanup != null)
            {
                CLEANUP_LOCK.writeLock().lock();
                deletedDocumentRecords.addAll(documentRecordsToCleanup);
                CLEANUP_LOCK.writeLock().unlock();
            }

            throw new IndexStorageException("Failed to cleanup the storage", e);
        }
    }

    private boolean validateNewDocument(Document document)
    {
        return document != null
            && StringUtils.isNotBlank(document.getUri())
            && document.getDataIndex() != null;
    }

    private List<Document> processWildcardQuery(IndexStorageQuery storageQuery)
    {
        Set<String> keys = getKeys();
        List<String> matchingKeys = new ArrayList<>();

        for (String key : keys)
        {
            if (storageQuery.matches(key))
            {
                matchingKeys.add(key);

                if (matchingKeys.size() > MAX_WILDCARD_KEY_MATCHES)
                {
                    return new ArrayList<>();
                }
            }
        }

        Map<Long, Document> documentsMapping = new HashMap<>();
        for (String matchingKey : matchingKeys)
        {
            List<Document> documentsForKey = processSimpleQuery(new IndexStorageQuery(matchingKey));

            for (Document document : documentsForKey)
            {
                Document existingDocument = documentsMapping.get(document.getId());

                if (existingDocument == null)
                {
                    documentsMapping.put(
                        document.getId(),
                        new Document(document.getUri())
                            .setId(document.getId())
                            .setDataIndex(
                                new IndexedText().addToken(
                                    new Token(
                                        storageQuery.getToken(),
                                        document.getDataIndex().getToken(matchingKey).getPositions()
                                    )
                                )
                            )
                    );
                }
                else
                {
                    existingDocument.getDataIndex().getToken(storageQuery.getToken()).addPositions(document.getDataIndex().getToken(matchingKey).getPositions());
                }
            }
        }

        return new ArrayList<>(documentsMapping.values());
    }

    private List<Document> processSimpleQuery(IndexStorageQuery storageQuery)
    {
        List<Document> results = new ArrayList<>();

        INDEX_RECORDS_LOCK.readLock().lock();
        TokenContainer tokenContainer = indexContainer.get(storageQuery.getToken());
        INDEX_RECORDS_LOCK.readLock().unlock();

        if (tokenContainer == null)
        {
            return results;
        }

        List<IndexRecord> tokenRecords = tokenContainer.get();
        if (tokenRecords == null || tokenRecords.isEmpty())
        {
            return results;
        }

        for (IndexRecord indexRecord : tokenRecords)
        {
            DocumentRecord documentRecord = activeDocumentRecordsById.get(indexRecord.getDocumentId());

            if (documentRecord == null || documentRecord.getDocumentState() != DocumentState.ACTIVE)
            {
                continue;
            }

            Document document = new Document(documentRecord.getUri());
            IndexedText dataIndex = new IndexedText().addToken(
                new Token(storageQuery.getToken(), indexRecord.getPositions())
            );
            document.setDataIndex(dataIndex);
            document.setId(documentRecord.getDocumentId());

            results.add(document);
        }

        return results;
    }

    private Set<String> getKeys()
    {
        INDEX_RECORDS_LOCK.readLock().lock();
        Set<String> keys = new HashSet<>(indexContainer.keySet());
        INDEX_RECORDS_LOCK.readLock().unlock();
        return keys;
    }

    private static final class DocumentIdGenerator
    {
        private volatile long nextId = 1;

        synchronized long getNextId()
        {
            return nextId++;
        }
    }
}
