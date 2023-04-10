package myrmi.example;

import myrmi.registry.Registry;
import myrmi.registry.LocateRegistry;
import myrmi.exception.RemoteException;

/**
 * This is the client program I use when testing system availability.
 * It gets a local registry instance, requests a service from that instance,
 * calls the sayHello method of that service, passes in a string, and prints the result
 */

public class MyClient {
    public static void main(String[] args) {
        try {
            Registry Naming = LocateRegistry.getRegistry();
            MyRemoteInterface remoteObject = (MyRemoteInterface) Naming.lookup("MyRemoteImpl");
            String message = remoteObject.sayHello("GULUGULU");
            System.out.println(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
