package solo.egorov.file_indexer.app;

public class ActionResult
{
    private boolean success;
    private String message;

    private ActionResult(boolean success, String message)
    {
        this.success = success;
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

    public static ActionResult failure(String message)
    {
        return new ActionResult(false, message);
    }

    public static ActionResult failure()
    {
        return failure(null);
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
