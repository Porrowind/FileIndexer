package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.core.Document;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerQuery;

import java.util.List;
import java.util.Map;

class SearchCommand extends AbstractCommand
{
    private static final String STRICT = "s";

    @Override
    ActionResult executeSafe(ApplicationContext context, Map<String, String> args)
    {
        FileIndexer fileIndexer = context.getFileIndexer();

        FileIndexerQuery query = new FileIndexerQuery()
            .setSearchText(args.get(TARGET));

        Boolean strict = parseBoolean(args.get(STRICT));
        if (strict != null)
        {
            query.setStrict(strict);
        }

        List<Document> documents = fileIndexer.search(query);

        if (documents == null || documents.isEmpty())
        {
            return ActionResult.success("No matches found");
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Document document : documents)
        {
            sb.append(first ? "\t" : "\n\t").append(document.getUri());
            first = false;
        }

        return ActionResult.success(sb.toString());
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

    @Override
    public String getInfo()
    {
        return "search - Search in index" +
            "\n\t-s=<true/false> - Keep words order strict" +
            "\n\t<searchText> - Text to search; can be multiple words; words can contain wildcards (*)";
    }
}
