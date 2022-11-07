package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.app.file.FileTraveler;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class IndexCommand extends AbstractCommand
{
    private static final String TARGET = "TARGET";

    @Override
    public ActionResult execute(ApplicationContext context, Map<String, String> args)
    {
        FileIndexer fileIndexer = context.getFileIndexer();
        FileTraveler fileTraveler = context.getFileTraveler();

        String path = StringUtils.isNotBlank(args.get(TARGET))
            ? fileTraveler.resolvePath(args.get(TARGET))
            : fileTraveler.getCurrentPath();

        File f = new File(path);

        if (!f.exists())
        {
            return ActionResult.failure("File not found: " + path);
        }

        fileIndexer.index(
            new FileIndexerOptions()
                .setPath(f.getAbsolutePath())
                .setRecursiveIndex(true)
                .setRecursiveIndexDepth(3)
        );

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
        return ActionResult.success();
    }
}
