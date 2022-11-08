package solo.egorov.file_indexer.app.command;

import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;

import java.util.Map;

public class InfoCommand extends AbstractCommand
{
    private final String info;

    public InfoCommand(String info)
    {
        this.info = info;
    }

    @Override
    ActionResult executeSafe(ApplicationContext context, Map<String, String> args)
    {
        System.out.print(info);
        return ActionResult.success();
    }
}
