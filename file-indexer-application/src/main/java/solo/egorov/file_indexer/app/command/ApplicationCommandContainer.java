package solo.egorov.file_indexer.app.command;

import java.util.Map;

public class ApplicationCommandContainer
{
    private final ApplicationCommand command;
    private final Map<String, String> commandArgs;

    ApplicationCommandContainer(ApplicationCommand command, Map<String, String> commandArgs)
    {
        this.command = command;
        this.commandArgs = commandArgs;
    }

    public ApplicationCommand getCommand()
    {
        return command;
    }

    public Map<String, String> getCommandArgs()
    {
        return commandArgs;
    }
}
