package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.app.file.FileTraveler;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerOptions;

import java.io.File;
import java.util.Map;

class DeleteCommand extends AbstractCommand
{
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

        FileIndexerOptions indexerOptions = new FileIndexerOptions()
            .setPath(f.getAbsolutePath());

        fileIndexer.delete(indexerOptions);

        return ActionResult.success();
    }

    @Override
    public ActionResult validateArguments(Map<String, String> args)
    {
        return ActionResult.success();
    }

    @Override
    public String getInfo()
    {
        return "delete - Remove files from the index" +
            "\n\t<path> - Absolute or relative path to remove from index; can be file or directory; leave empty if you want to index current path";
    }
}
