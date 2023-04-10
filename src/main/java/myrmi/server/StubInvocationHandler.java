package myrmi.server;

import myrmi.exception.RemoteException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

public class StubInvocationHandler implements InvocationHandler, Serializable {
    private String host;
    private int port;
    private int objectKey;

    public StubInvocationHandler(String host, int port, int objectKey) {
        this.host = host;
        this.port = port;
        this.objectKey = objectKey;
        System.out.printf("Stub created to %s:%d, object key = %d\n", host, port, objectKey);
    }

    public StubInvocationHandler(RemoteObjectRef ref) {
        this(ref.getHost(), ref.getPort(), ref.getObjectKey());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws RemoteException, IOException, ClassNotFoundException, Throwable {
        // Connect to the remote skeleton, send the methods and arguments
        try (Socket socket = new Socket(host, port)) {
            // Get the input and output stream
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Send the object key, method name and arguments to the remote skeleton
            out.writeInt(objectKey);
            out.writeUTF(method.getName());
            out.writeObject(method.getParameterTypes());
            out.writeObject(args);
            out.flush();

            // Get the result back and return to the caller transparently
            int check = in.readInt();
            Object result = null;
            switch(check) {
                case 0:
                    result = in.readObject();
                    throw (Throwable) result;
                case 1:
                    break;
                case 2:
                    result = in.readObject();
                    break;
            }

            // close the socket and return the result
            socket.close();
            return result;
        }

    }

}
