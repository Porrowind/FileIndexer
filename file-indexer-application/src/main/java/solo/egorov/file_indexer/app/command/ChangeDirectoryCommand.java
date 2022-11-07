package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.app.file.FileTraveler;

import java.util.HashMap;
import java.util.Map;

class ChangeDirectoryCommand extends AbstractCommand
{
    private static final String TARGET = "TARGET";

    @Override
    public ActionResult execute(ApplicationContext context, Map<String, String> args)
    {
        FileTraveler fileTraveler = context.getFileTraveler();

        fileTraveler.cd(args.get(TARGET));
        return ActionResult.success();
    }

    @Override
    Map<String, String> parseArguments(String args)
    {
        Map<String, String> parsedArguments = new HashMap<>();
        parsedArguments.put(TARGET, args);
        return parsedArguments;
    }

    @Override
    public ActionResult validateArguments(Map<String, String> args)
    {
        if (args == null || StringUtils.isBlank(args.get(TARGET)))
        {
            return ActionResult.failure("No target provided");
        }

        return ActionResult.success();
    }
}
