package solo.egorov.file_indexer.app;

public class ActionResult
{
    private final boolean success;
    private final boolean exit;
    private final String message;

    private ActionResult(boolean success, String message)
    {
        this(success, message, false);
    }

    private ActionResult(boolean success, String message, boolean exit)
    {
        this.success = success;
        this.exit = exit;
        this.message = message;
    }

    public static ActionResult success()
    {
        return new ActionResult(true, null);
    }

    public static ActionResult success(String message)
    {
        return new ActionResult(true, message);
    }

    public static ActionResult failure()
    {
        return failure(null);
    }

    public static ActionResult failure(String message)
    {
        return new ActionResult(false, message);
    }

    public static ActionResult exit()
    {
        return new ActionResult(true, null, true);
    }

    public boolean isSuccess()
    {
        return success;
    }

    public boolean isExit()
    {
        return exit;
    }

    public String getMessage()
    {
        return message;
    }
}
