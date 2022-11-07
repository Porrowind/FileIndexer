package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractCommand implements ApplicationCommand
{
    //TODO: use stream, "values"
    Map<String, String> parseArguments(String args)
    {
        Map<String, String> parsedArgs = new HashMap<>();

        if (StringUtils.isBlank(args))
        {
            return parsedArgs;
        }

        String[] splittedArgs = args.split(" ");
        for (String arg : splittedArgs)
        {
            String[] argPair = arg.split("=");

            if (argPair.length == 1)
            {
                parsedArgs.put(argPair[0], null);
            }
            else if (argPair.length == 2)
            {
                parsedArgs.put(argPair[0], argPair[1]);
            }
        }

        return parsedArgs;
    }

    ActionResult validateArguments(Map<String, String> args)
    {
        return ActionResult.success();
    }
}
