package myrmi.server;

import myrmi.Remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Skeleton extends Thread {
    static final int BACKLOG = 5;
    private Remote remoteObj;

    private String host;
    private int port;
    private int objectKey;

    public int getPort() {
        return port;
    }

    public Skeleton(Remote remoteObj, RemoteObjectRef ref) {
        this(remoteObj, ref.getHost(), ref.getPort(), ref.getObjectKey());
    }

    public Skeleton(Remote remoteObj, String host, int port, int objectKey) {
        super();
        this.remoteObj = remoteObj;
        this.host = host;
        this.port = port;
        this.objectKey = objectKey;
        this.setDaemon(false);
    }

    @Override
    public void run() {
        try {
            /**
             *  Kept listening on the host and the port, If a request is
             *  received for processing, a child thread is created and processed
             */
            ServerSocket serverSocket = new ServerSocket(port, BACKLOG, InetAddress.getByName(host));
            System.out.println("Skeleton listening on " + serverSocket.getInetAddress().getHostAddress() + ":" + port);

            while (true) {
                Socket socket = serverSocket.accept();
                SkeletonReqHandler handler = new SkeletonReqHandler(socket, remoteObj, objectKey);
                handler.start();
            }
        } catch (SocketException e) {
            System.err.println("SocketException while creating server socket: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException while creating server socket: " + e.getMessage());
        }
    }
}
