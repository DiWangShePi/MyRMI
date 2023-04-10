package myrmi.example;

import myrmi.server.UnicastRemoteObject;
import myrmi.exception.RemoteException;

/**
 * This is the server-side program I used when testing
 * system availability, and it implements only one
 * sayhello method, which returns a string
 */
public class MyRemoteImpl extends UnicastRemoteObject implements MyRemoteInterface {
    public MyRemoteImpl(int port) throws RemoteException {
        super(port);
    }

    public String sayHello(String name) throws RemoteException {
        return "Hello " + name;
    }
}
