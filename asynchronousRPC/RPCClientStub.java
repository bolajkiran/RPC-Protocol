import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RPCClientStub<T> {
    public static <T> T getRemoteProxyObj(final Class<?> serviceInterface, final InetSocketAddress addr) {
        // 1. Convert local interface calls to JDK's dynamic proxy to implement remote interface calls in the dynamic proxy
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Socket socket = null;
                        ObjectOutputStream output = null;
                        ObjectInputStream input = null;
                        try {
                            // 2. Create a Socket client to connect to the remote service provider at the specified address
                            socket = new Socket();
                            socket.connect(addr);

                            // 3. Send to the service provider after encoding the interface class, method name, parameter list, etc., required for a remote service call
                            output = new ObjectOutputStream(socket.getOutputStream());
                            output.writeInt(1);
                            output.writeUTF(serviceInterface.getName());
                            output.writeUTF(method.getName());
                            output.writeObject(method.getParameterTypes());
                            output.writeObject(args);

                            // 4. Synchronization blocking waits for the server to return to answer, and returns after getting the answer
                            input = new ObjectInputStream(socket.getInputStream());
                            System.out.println(input.readUTF());
                            return null;
                        } finally {
                            if (socket != null) socket.close();
                            if (output != null) output.close();
                            if (input != null) input.close();
                        }
                    }
                });
    }

    public static <T> T getRemoteProxyResultObj(final Class<?> serviceInterface, final InetSocketAddress addr) {
        // 1. Convert local interface calls to JDK's dynamic proxy to implement remote interface calls in the dynamic proxy
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Socket socket = null;
                        ObjectOutputStream output = null;
                        ObjectInputStream input = null;
                        try {
                            // 2. Create a Socket client to connect to the remote service provider at the specified address
                            socket = new Socket();
                            socket.connect(addr);

                            // 3. Send to the service provider after encoding the interface class, method name, parameter list, etc., required for a remote service call
                            output = new ObjectOutputStream(socket.getOutputStream());
                            output.writeInt(2);
                            output.writeUTF(serviceInterface.getName());
                            output.writeUTF(method.getName());
                            output.writeObject(method.getParameterTypes());
                            output.writeObject(args);

                            // 4. Synchronization blocking waits for the server to return to answer, and returns after getting the answer
                            input = new ObjectInputStream(socket.getInputStream());
                            return input.readObject();
                        } finally {
                            if (socket != null) socket.close();
                            if (output != null) output.close();
                            if (input != null) input.close();
                        }
                    }
                });
    }
}

