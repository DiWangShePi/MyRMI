package myrmi.registry;

import myrmi.Remote;
import myrmi.exception.AlreadyBoundException;
import myrmi.exception.NotBoundException;
import myrmi.exception.RemoteException;
import myrmi.server.Skeleton;
import myrmi.server.Util;
import myrmi.zookeeper.ZkConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

public class RegistryImpl implements Registry {

    /**
     * In the registry implementation, I use zookeeper to store the
     * server information, so the hashmap I created earlier is commented out.
     */
    // private final HashMap<String, Remote> bindings = new HashMap<>();
    private ZkConnection zkConnection;

    /**
     * Construct a new RegistryImpl, define the connection to the zookeeper server
     * and create a skeleton on given port
     **/
    public RegistryImpl(int port, ZkConnection connection) throws RemoteException {
        this.zkConnection = connection;
        Skeleton skeleton = new Skeleton(this, "127.0.0.1", port, 0);
        skeleton.start();
    }

    /**
     * looks up the specified service in the zookeeper server and deserializes the found value back to the user as an object
     * @param name
     * @return
     * @throws RemoteException
     * @throws NotBoundException
     */
    public Remote lookup(String name) throws RemoteException, NotBoundException {
        System.out.printf("RegistryImpl: lookup(%s)\n", name);

        // Look for the object
        Remote remoteObj = null;
        try{
            byte[] datas = zkConnection.getConnection().getData("/"+name, false, null);
            if (datas != null)
                remoteObj = (Remote) Util.deserialize(datas);
        } catch (Exception e){
            e.printStackTrace();
        }

        // Remote remoteObj = bindings.get(name);
        if (remoteObj == null) {
            throw new NotBoundException("Object not found for name: " + name);
        }
        return remoteObj;
    }

    /**
     * Bind a service to the zoopeeker server, first verify that
     * there is a service with the same name, and if not, serialize
     * the incoming object and register it
     * @param name
     * @param obj
     * @throws RemoteException
     * @throws AlreadyBoundException
     */
    public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException {
        System.out.printf("RegistryImpl: bind(%s)\n", name);

        try{
            // Turn the object into an byte array
            byte[] objBytes = Util.serialize(obj);

            // Check the service
            byte[] datas = zkConnection.getConnection().getData("/"+name, false, null);
            if (datas != null)
                throw new AlreadyBoundException("Service name already been used: " + name);

            // bind the service
            String result = 
                zkConnection.getConnection().create("/"+name, objBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.printf("Result for adding serverce: ", result);
        } catch ( Exception e ){
            e.printStackTrace();
        }
    }

    /**
     * Unbind a service, first verify whether it exists, and then delete it
     * @param name
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void unbind(String name) throws RemoteException, NotBoundException {
        System.out.printf("RegistryImpl: unbind(%s)\n", name);
        
        Stat stat = new Stat();
        try {
            byte[] data = zkConnection.getConnection().getData("/"+name, false, stat);
            if (data != null)
                zkConnection.getConnection().delete(name, stat.getCversion());
        } catch (IOException | KeeperException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public void rebind(String name, Remote obj) throws RemoteException {
        System.out.printf("RegistryImpl: rebind(%s)\n", name);
        try {
            Stat stat = new Stat();
            byte[] data = zkConnection.getConnection().getData("/"+name, false, stat);
            byte[] objBytes = Util.serialize(obj);

            if (data != null){
                zkConnection.getConnection().delete("/"+name, stat.getCversion());
            }
            zkConnection.getConnection().create("/"+name, objBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Rebind a service, verify whether it exists, delete it and re-register it,
     * and register it directly if it does not exist
     * @return
     * @throws RemoteException
     */
    public String[] list() throws RemoteException {
        System.out.print("RegistryImpl: Services list(%s)\n");

        List<String> children = null;
        try{
            children = zkConnection.getConnection().getChildren("/", false);
        } catch (KeeperException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        
        return children.toArray(new String[0]);
    }


    public static void main(String args[]) {
        final int regPort = (args.length >= 1) ? Integer.parseInt(args[0])
                : Registry.REGISTRY_PORT;
        RegistryImpl registry;
        try {
            ZkConnection connection = new ZkConnection();
            registry = new RegistryImpl(regPort, connection);
        } catch (RemoteException e) {
            System.exit(1);
        }

        System.out.printf("RMI Registry is listening on port %d\n", regPort);

    }
}
