# MyRMI

This was the second job in distributed and cloud computing, where we were asked to implement our own RPC/RMI framework, using the MyServer and MyClient files in the example folder.


- The server address bound to the registry is defined in the properties file and is the local address by default.
- The port on which the registry listens can be passed in when MyServer is created and defaults to 11099.
- The address at which the registration service runs is the local address, and the port can be passed in when MyServer is created. The default is 1099.
- The IP address of the zookeeper server monitored is the local IP address, port number is 2181, and wait time is 10 seconds. This information can be specified when you create the connection. For a more detailed explanation, see the ZkConnection file in the zookeeper folder
