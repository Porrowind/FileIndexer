package solo.egorov.file_indexer.core.generator;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class TextGeneratorTest
{
    @Test
    public void testRandomTokensOnly()
    {
        String generatedText = TextGenerator.generateText(
            new GeneratedTextConfig()
        );
        assertEquals(generatedText, StringUtils.EMPTY);

        generatedText = TextGenerator.generateText(
            new GeneratedTextConfig()
                .setRandomTokensCount(5)
        );
        assertEquals(StringUtils.split(generatedText, ' ').length, 5);
        assertEquals(StringUtils.split(generatedText, ',').length, 1);
        assertEquals(StringUtils.replaceChars(generatedText, GeneratedTextConfig.ALPHABET_ENGLISH + " ", ""), "");

        generatedText = TextGenerator.generateText(
            new GeneratedTextConfig()
                .setRandomTokensCount(10)
                .setSeparators(",")
        );
        assertEquals(StringUtils.split(generatedText, ' ').length, 1);
        assertEquals(StringUtils.split(generatedText, ',').length, 10);
        assertEquals(StringUtils.replaceChars(generatedText, GeneratedTextConfig.ALPHABET_ENGLISH + ",", ""), "");

        generatedText = TextGenerator.generateText(
            new GeneratedTextConfig()
                .setRandomTokensCount(10)
                .setSeparators(",")
                .setAlphabet(GeneratedTextConfig.ALPHABET_NUMBERS)
        );
        assertEquals(StringUtils.split(generatedText, ' ').length, 1);
        assertEquals(StringUtils.split(generatedText, ',').length, 10);
        assertEquals(StringUtils.replaceChars(generatedText, GeneratedTextConfig.ALPHABET_NUMBERS + ",", ""), "");

        generatedText = TextGenerator.generateText(
            new GeneratedTextConfig()
                .setRandomTokensCount(10)
                .setMinTokenLength(1)
                .setMaxTokenLength(1)
                .setSeparators("")
                .setAlphabet(GeneratedTextConfig.ALPHABET_NUMBERS)
        );
        assertTrue(StringUtils.isNumeric(generatedText));
        assertEquals(StringUtils.length(generatedText), 10);
    }

    @Test
    public void testRequiredTokensOnly()
    {
        String generatedText = TextGenerator.generateText(
            new GeneratedTextConfig()
                .addRequiredToken(new GeneratedTextConfigToken("Some Test"))
        );
        assertEquals(generatedText, "Some Test");

        generatedText = TextGenerator.generateText(
            new GeneratedTextConfig()
                .addRequiredToken(new GeneratedTextConfigToken("Some Test", 2))
                .addRequiredToken(new GeneratedTextConfigToken("Another Test", 2))
        );
        assertEquals(generatedText, "Some Test Some Test Another Test Another Test");

        generatedText = TextGenerator.generateText(
            new GeneratedTextConfig()
                .addRequiredToken(new GeneratedTextConfigToken("SomeTest", 3))
                .addRequiredToken(new GeneratedTextConfigToken("AnotherTest"))
                .addRequiredToken(new GeneratedTextConfigToken("OneMoreTest", 3))
                .setSeparators(",")
        );
        assertEquals(generatedText, "SomeTest,SomeTest,SomeTest,AnotherTest,OneMoreTest,OneMoreTest,OneMoreTest");
    }

    @Test
    public void testAllTokens()
    {
        String generatedText = TextGenerator.generateText(
            new GeneratedTextConfig()
                .setRandomTokensCount(5)
                .setMinTokenLength(2)
                .setMaxTokenLength(2)
                .setAlphabet(GeneratedTextConfig.ALPHABET_NUMBERS)
                .addRequiredToken(new GeneratedTextConfigToken("SomeTest", 3))
                .addRequiredToken(new GeneratedTextConfigToken("AnotherTest", 2))
        );
        assertEquals(StringUtils.split(generatedText, ' ').length, 10);
        assertEquals(StringUtils.countMatches(generatedText, "SomeTest"), 3);
        assertEquals(StringUtils.countMatches(generatedText, "AnotherTest"), 2);

        String temp = StringUtils.replace(generatedText, "SomeTest", "");
        temp = StringUtils.replace(temp, "AnotherTest", "");
        temp = StringUtils.replace(temp, " ", "");
        assertEquals(StringUtils.length(temp), 10);
        assertTrue(StringUtils.isNumeric(temp));

        String[] splittedText = StringUtils.split(generatedText, " ");
        assertEquals(splittedText[1], "SomeTest");
        assertEquals(splittedText[3], "SomeTest");
        assertEquals(splittedText[5], "SomeTest");
        assertEquals(splittedText[7], "AnotherTest");
        assertEquals(splittedText[9], "AnotherTest");
    }
}
