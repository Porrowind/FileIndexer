package solo.egorov.file_indexer.core.storage.memory;

import java.util.HashSet;
import java.util.Set;

class IndexRecord
{
    private Long documentId;
    private Set<Long> positions = new HashSet<>();

    public IndexRecord() {}

    public IndexRecord(Long documentId, Set<Long> positions)
    {
        this.documentId = documentId;
        this.positions = positions;
    }

    public Long getDocumentId()
    {
        return documentId;
    }

    public IndexRecord setDocumentId(Long documentId)
    {
        this.documentId = documentId;
        return this;
    }

    public Set<Long> getPositions()
    {
        return positions;
    }

    public IndexRecord setPositions(Set<Long> positions)
    {
        this.positions = positions;
        return this;
    }
}
