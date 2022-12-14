package solo.egorov.file_indexer.app.command;

import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;

import java.util.Map;

class ExitCommand extends AbstractCommand
{
    @Override
    ActionResult executeSafe(ApplicationContext context, Map<String, String> args)
    {
        context.getFileIndexer().stop();
        return ActionResult.exit();
    }

    @Override
    public String getInfo()
    {
        return "exit - Finish all running processes and exit";
    }
}
