package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

class ErrorCommand extends AbstractCommand
{
    private static final String ERROR = "ERROR";

    @Override
    public ActionResult execute(ApplicationContext context, Map<String, String> args)
    {
        if (args != null && StringUtils.isNotBlank(args.get(ERROR)))
        {
            System.out.println("Error: " + args.get(ERROR));
        }

        return ActionResult.success();
    }

    @Override
    Map<String, String> parseArguments(String args)
    {
        Map<String, String> parsedArguments = new HashMap<>();
        parsedArguments.put(ERROR, args);
        return parsedArguments;
    }
}
