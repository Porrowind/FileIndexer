package solo.egorov.file_indexer.core.query;

import java.util.ArrayList;
import java.util.List;

class PotentialMatch
{
    private final List<Long> allPositions = new ArrayList<>();

    private long currentPosition;

    public PotentialMatch(long currentPosition)
    {
        this.currentPosition = currentPosition;
        this.allPositions.add(currentPosition);
    }

    public PotentialMatch setCurrentPosition(long currentPosition)
    {
        this.currentPosition = currentPosition;
        this.allPositions.add(currentPosition);

        return this;
    }

    public long getCurrentPosition()
    {
        return currentPosition;
    }

    public long getFirstPosition()
    {
        return allPositions.get(0);
    }

    public long getLastPosition()
    {
        return currentPosition;
    }
}
