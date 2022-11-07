package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.app.ApplicationException;

import java.util.Map;

public interface ApplicationCommand
{
    ActionResult execute(ApplicationContext context, Map<String, String> args) throws ApplicationException;

    default String getInfo()
    {
        return StringUtils.EMPTY;
    }
}
