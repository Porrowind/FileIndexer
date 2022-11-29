package solo.egorov.file_indexer.core.generator;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

public class GeneratedTextConfig
{
    public static final String ALPHABET_ENGLISH_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALPHABET_ENGLISH_LOWER = "abcdefghijklmnopqrstuvwxyz";
    public static final String ALPHABET_ENGLISH = ALPHABET_ENGLISH_LOWER + ALPHABET_ENGLISH_UPPER;
    public static final String ALPHABET_NUMBERS = "0123456789";

    private String alphabet = ALPHABET_ENGLISH;
    private String separators = StringUtils.SPACE;

    private int minTokenLength = 5;
    private int maxTokenLength = 10;
    private int randomTokensCount;
    private String[] randomTokensPool;

    private ArrayList<GeneratedTextConfigToken> requiredTokens = new ArrayList<>();
    private int requiredTokensCount;

    public String getAlphabet()
    {
        return alphabet;
    }

    public GeneratedTextConfig setAlphabet(String alphabet)
    {
        this.alphabet = alphabet;
        return this;
    }

    public String getSeparators()
    {
        return separators;
    }

    public GeneratedTextConfig setSeparators(String separators)
    {
        this.separators = separators;
        return this;
    }

    public int getMinTokenLength()
    {
        return minTokenLength;
    }

    public GeneratedTextConfig setMinTokenLength(int minTokenLength)
    {
        this.minTokenLength = minTokenLength;
        return this;
    }

    public int getMaxTokenLength()
    {
        return maxTokenLength;
    }

    public GeneratedTextConfig setMaxTokenLength(int maxTokenLength)
    {
        this.maxTokenLength = maxTokenLength;
        return this;
    }

    public int getRandomTokensCount()
    {
        return randomTokensCount;
    }

    public GeneratedTextConfig setRandomTokensCount(int randomTokensCount)
    {
        this.randomTokensCount = randomTokensCount;
        return this;
    }

    public String[] getRandomTokensPool()
    {
        return randomTokensPool;
    }

    public GeneratedTextConfig setRandomTokensPool(String[] randomTokensPool)
    {
        this.randomTokensPool = randomTokensPool;
        return this;
    }

    public ArrayList<GeneratedTextConfigToken> getRequiredTokens()
    {
        return requiredTokens;
    }

    public GeneratedTextConfig addRequiredToken(GeneratedTextConfigToken token)
    {
        this.requiredTokensCount += token.getCount();
        this.requiredTokens.add(token);
        return this;
    }

    public GeneratedTextConfig addRequiredTokens(Collection<GeneratedTextConfigToken> tokens)
    {
        tokens.forEach(this::addRequiredToken);
        return this;
    }

    public int getRequiredTokensCount()
    {
        return requiredTokensCount;
    }
}
