package solo.egorov.file_indexer.app.command;

import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.app.ApplicationException;
import solo.egorov.file_indexer.app.file.FileTraveler;

import java.io.IOException;
import java.util.Map;

public class ListDirectoryCommand extends AbstractCommand
{
    @Override
    ActionResult executeSafe(ApplicationContext context, Map<String, String> args) throws ApplicationException
    {
        try
        {
            FileTraveler fileTraveler = context.getFileTraveler();

            fileTraveler.listDirs()
                .forEach(dir -> System.out.println("<dir> " + dir));

            fileTraveler.listFiles()
                .forEach(System.out::println);

            return ActionResult.success();
        }
        catch (IOException ioe)
        {
            throw new ApplicationException(ioe);
        }
    }

    @Override
    public String getInfo()
    {
        return "dir - List current directory";
    }
}
