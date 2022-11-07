package solo.egorov.file_indexer.core;

import java.io.Serializable;

/**
 * Represents indexed file
 */
public class Document implements Serializable
{
    /**
     * Document unique external identifier
     */
    private final String uri;

    /**
     * Document unique internal identifier
     */
    private long id;

    /**
     * Document hash sum
     */
    private byte[] hash;

    /**
     * Document index
     */
    private IndexedText dataIndex;

    public Document(String uri)
    {
        this.uri = uri;
    }

    public String getUri()
    {
        return uri;
    }

    public long getId()
    {
        return id;
    }

    public Document setId(long id)
    {
        this.id = id;
        return this;
    }

    public byte[] getHash()
    {
        return hash;
    }

    public Document setHash(byte[] hash)
    {
        this.hash = hash;
        return this;
    }

    public IndexedText getDataIndex()
    {
        return dataIndex;
    }

    public Document setDataIndex(IndexedText dataIndex)
    {
        this.dataIndex = dataIndex;
        return this;
    }
}
