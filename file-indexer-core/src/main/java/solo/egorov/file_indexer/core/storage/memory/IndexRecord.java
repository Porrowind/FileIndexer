package solo.egorov.file_indexer.core.storage.memory;

import java.util.List;

class IndexRecord
{
    private final Long documentId;
    private final List<Long> positions;

    public IndexRecord(Long documentId, List<Long> positions)
    {
        this.documentId = documentId;
        this.positions = positions;
    }

    public Long getDocumentId()
    {
        return documentId;
    }

    public List<Long> getPositions()
    {
        return positions;
    }
}
