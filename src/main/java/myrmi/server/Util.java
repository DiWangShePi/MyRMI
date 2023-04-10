package myrmi.server;

import myrmi.Remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Util {


    public static Remote createStub(RemoteObjectRef ref) {
        // Create the Invocation handler for the remoteObjectRef
        InvocationHandler handler = new StubInvocationHandler(ref);

        // Get the interface class for the remote object
        Class<?> clazz = null;
        try{
            clazz = Class.forName(ref.getInterfaceName());
        } catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }

        // Create the object, cast it into Remote type and return it
        Object returnObj = 
                Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, handler);
        return (Remote) returnObj;
    }

    // Turn an object into an byte array
    public static byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        return byteOut.toByteArray();
    }

    // Turn an byte array into an object
    public static Object deserialize(byte[] bytes) throws Exception {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        return objIn.readObject();
    }

}
