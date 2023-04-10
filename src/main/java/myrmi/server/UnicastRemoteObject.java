package myrmi.server;

import myrmi.Remote;
import myrmi.exception.RemoteException;


public class UnicastRemoteObject implements Remote, java.io.Serializable {
    int port;
    static int objectKey = 1;

    protected UnicastRemoteObject() throws RemoteException {
        this(0);
    }

    protected UnicastRemoteObject(int port) throws RemoteException {
        this.port = port;
        exportObject(this, port);
    }

    public static Remote exportObject(Remote obj) throws RemoteException {
        return exportObject(obj, 0);
    }

    public static Remote exportObject(Remote obj, int port) throws RemoteException {
        return exportObject(obj, "127.0.0.1", port);
    }

    /**
     * 1. create a skeleton of the given object ``obj'' and bind with the address ``host:port''
     * 2. return a stub of the object ( Util.createStub() )
     **/
    public static Remote exportObject(Remote obj, String host, int port) throws RemoteException {
        // Get the name of the object's interface name in remote
        String objInterfaceName =obj.getClass().getInterfaces()[0].getName();
        // Use this name and the other arguements to create an RemoteObjectRef
        RemoteObjectRef remoteObjectRef = 
                        new RemoteObjectRef(host, port, objectKey++, objInterfaceName);

        // Create an Skeleton and start the object service
        Skeleton skeleton = new Skeleton(obj, host, port, remoteObjectRef.getObjectKey());
        skeleton.start();

        // Create and return the stub
        return Util.createStub(remoteObjectRef);
    }
}
