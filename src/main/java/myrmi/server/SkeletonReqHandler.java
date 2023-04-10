package myrmi.server;

import myrmi.Remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class SkeletonReqHandler extends Thread {
    private Socket socket;
    private Remote obj;
    private int objectKey;

    public SkeletonReqHandler(Socket socket, Remote remoteObj, int objectKey) {
        this.socket = socket;
        this.obj = remoteObj;
        this.objectKey = objectKey;
    }

    @Override
    public void run() {
        int objectKey;
        String methodName;
        Class<?>[] argTypes;
        Object[] args;
        Object result;

        try {
            // Get the socket's input and output stream
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            // Get the specific information needed to handle the request
            objectKey = in.readInt();
            methodName = in.readUTF();
            argTypes = (Class<?>[]) in.readObject();
            args = (Object[]) in.readObject();

            // Invoke the method and get the result
            Method method = obj.getClass().getMethod(methodName, argTypes);
            result = method.invoke(obj, args);

            if (result == null) {
                out.writeInt(1);
            } else {
                out.writeInt(2);
                out.writeObject(result);
            }
            out.flush();
            socket.close();
        } catch (IOException e) {
            System.err.println("IOException while handling request: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException while handling request: " + e.getMessage());
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeInt(0);
                out.writeObject(e);
                out.flush();
            } catch (IOException ex) {
                System.err.println("IOException while handling exception: " + ex.getMessage());
            }
        }

    }
}
