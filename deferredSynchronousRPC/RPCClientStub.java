import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class RPCClientStub<T> {
    public static <T> T getRemoteProxyObj(final Class<?> serviceInterface, final InetSocketAddress addr) {
        // 1. Convert local interface calls to JDK's dynamic proxy to implement remote interface calls in the dynamic proxy
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Socket socket = null;
                        ObjectOutputStream output = null;
                        ObjectInputStream input = null;
                        Thread t = null;
                        try {
                            // 2. Create a Socket client to connect to the remote service provider at the specified address
                            socket = new Socket();
                            socket.connect(addr);

                            // 3. Send to the service provider after encoding the interface class, method name, parameter list, etc., required for a remote service call
                            output = new ObjectOutputStream(socket.getOutputStream());
                            output.writeUTF(serviceInterface.getName());
                            output.writeUTF(method.getName());
                            output.writeObject(method.getParameterTypes());
                            output.writeObject(args);

                            // do some job
                             t = new Thread(new Runnable(){
                                @Override
                                public void run() {
                                    while(!Thread.currentThread().isInterrupted()){
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(500l);
                                        } catch (InterruptedException e) {
                                            System.out.println("Interrupted by server");
                                        }
                                        System.out.println("Client is executing some job...");
                                    }
                                }});
                            t.start();
                            // 4. Synchronization blocking waits for the server to return to answer, and returns after getting the answer
                            Object objectToReturn;
                            input = new ObjectInputStream(socket.getInputStream());

                            // keep a watch on socket input stream
                            while ((objectToReturn = input.readObject()) != null) {
                                // interrupt some job
                                t.interrupt();
                                return objectToReturn;
                            }

                        } finally {
                            if (socket != null) socket.close();
                            if (output != null) output.close();
                            if (input != null) input.close();
                            t.stop();
                        }
                        return null;
                    }
                });
    }
}


