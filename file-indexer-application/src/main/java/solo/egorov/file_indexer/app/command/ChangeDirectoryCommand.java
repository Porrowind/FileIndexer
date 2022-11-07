package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.app.file.FileTraveler;

import java.util.Map;

class ChangeDirectoryCommand extends AbstractCommand
{
    @Override
    public ActionResult execute(ApplicationContext context, Map<String, String> args)
    {
        FileTraveler fileTraveler = context.getFileTraveler();

        fileTraveler.cd(args.get(TARGET));
        return ActionResult.success();
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

    @Override
    public String getInfo()
    {
        return "cd - Change path to specified directory" +
            "\n\t<path> - Absolute or relative path to directory";
    }
}
