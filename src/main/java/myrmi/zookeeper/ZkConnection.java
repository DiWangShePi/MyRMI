package myrmi.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZkConnection{
    // Save the path to zk. Ths format should be ip:port
    private String zkServer;
    // Save session time out.
    private int sessionTimeout;

    public ZkConnection(){
        super();
        this.zkServer = "localhost:2181";
        this.sessionTimeout = 10000;
    }

    public ZkConnection(String zkServer, int sessionTimeout){
        this.zkServer = zkServer;
        this.sessionTimeout = sessionTimeout;
    }

    public ZooKeeper getConnection() throws IOException {
        return new ZooKeeper(zkServer, sessionTimeout, new Watcher(){
            @Override
            public void process(WatchedEvent event) {
                // System.out.println("The big brother is now watching you");
            }
        });
    }
}
