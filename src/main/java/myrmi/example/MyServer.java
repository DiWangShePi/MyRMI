package myrmi.example;

import myrmi.registry.LocateRegistry;
import myrmi.registry.Registry;
import myrmi.zookeeper.ZkConnection;
import myrmi.exception.RemoteException;

/**
 * This is the server instance that I created during testing,
 * and it gets an instance of the zKeeper service locally
 * and passes it to the local registry as a parameter.
 * A locally implemented service is then registered with the registry.
 * The thread of this service will be upgraded to a daemon thread after
 * creation, listening on the port and providing service continuously.
 */

public class MyServer {
    public static void main(String[] args) {
        try {
            ZkConnection connection = new ZkConnection();
            LocateRegistry.setConnection(connection);

            Registry Naming = LocateRegistry.createRegistry();
            MyRemoteImpl remoteObject = new MyRemoteImpl(1099);

            /**
             * The rebind method is used here instead of bind to avoid
             * the zookeeper server reporting that the node is registered
             */
            Naming.rebind("MyRemoteImpl",remoteObject);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

