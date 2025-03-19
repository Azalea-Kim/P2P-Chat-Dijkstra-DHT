
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ClientRun {
    P2PClientView view;

    private Client client;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    // The server socket of the client server
    private ServerSocket clientServerSocket = null;


    // bw and br of client's connection to a client server
    private BufferedWriter p2pBufferedWriter;
    private BufferedReader p2pBufferedReader;

    // input text field
    private JTextField inputMessage = new JTextField();
    //Display information
    private JTextArea displayMessage = new JTextArea();

    JButton jb, jb1, jb2, jb3, jb4, jb5, jb6;

    public ClientRun(Client client1) throws IOException {

        try {

            // get buttons and fields from the client view
            view = new P2PClientView();
            this.client = client1;
            inputMessage = view.getInputMessage();
            displayMessage = view.getDisplayMessage();
            jb = view.getJb();
            jb1 = view.getJb1();
            jb2 = view.getJb2();
            jb3 = view.getJb3();
            jb4 = view.getJb4();
            jb5 = view.getJb5();
            jb6 = view.getJb6();

            // act same as "enter"
            jb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String msg = inputMessage.getText().trim();
                    System.out.println(msg);
                    try {
                        sendMessage(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    inputMessage.setText("");
                }
            });

            //MESSAGE button

            jb1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String msg = inputMessage.getText().trim();
                    msg = "MESSAGE_" + msg;
                    try {
                        sendMessage(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    inputMessage.setText("");


                }
            });

            //BROADCAST button

            jb2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String msg = inputMessage.getText().trim();
                    msg = "BROADCAST_" + msg;
                    try {
                        sendMessage(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    inputMessage.setText("");

                }
            });

            // STATS button
            jb3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String msg = inputMessage.getText().trim();
                    msg = "STATS_" + msg;
                    try {
                        sendMessage(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    inputMessage.setText("");


                }
            });

            // LIST button
            jb4.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String msg = inputMessage.getText().trim();
                    msg = "LIST" + msg;
                    try {
                        sendMessage(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    inputMessage.setText("");

                }
            });

            // EXIT button

            jb5.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {

                    try {
                        Thread.sleep(3000); //Wait for 3 seconds
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.exit(EXIT_ON_CLOSE);
                    inputMessage.setText("");


                }
            });

            //"enter" to send message with command

            inputMessage.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String msg = inputMessage.getText().trim();
                    try {
                        sendMessage(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    inputMessage.setText("");
                }
            });

            // connection with the main server
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(client1.getSocket().getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(client1.getSocket().getInputStream()));
            clientServerSocket = client.getP2PServerSocket();


            // send the client info to main server with socket
            bufferedWriter.write(client.getClientName() + "_" + client.getPort());
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e2) {
            e2.printStackTrace();
            closeEverything(client.getSocket(), bufferedReader, bufferedWriter);
        }

        new ListenForMessage().start();
        new clientServer().start();

    }


    // send message activity of the client
    public void sendMessage(String msg) throws IOException {
        try {

            while (client.getSocket().isConnected()) {
                bufferedWriter.write(msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                break;

            }
        } catch (Exception e) {
            closeEverything(client.getSocket(), bufferedReader, bufferedWriter);
        }
    }

    // listen for message from the server
    private class ListenForMessage extends Thread {
        @Override
        public void run() {
            String port = null;
            while (true) {

                String msg = null;
                try {
                    msg = bufferedReader.readLine();

                    // display list of the current online users
                    if (msg.startsWith("LIST")) {
                        String[] str = msg.split("_");

                        displayMessage.append("Current users are: " + str[1]+"\n\n");
                        System.out.println("Current users are: " + str[1]);
                    }
                    // display the commands history of the client that asked for
                    else if (msg.startsWith("STATS")) {
                        String[] str = msg.split(",");
                        String command = str[1];
                        String of = str[2];

                        displayMessage.append("Command(s) used by " + of + " are: " + command+"\n\n");
                        System.out.println("Command(s) used by " + of + " are: " + command);
                    }

                    // display a server broadcast message for everyone
                    else if ( msg.startsWith("SBROADCAST")) {
                        String[] str = msg.split("_");
                        String content = str[1];
                        displayMessage.append("SERVER: " + content+"\n\n");
                        System.out.println("SERVER: " + content);
                    }

                    // display a broadcast message (to all the others)
                    else if (msg.startsWith("BROADCAST")) {
                        String[] str = msg.split("_");
                        String content = str[1];
                        String from = str[2];

                        displayMessage.append(from + "(broadcast): " + content+"\n\n");
                        System.out.println(from + "(broadcast): " + content);
                    }
                    // A p2p connection is created from this client to the receiver's client server
                    else if ( msg.startsWith("MESSAGE")) {

                        String[] str = msg.split("_");
                        Integer p2pServer = Integer.parseInt(str[1]);
                        String content = str[2];
                        String from = str[3];
                        //add connection to achieve a p2pconnection to the receiver's client server
                        try {
                            Socket socket = new Socket(InetAddress.getLocalHost(),p2pServer);

                            // send message through socket
                            p2pBufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            p2pBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                            p2pBufferedWriter.write("MESSAGE" + "_" + from + "_" + content);
                            p2pBufferedWriter.newLine();
                            p2pBufferedWriter.flush();

//


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    // A p2p connection is created from this client to the receiver's client server
                    else if ( msg.startsWith("MULTICAST")) {

                        String[] str = msg.split("_");
                        Integer p2pServer = Integer.parseInt(str[1]);
                        String content = str[2];
                        String from = str[3];
                        //add connection to achieve a p2pconnection to the receiver's client server
                        try {
                            Socket socket = new Socket(InetAddress.getLocalHost(),p2pServer);

                            // send message through socket
                            p2pBufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            p2pBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                            p2pBufferedWriter.write("MULTICAST" + "_" + from + "_" + content);
                            p2pBufferedWriter.newLine();
                            p2pBufferedWriter.flush();

//


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    // same as above p2p connection
                    else if (msg.startsWith("KICK")) {

                        String[] str = msg.split("_");
                        Integer p2pServer = Integer.parseInt(str[1]);
                        String from = str[2];
                        //get Connected to build P2P
                        try {

                            Socket socket = new Socket(InetAddress.getLocalHost(),p2pServer);
                            p2pBufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            p2pBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                            p2pBufferedWriter.write("KICK" + "_" + from);
                            p2pBufferedWriter.newLine();
                            p2pBufferedWriter.flush();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    closeEverything(client.getSocket(), bufferedReader, bufferedWriter);
                }

            }
        }
    }

    // close
    public void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw) {
//            broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
        try {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }

            if (socket != null) {
                socket.close(); // will also inputoutput stream
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    // the client server
    private class clientServer extends Thread {

        @Override
        public void run() {

            InetAddress address = null;
            try {
                address = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            String ip = address.getHostAddress();
            System.out.println("P2P Server is waiting for the clients,server address: " + ip + " P2P server port " + clientServerSocket.getLocalPort());

            while (true) {
                Socket socket = null;
                try {
                    socket = clientServerSocket.accept();
                    System.out.println("the P2P CLIENT address for " + clientServerSocket.getLocalPort() + " is" + socket.getRemoteSocketAddress());


                    new Thread(new P2PClientHandler(socket)).start();

                } catch (IOException e) {
                    e.printStackTrace();
                    closeServerSocket(clientServerSocket);
                }


            }


        }

    }

    // clientHandler within the scale of each client server.
    // this handles the sockets connected to the client serversocket
    private class P2PClientHandler extends Thread {
        private BufferedReader p2p_br = null;
        private Socket socket = null;


        public P2PClientHandler(Socket socket) throws IOException {
            //the connected socket to the client's server
            this.socket = socket;
            p2p_br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        }

        @Override
        public void run() {

            while (true) {
                // message received
                String message = null;
                try {
                    message = p2p_br.readLine();
                    // display a private chat message
                    if (message != null && message.startsWith("MESSAGE")) {

                        String[] msg = message.split("_");

                        String from = msg[1];
                        String content = msg[2];

                        displayMessage.append(from + "(private): " + content+"\n\n");

                        System.out.println(from + "(private): " + content);


                    }

                    else if (message != null && message.startsWith("MULTICAST")) {

                        String[] msg = message.split("_");

                        String from = msg[1];
                        String content = msg[2];

                        displayMessage.append(from + "(multicast): " + content+"\n\n");

                        System.out.println(from + "(multicast): " + content);


                    }

                    // display a kicked message and exit
                    else if (message != null && message.startsWith("KICK")) {

                        String[] msg = message.split("_");

                        String from = msg[1];

                        displayMessage.append("You are kicked by " + from+"\n\n");
                        System.out.println("You are kicked by " + from);

                        Thread.sleep(3000); //Wait for 3 seconds
                        System.exit(EXIT_ON_CLOSE);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    closeEverything(socket, p2pBufferedReader, p2pBufferedWriter);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        }
    }

    public void closeServerSocket(ServerSocket serverSocket) {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
