package solo.egorov.file_indexer.app.command;

import java.util.Map;

public class ResolvedApplicationCommand
{
    private final ApplicationCommand applicationCommand;
    private final Map<String, String> commandArgs;

    private ResolvedApplicationCommand(ApplicationCommand applicationCommand, Map<String, String> commandArgs)
    {
        this.applicationCommand = applicationCommand;
        this.commandArgs = commandArgs;
    }

    static ResolvedApplicationCommand withArgs(ApplicationCommand applicationCommand, Map<String, String> commandArgs)
    {
        return new ResolvedApplicationCommand(applicationCommand, commandArgs);
    }

    static ResolvedApplicationCommand noArgs(ApplicationCommand applicationCommand)
    {
        return withArgs(applicationCommand, null);
    }

    public ApplicationCommand getApplicationCommand()
    {
        return applicationCommand;
    }

    public Map<String, String> getCommandArgs()
    {
        return commandArgs;
    }
}
