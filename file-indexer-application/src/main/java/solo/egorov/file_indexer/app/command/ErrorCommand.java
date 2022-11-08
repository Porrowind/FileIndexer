package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;

import java.util.Map;

class ErrorCommand extends AbstractCommand
{
    @Override
    ActionResult executeSafe(ApplicationContext context, Map<String, String> args)
    {
        if (args != null && StringUtils.isNotBlank(args.get(TARGET)))
        {
            return ActionResult.failure("Error: " + args.get(TARGET));
        }

        return ActionResult.success();
    }
}
