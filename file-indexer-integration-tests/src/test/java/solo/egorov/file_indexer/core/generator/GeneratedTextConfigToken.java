package solo.egorov.file_indexer.core.generator;

public class GeneratedTextConfigToken
{
    private final String text;
    private final int count;

    public GeneratedTextConfigToken(String text)
    {
        this(text, 1);
    }

    public GeneratedTextConfigToken(String text, int count)
    {
        this.text = text;
        this.count = count;
    }

    public String getText()
    {
        return text;
    }

    public int getCount()
    {
        return count;
    }
}
