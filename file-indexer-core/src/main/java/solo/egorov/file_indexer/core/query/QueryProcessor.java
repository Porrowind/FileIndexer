package solo.egorov.file_indexer.core.query;

import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.FileIndexerQuery;
import solo.egorov.file_indexer.core.storage.IndexStorage;
import solo.egorov.file_indexer.core.tokenizer.StringTokenizer;

import java.util.List;

/**
 * Interface for processing the queries
 */
public interface QueryProcessor
{
    /**
     * Process the query
     *
     * @param query Original query to process
     * @param storage Index storage
     * @param tokenizer Query tokenizer
     *
     * @return List of matching documents
     */
    List<Document> process(FileIndexerQuery query, IndexStorage storage, StringTokenizer tokenizer);
}
