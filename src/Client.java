import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Client {

    // A client class with information contained in a client

    private Socket socket; //socket for main server

    private ServerSocket p2p_serverSocket = null; // serversocket of its own client server

    private String username;

    private int client_serverPort;


    // client constructor

    public Client(String username, int client_serverPort) throws IOException {

            this.username = username;
            this.socket = new Socket(InetAddress.getLocalHost(), 1234);
            this.client_serverPort = client_serverPort;
            Server.name_port.put(username,client_serverPort);
            this.p2p_serverSocket = new ServerSocket(client_serverPort);

        }




    public Socket getSocket() {
        return socket;
    }
    public String getClientName() {
        return username;
    }

    public int  getPort(){
        return client_serverPort;
    }

    public ServerSocket getP2PServerSocket() {
        return p2p_serverSocket;
    }


}
