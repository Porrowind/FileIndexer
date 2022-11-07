package solo.egorov.file_indexer.core.query;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.FileIndexerQuery;
import solo.egorov.file_indexer.core.IndexedText;
import solo.egorov.file_indexer.core.Token;
import solo.egorov.file_indexer.core.storage.IndexStorage;
import solo.egorov.file_indexer.core.storage.IndexStorageQuery;
import solo.egorov.file_indexer.core.tokenizer.StringTokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default {@link QueryProcessor} implementation
 * Allows search with composite queries
 */
public class DefaultQueryProcessor implements QueryProcessor
{
    public List<Document> process(FileIndexerQuery query, IndexStorage storage, StringTokenizer tokenizer)
    {
        if (query == null || StringUtils.isBlank(query.getSearchText()))
        {
            return new ArrayList<>();
        }

        IndexedText indexedQuery = tokenizer.tokenize(query.getSearchText(), true);
        List<Token> queryTokens = new ArrayList<>(indexedQuery.getAllTokens());

        if (queryTokens.size() == 0)
        {
            return new ArrayList<>();
        }

        if (queryTokens.size() == 1 && queryTokens.get(0).getPositions().size() == 1)
        {
            Token queryToken = queryTokens.get(0);
            return storage.get(new IndexStorageQuery(queryToken.getData()));
        }

        return processCompositeQuery(indexedQuery, query, storage);
    }

    //TODO: Document with positions
    private List<Document> processCompositeQuery(IndexedText indexedQuery, FileIndexerQuery query, IndexStorage storage)
    {
        Map<Long, List<PotentialMatch>> potentialMatchesById = new HashMap<>();
        Map<Long, String> documentUriById = new HashMap<>();
        Map<Token, List<Document>> processedTokens = new HashMap<>();
        Token[] normalizedQueryTokens = new Token[(int)indexedQuery.getSize()];

        for (Token queryToken : indexedQuery.getAllTokens())
        {
            for (Long queryTokenPosition : queryToken.getPositions())
            {
                normalizedQueryTokens[queryTokenPosition.intValue()] = queryToken;
            }
        }

        for (int i = 0; i < normalizedQueryTokens.length; i++)
        {
            Token currentQueryToken = normalizedQueryTokens[i];

            if (i == 0)
            {
                List<Document> matchingDocuments = storage.get(new IndexStorageQuery(currentQueryToken.getData()));

                if (matchingDocuments.size() == 0)
                {
                    return new ArrayList<>();
                }

                processedTokens.put(currentQueryToken, matchingDocuments);

                for (Document matchingDocument : matchingDocuments)
                {
                    Token matchedToken = matchingDocument.getDataIndex().getToken(currentQueryToken.getData());

                    List<PotentialMatch> newPotentialMatches = new ArrayList<>(matchedToken.getPositions().size());
                    for (Long position : matchedToken.getPositions())
                    {
                        newPotentialMatches.add(new PotentialMatch(position));
                    }

                    potentialMatchesById.put(matchingDocument.getId(), newPotentialMatches);
                    documentUriById.put(matchingDocument.getId(), matchingDocument.getUri());
                }

                continue;
            }

            List<Document> matchingDocuments;
            if (processedTokens.containsKey(currentQueryToken))
            {
                matchingDocuments = processedTokens.get(currentQueryToken);
            }
            else
            {
                matchingDocuments = storage.get(new IndexStorageQuery(currentQueryToken.getData()));
                processedTokens.put(currentQueryToken, matchingDocuments);
            }

            if (matchingDocuments.size() == 0)
            {
                return new ArrayList<>();
            }

            Map<Long, List<PotentialMatch>> newPotentialMatchesById = new HashMap<>();
            for (Document matchingDocument : matchingDocuments)
            {
                List<PotentialMatch> currentPotentialMatches = potentialMatchesById.get(matchingDocument.getId());

                if (currentPotentialMatches == null)
                {
                    continue;
                }

                List<PotentialMatch> newPotentialMatches = new ArrayList<>(currentPotentialMatches.size());

                Set<Long> resultTokenPositions = matchingDocument.getDataIndex().getToken(currentQueryToken.getData()).getPositions();

                outer: for (Long resultTokenPosition : resultTokenPositions)
                {
                    for (PotentialMatch potentialMatch : currentPotentialMatches)
                    {
                        if (query.isStrict())
                        {
                            if (potentialMatch.getCurrentPosition() + 1 == resultTokenPosition)
                            {
                                newPotentialMatches.add(potentialMatch.setCurrentPosition(resultTokenPosition));
                            }
                        }
                        else
                        {
                            if (potentialMatch.getCurrentPosition() < resultTokenPosition)
                            {
                                newPotentialMatches.add(potentialMatch.setCurrentPosition(resultTokenPosition));
                                break outer;
                            }
                        }
                    }
                }

                if (!newPotentialMatches.isEmpty())
                {
                    newPotentialMatchesById.put(matchingDocument.getId(), newPotentialMatches);
                    documentUriById.put(matchingDocument.getId(), matchingDocument.getUri());
                }
            }

            potentialMatchesById = newPotentialMatchesById;

            if (potentialMatchesById.isEmpty())
            {
                return new ArrayList<>();
            }
        }

        List<Document> result = new ArrayList<>();

        for (Map.Entry<Long, List<PotentialMatch>> entry : potentialMatchesById.entrySet())
        {
            Document resultDocument = new Document(documentUriById.get(entry.getKey()));

            Token token = new Token(query.getSearchText());
            for (PotentialMatch match : entry.getValue())
            {
                token.addPosition(match.getFirstPosition());
            }

            result.add(
                resultDocument.setDataIndex(
                    new IndexedText().addToken(token)
                )
            );
        }

        return result;
    }
}
