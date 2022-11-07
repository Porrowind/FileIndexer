package solo.egorov.file_indexer.core.storage.memory;

class DocumentRecord
{
    private String uri;
    private Long documentId;
    private byte[] hash;
    private DocumentState documentState;

    public DocumentRecord() {}

    public DocumentRecord(String uri,  Long documentId, byte[] hash, DocumentState documentState)
    {
        this.uri = uri;
        this.documentId = documentId;
        this.hash = hash;
        this.documentState = documentState;
    }

    public byte[] getHash()
    {
        return hash;
    }

    public DocumentRecord setHash(byte[] hash)
    {
        this.hash = hash;
        return this;
    }

    public String getUri()
    {
        return uri;
    }

    public DocumentRecord setUri(String uri)
    {
        this.uri = uri;
        return this;
    }

    public Long getDocumentId()
    {
        return documentId;
    }

    public DocumentRecord setDocumentId(Long documentId)
    {
        this.documentId = documentId;
        return this;
    }

    public DocumentState getDocumentState()
    {
        return documentState;
    }

    public DocumentRecord setDocumentState(DocumentState documentState)
    {
        this.documentState = documentState;
        return this;
    }
}
