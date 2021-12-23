import java.io.IOException;
import java.util.HashMap;

public interface IServer {

    public HashMap<String, Object> jobResult = null;
    
    public void stop();

    public void start() throws IOException;

    public void register(Class serviceInterface, Class impl);

    public boolean isRunning();

    public int getPort();
}
