package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.app.ApplicationException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractCommand implements ApplicationCommand
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommand.class);

    static final String TARGET = "TARGET";

    @Override
    public ActionResult execute(ApplicationContext context, Map<String, String> args)
    {
        try
        {
            return executeSafe(context, args);
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred: " + e.getMessage(), e);
            return ActionResult.failure("Exception occurred: " + e.getMessage());
        }
    }

    abstract ActionResult executeSafe(ApplicationContext context, Map<String, String> args);

    Map<String, String> parseArguments(String args)
    {
        Map<String, String> parsedArgs = new HashMap<>();

        if (StringUtils.isBlank(args))
        {
            return parsedArgs;
        }

        try
        {
            StringReader sr = new StringReader(args);

            StringBuilder currentKey = new StringBuilder();
            StringBuilder currentValue = new StringBuilder();

            boolean processingKey = false;

            int ch;
            while ((ch = sr.read()) != -1)
            {
                if (ch == '-')
                {
                    if (StringUtils.isNotBlank(currentKey))
                    {
                        if (StringUtils.isNotBlank(currentValue))
                        {
                            parsedArgs.put(currentKey.toString(), currentValue.toString());
                        }
                        else
                        {
                            parsedArgs.put(currentKey.toString(), null);
                        }
                    }

                    processingKey = true;

                    currentKey = new StringBuilder();
                    currentValue = new StringBuilder();
                }
                else if (ch == '=')
                {
                    processingKey = false;
                }
                else if (ch == ' ')
                {
                    if (StringUtils.isNotBlank(currentKey))
                    {
                        if (StringUtils.isNotBlank(currentValue))
                        {
                            parsedArgs.put(currentKey.toString(), currentValue.toString());
                        }
                        else
                        {
                            parsedArgs.put(currentKey.toString(), null);
                        }

                        processingKey = false;

                        currentKey = new StringBuilder();
                        currentValue = new StringBuilder();
                    }
                    else
                    {
                        currentValue.append((char)ch);
                    }
                }
                else
                {
                    if (processingKey)
                    {
                        currentKey.append((char)ch);
                    }
                    else
                    {
                        currentValue.append((char)ch);
                    }
                }
            }

            if (StringUtils.isNotBlank(currentKey))
            {
                if (StringUtils.isNotBlank(currentValue))
                {
                    parsedArgs.put(currentKey.toString(), currentValue.toString());
                }
                else
                {
                    parsedArgs.put(currentKey.toString(), null);
                }
            }
            else if (StringUtils.isNotBlank(currentValue))
            {
                parsedArgs.put(TARGET, currentValue.toString());
            }
        }
        catch (IOException ioe)
        {
            throw new ApplicationException("Failed to parse command parameters");
        }

        return parsedArgs;
    }

    ActionResult validateArguments(Map<String, String> args)
    {
        return ActionResult.success();
    }

    Boolean parseBoolean(String value)
    {
        return BooleanUtils.toBooleanObject(StringUtils.trim(value));
    }

    Integer parseInteger(String value)
    {
        try
        {
            return Integer.parseInt(StringUtils.trim(value));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    Long parseLong(String value)
    {
        try
        {
            return Long.parseLong(StringUtils.trim(value));
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
