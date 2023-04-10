package myrmi.registry;

import myrmi.Remote;
import myrmi.exception.RemoteException;
import myrmi.zookeeper.ZkConnection;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Properties;

public class LocateRegistry {

    /**
     * A string variable named hostname is defined here.
     * The value of this variable is defined in the following code snippet
     * when the class is loaded. The user can change the value of this
     * variable based on modifying the string value of the host attribute
     * in the file.
     */
    static String hostname;
    static{
        try {
            FileReader reader=new FileReader("src/main/resources/registry.properties");
            Properties p=new Properties();
            p.load(reader);
            hostname=p.getProperty("host");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Connect to the zookeeper server
     */
    private static ZkConnection connection;

    public static void setConnection(ZkConnection zkConnection){
        connection = zkConnection;
    }

    public static Registry getRegistry() {
        return getRegistry("127.0.0.1", Registry.REGISTRY_PORT);
    }

    public static Registry getRegistry(String host, int port) {
        if (port <= 0) {
            port = Registry.REGISTRY_PORT;
        }
        if (host == null || host.length() == 0) {
            try {
                host = java.net.InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                host = "";
            }
        }
        Remote stub = (Remote) Proxy.newProxyInstance(Registry.class.getClassLoader(), new Class<?>[]{Registry.class}, new RegistryStubInvocationHandler(host, port));

        return (Registry) stub;
    }

    public static Registry createRegistry() throws RemoteException {
        return createRegistry(Registry.REGISTRY_PORT);
    }

    public static Registry createRegistry(int port) throws RemoteException {
        if (port == 0) {
            port = Registry.REGISTRY_PORT;
        }
        Registry registry = new RegistryImpl(port, connection);

        /**
         * Returns a proxy of the registry type that listens to the
         * address defined by the value in the properties file and
         * the port that can be passed in during initialization
         */
        return (Registry) Proxy.newProxyInstance(Registry.class.getClassLoader(), new Class<?>[]{Registry.class}, new RegistryStubInvocationHandler(hostname, port));
    }
}
