package solo.egorov.file_indexer.app.command;

import org.apache.commons.lang3.StringUtils;
import solo.egorov.file_indexer.app.ActionResult;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationCommandResolver
{
    private final ErrorCommand ERROR = new ErrorCommand();
    private final Map<String, AbstractCommand> AVAILABLE_COMMANDS = new LinkedHashMap<>();

    public ApplicationCommandResolver()
    {
        AVAILABLE_COMMANDS.put("cd", new ChangeDirectoryCommand());
        AVAILABLE_COMMANDS.put("dir", new ListDirectoryCommand());
        AVAILABLE_COMMANDS.put("index", new IndexCommand());
        AVAILABLE_COMMANDS.put("delete", new DeleteCommand());
        AVAILABLE_COMMANDS.put("search", new SearchCommand());
        AVAILABLE_COMMANDS.put("exit", new ExitCommand());

        StringBuilder info = new StringBuilder();
        for (Map.Entry<String, AbstractCommand> entry : AVAILABLE_COMMANDS.entrySet())
        {
            info.append(entry.getValue().getInfo());
            info.append('\n');
        }
        AbstractCommand infoCommand = new InfoCommand(info.toString());
        AVAILABLE_COMMANDS.put("info", infoCommand);
        AVAILABLE_COMMANDS.put("help", infoCommand);
    }

    public ApplicationCommandContainer resolve(String commandLine)
    {
        commandLine = StringUtils.trim(commandLine);

        if (StringUtils.isEmpty(commandLine))
        {
            return error("Empty arguments");
        }

        String commandName = StringUtils.trim(StringUtils.substringBefore(commandLine, StringUtils.SPACE));
        String commandArgs = commandLine.contains(StringUtils.SPACE)
            ? StringUtils.trim(StringUtils.substringAfter(commandLine, StringUtils.SPACE))
            : StringUtils.EMPTY;

        AbstractCommand command = AVAILABLE_COMMANDS.get(commandName);
        if (command == null)
        {
            return error("Unknown command");
        }

        Map<String, String> parsedArgs = command.parseArguments(commandArgs);
        ActionResult validationResult = command.validateArguments(parsedArgs);
        if (!validationResult.isSuccess())
        {
            return error("Wrong arguments: " + validationResult.getMessage());
        }

        return new ApplicationCommandContainer(command, parsedArgs);
    }

    private ApplicationCommandContainer error(String message)
    {
        return new ApplicationCommandContainer(ERROR, ERROR.parseArguments(message));
    }
}
