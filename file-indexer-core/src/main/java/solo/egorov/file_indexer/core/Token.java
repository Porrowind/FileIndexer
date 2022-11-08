package solo.egorov.file_indexer.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private List<Long> positions = new ArrayList<>();

    public Token(String data)
    {
        this.data = data;
    }

    public Token(String data, long position)
    {
        this.data = data;
        this.positions.add(position);
    }

    public Token(String data, List<Long> positions)
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

    public List<Long> getPositions()
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
