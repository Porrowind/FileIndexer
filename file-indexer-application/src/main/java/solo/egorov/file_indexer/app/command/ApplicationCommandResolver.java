package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;
import solo.egorov.file_indexer.app.ApplicationContext;
import solo.egorov.file_indexer.app.ApplicationException;
import solo.egorov.file_indexer.app.configuration.ApplicationConfiguration;
import solo.egorov.file_indexer.app.file.FileTraveler;
import solo.egorov.file_indexer.core.default_impl.DefaultFileIndexer;
import solo.egorov.file_indexer.core.FileIndexer;
import solo.egorov.file_indexer.core.FileIndexerConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationCommandResolver
{
    private final ErrorCommand ERROR = new ErrorCommand();
    private final Map<String, AbstractCommand> AVAILABLE_COMMANDS;

    public ApplicationCommandResolver()
    {
        AVAILABLE_COMMANDS = new LinkedHashMap<>();
        AVAILABLE_COMMANDS.put("cd", new ChangeDirectoryCommand());
        AVAILABLE_COMMANDS.put("dir", new ListDirectoryCommand());
        AVAILABLE_COMMANDS.put("index", new IndexCommand());
        AVAILABLE_COMMANDS.put("search", new SearchCommand());
        AVAILABLE_COMMANDS.put("exit", new ExitCommand());

        StringBuilder info = new StringBuilder();
        for (Map.Entry<String, AbstractCommand> entry : AVAILABLE_COMMANDS.entrySet())
        {
            info.append(entry.getValue().getInfo());
        }
        AVAILABLE_COMMANDS.put("info", new InfoCommand(info.toString()));
    }

    public ResolvedApplicationCommand resolve(String command)
    {
        command = StringUtils.trim(command);

        if (StringUtils.isEmpty(command))
        {
            return error("Empty arguments");
        }

        String commandName = StringUtils.trim(StringUtils.substringBefore(command, StringUtils.SPACE)); //TODO
        String commandArgs = command.contains(StringUtils.SPACE)
            ? StringUtils.trim(StringUtils.substringAfter(command, StringUtils.SPACE))
            : StringUtils.EMPTY;

        AbstractCommand resolvedCommand = AVAILABLE_COMMANDS.get(commandName);
        if (resolvedCommand == null)
        {
            return error("Unknown command");
        }

        Map<String, String> parsedArgs = resolvedCommand.parseArguments(commandArgs);
        ActionResult validationResult = resolvedCommand.validateArguments(parsedArgs);
        if (!validationResult.isSuccess())
        {
            return error("Wrong arguments: " + validationResult.getMessage());
        }

        return ResolvedApplicationCommand.withArgs(resolvedCommand, parsedArgs);
    }

    private ResolvedApplicationCommand error(String message)
    {
        return ResolvedApplicationCommand.withArgs(ERROR, ERROR.parseArguments(message));
    }
}
