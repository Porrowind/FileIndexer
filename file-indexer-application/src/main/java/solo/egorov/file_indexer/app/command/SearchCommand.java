package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SearchCommand extends AbstractCommand
{
    private static final String TARGET = "TARGET";

    @Override
    public ActionResult execute(ApplicationContext context, Map<String, String> args)
    {
        FileIndexer fileIndexer = context.getFileIndexer();

        List<Document> documents = fileIndexer.search(new FileIndexerQuery().setSearchText(args.get(TARGET)));

        if (documents == null || documents.isEmpty())
        {
            return ActionResult.success("No matches found");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Documents found: ");
        for (Document document : documents)
        {
            sb.append("\n\t").append(document.getUri());
        }

        return ActionResult.success(sb.toString());
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
        if (args == null || StringUtils.isBlank(args.get(TARGET)))
        {
            return ActionResult.failure("No target provided");
        }

        return ActionResult.success();
    }
}
