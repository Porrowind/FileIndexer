package solo.egorov.file_indexer.core;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents single token
 */
public class Token
{
    /**
     * Token data
     */
    private String data;

    /**
     * Token positions
     */
    private Set<Long> positions = new TreeSet<>();

    public Token(String data)
    {
        this.data = data;
    }

    public Token(String data, long position)
    {
        this.data = data;
        this.positions.add(position);
    }

    public Token(String data, Collection<Long> positions)
    {
        this.data = data;
        this.positions.addAll(positions);
    }

    public String getData()
    {
        return data;
    }

    public Token setData(String data)
    {
        this.data = data;
        return this;
    }

    public Set<Long> getPositions()
    {
        return positions;
    }

    public Token addPosition(long position)
    {
        this.positions.add(position);
        return this;
    }

    public Token addPositions(Collection<Long> positions)
    {
        this.positions.addAll(positions);
        return this;
    }

    public Token removePosition(long position)
    {
        this.positions.remove(position);
        return this;
    }
}
