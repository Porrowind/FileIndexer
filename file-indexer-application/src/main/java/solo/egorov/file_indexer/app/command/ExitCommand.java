package solo.egorov.file_indexer.app.command;

import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;

import java.util.Map;

class ExitCommand extends AbstractCommand
{
    @Override
    public ActionResult execute(ApplicationContext context, Map<String, String> args)
    {
        context.getFileIndexer().stop();
        return ActionResult.success();
    }

    @Override
    public boolean isExitCommand()
    {
        return true;
    }
}
