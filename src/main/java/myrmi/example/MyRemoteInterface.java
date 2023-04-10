package myrmi.example;

import myrmi.Remote;
import myrmi.exception.RemoteException;

/**
 * The specific interface that the Server program implemented
 */
public interface MyRemoteInterface extends Remote {
    String sayHello(String name) throws RemoteException;
}