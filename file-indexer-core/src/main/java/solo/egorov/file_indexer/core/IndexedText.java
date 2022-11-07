package solo.egorov.file_indexer.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Indexed text as a collection of {@link Token}
 */
public class IndexedText implements Serializable
{
    /**
     * Collection of a text tokens
     */
    private final Map<String, Token> container;

    /**
     * Are tokens keep they order or not
     */
    private final boolean ordered;

    /**
     * Amount of tokens in text
     */
    private long size;

    public IndexedText()
    {
        this(false);
    }

    public IndexedText(boolean ordered)
    {
        this.ordered = ordered;
        container = ordered ? new LinkedHashMap<>() : new HashMap<>();
    }

    public boolean isOrdered()
    {
        return ordered;
    }

    public IndexedText addToken(Token token)
    {
        if (token != null && token.getData() != null)
        {
            Token existingToken = container.get(token.getData());

            if (existingToken != null)
            {
                existingToken.addPositions(token.getPositions());
            }
            else
            {
                container.put(token.getData(), token);
            }

            size++;
        }

        return this;
    }

    public IndexedText addTokens(Collection<Token> tokens)
    {
        if (tokens != null && !tokens.isEmpty())
        {
            tokens.forEach(this::addToken);
        }

        return this;
    }

    public Collection<Token> getAllTokens()
    {
        return this.container.values();
    }

    public Token getToken(String data)
    {
        return container.get(data);
    }

    public boolean hasToken(String data)
    {
        return getToken(data) != null;
    }

    public long getSize()
    {
        return size;
    }
}
