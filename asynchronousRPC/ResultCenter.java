import java.util.HashMap;

public class ResultCenter implements IResult{
    public static final ResultCenter instance = new ResultCenter();

    public static HashMap<String, Object> jobResult = null;

    public ResultCenter() {
    }
    public static ResultCenter getInstance() {
        return instance;
    }

    public void setJobResult(String method, Object result) {
        if (jobResult == null) {
            jobResult = new HashMap<>();
        }
        jobResult.put(method, result);
    }

    public HashMap<String, Object> getJobResult() {
        return jobResult;
    }


    @Override
    public Object getResult(String methodName) {
        return jobResult.get(methodName);
    }
}

