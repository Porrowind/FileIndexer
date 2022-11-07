package solo.egorov.file_indexer.app.command;

import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.core.FileIndexer;

import java.util.Map;

class CleanupCommand extends AbstractCommand
{
    @Override
    public ActionResult execute(ApplicationContext context, Map<String, String> args)
    {
        FileIndexer fileIndexer = context.getFileIndexer();

        fileIndexer.cleanup();

        return ActionResult.success();
    }

    @Override
    public String getInfo()
    {
        return "cleanup - Cleanup the index memory";
    }
}
