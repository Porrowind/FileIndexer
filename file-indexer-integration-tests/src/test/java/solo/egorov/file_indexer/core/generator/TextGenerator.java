package solo.egorov.file_indexer.core.generator;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

public class TextGenerator
{
    public static String generateText(GeneratedTextConfig config)
    {
        int totalTokensCount = config.getRandomTokensCount() + config.getRequiredTokensCount();

        int currentRandomToken = 0;
        int currentRequiredToken = 0;
        int currentRequiredTokenEntry = 0;
        int currentRequiredTokenEntryCounter = 0;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalTokensCount; i++)
        {
            if (i != 0)
            {
                sb.append(generateSeparator(config));
            }

            if ((i % 2 == 0 && currentRandomToken < config.getRandomTokensCount())
                || currentRequiredToken >= config.getRequiredTokensCount())
            {
                sb.append(generateToken(config));
                currentRandomToken++;
            }
            else if ((i % 2 != 0 && currentRequiredToken < config.getRequiredTokensCount())
                || currentRandomToken >= config.getRandomTokensCount())
            {
                GeneratedTextConfigToken requiredToken = config.getRequiredTokens().get(currentRequiredTokenEntry);
                sb.append(requiredToken.getText());

                if (currentRequiredTokenEntryCounter < requiredToken.getCount() - 1)
                {
                    currentRequiredTokenEntryCounter++;
                }
                else
                {
                    currentRequiredTokenEntry++;
                    currentRequiredTokenEntryCounter = 0;
                }

                currentRequiredToken++;
            }
        }

        return sb.toString();
    }

    public static String[] generateTokenPool(GeneratedTextConfig config)
    {
        String[] result = new String[config.getRandomTokensCount()];
        for (int i = 0; i < config.getRandomTokensCount(); i++)
        {
            result[i] = generateToken(config);
        }
        return result;
    }

    private static String generateSeparator(GeneratedTextConfig config)
    {
        if (StringUtils.isEmpty(config.getSeparators()))
        {
            return StringUtils.EMPTY;
        }

        return RandomStringUtils.random(1, config.getSeparators());
    }

    private static String generateToken(GeneratedTextConfig config)
    {
        if (config.getRandomTokensPool() != null)
        {
            return config.getRandomTokensPool()[RandomUtils.nextInt(0, config.getRandomTokensPool().length)];
        }

        return RandomStringUtils.random(
            RandomUtils.nextInt(config.getMinTokenLength(), config.getMaxTokenLength() + 1),
            config.getAlphabet()
        );
    }
}
