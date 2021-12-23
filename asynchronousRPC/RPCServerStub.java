import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RPCServerStub implements IServer {
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static final HashMap<String, Class> serviceRegistry = new HashMap<String, Class>();

    private static boolean isRunning = false;

    private static int port;

    public RPCServerStub(int port) {
        this.port = port;
    }

    public void stop() {
        isRunning = false;
        executor.shutdown();
    }

    public void start() throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(port));
        System.out.println("start server");
        try {
            while (true) {
                // 1. Monitor client's TCP connection, encapsulate it as a task after receiving it, and execute by thread pool
                Socket client = server.accept();
                executor.execute(new ServiceTask(client));
            }
        } finally {
            server.close();
        }
    }

    public void register(Class serviceInterface, Class impl) {
        serviceRegistry.put(serviceInterface.getName(), impl);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getPort() {
        return port;
    }

    private static class ServiceTask implements Runnable {
        Socket client = null;

        public ServiceTask(Socket client) {
            this.client = client;
        }

        public void run() {
            ObjectOutputStream output = null;
            ObjectInputStream input = null;
            try {
                // 2. Deserialize the stream sent by the client into an object, reflect the caller to the service implementer, and get the execution result
                input = new ObjectInputStream(client.getInputStream());
                int serviceType = input.readInt();

                String serviceName = input.readUTF();
                String methodName = input.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                Object[] arguments = (Object[]) input.readObject();
                Class serviceClass = serviceRegistry.get(serviceName);
                if (serviceClass == null) {
                    throw new ClassNotFoundException(serviceName + " not found");
                }

                switch (serviceType) {
                    case 1:

                        System.out.println("Received serialized data from client for RPC: " + methodName);
                        System.out.println("Calling RPC: '" + methodName + "'.....");

                        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                            Object result;
                            try {
                                //TimeUnit.SECONDS.sleep(3);
                                Method method = serviceClass.getMethod(methodName, parameterTypes);
                                result = method.invoke(serviceClass.newInstance(), arguments);
                            } catch (Exception e) {
                                throw new IllegalStateException(e);
                            }
                            return result;
                        });
                        // 3. Send acknowledgment to Client that received the serialized data
                        output = new ObjectOutputStream(client.getOutputStream());
                        output.writeUTF("Received the serialized data for calling '" + methodName +"' RPC.");
                        ResultCenter resultService = ResultCenter.getInstance();
                        resultService.setJobResult(methodName, future.get());
                        break;
                    case 2:
                        System.out.println("Client is asking the result of RPC: " + arguments[0] + "...");

                        // 3. Send acknowledgment to Client that received the serialized data
                        output = new ObjectOutputStream(client.getOutputStream());
                        Method method = serviceClass.getMethod(methodName, parameterTypes);
                        Object result = method.invoke(serviceClass.newInstance(), arguments);
                        output.writeObject(result);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}

