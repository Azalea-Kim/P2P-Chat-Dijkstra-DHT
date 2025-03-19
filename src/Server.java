
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends JFrame{


    // server socket
    private ServerSocket serverSocket;

    // store all the clients connected in the server currently
    public static Map<String, Socket> clientsInServer = new HashMap<>();

    // store command histories for each client
    public static Map<String, String> commands = new HashMap<>();

    // store port number for each user name.
    public static Map<String, Integer> name_port = new HashMap<>();

    private JTextArea jTextArea = new JTextArea();

    // the text input field
    private JTextField server_input = new JTextField();
    //Kick out the user name

    // The text in the input field in the server interface
    private String server_text_input = null;

    private JButton jb, jb1;// STOP, SEND COMMAND

    // Server constructor with server socket
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){

        setLayout(new BorderLayout());
        add(new JScrollPane(jTextArea), BorderLayout.CENTER);

        // The input panel
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // display message on server
        jTextArea.setEditable(false);
        jTextArea.setFont(new Font("", 0, 16));

        // the input box on server
        server_input.setFont(new Font("", 0, 16));
        server_input.setHorizontalAlignment(JTextField.LEFT);


        JLabel jLabel = new JLabel("SERVER COMMAND");
        jLabel.setFont(new Font("", 0, 14));
        panel.add(jLabel, BorderLayout.WEST);
        panel.add(server_input, BorderLayout.CENTER);



        add(panel, BorderLayout.SOUTH);

        // Enter triggered command
        server_input.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                try {
                    //Get UserName
                    server_text_input = server_input.getText().trim();
                    server_text_input = server_text_input.replace("{", "");
                    server_text_input = server_text_input.replace("}", "");
                    String[] strarray = server_text_input.split("_");
                    if (server_text_input != null) {

                        if (strarray[0].equals("STOP")) {
                            // broadcast that the server is stopped
                            ClientHandler.s_broadcast("The server is stopped.");
                            // close the serversocket
                            closeServerSocket();
                            // exit system
                            System.exit(EXIT_ON_CLOSE);
                            return;
                        }

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                server_input.setText("");
            }

        });

        // Command buttons panel
        final JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(1,2));
        p2.setBorder(new TitledBorder("Command buttons"));

        // The stop button
        jb = new JButton("STOP");
        p2.add(jb);
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // same as above

                try {
                    ClientHandler.s_broadcast("The server is stopped.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                closeServerSocket();


                //Force the server program to shut down through the system
                System.exit(EXIT_ON_CLOSE);
            }
        });

        // Act same as "Enter"

        jb1 = new JButton("Send Command");
        p2.add(jb1);

        this.add(p2, BorderLayout.NORTH);


        setTitle("Server");
        setSize(500, 300);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();


        int ww = this.getWidth();
        int wh = this.getHeight();

        int sw = screenSize.width;
        int sh = screenSize.height;

        // fix the interface into the middle of the screen

        this.setLocation(sw/2-ww/2, sh/2-wh/2);

        setVisible(true); //

        try{
            // Display server creation time
            jTextArea.append("Server created on: " + new Date() + "\n\n");
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            jTextArea.append("Server waiting for client connections. The server address: " + ip + " server port " + 1234+"\n");
            System.out.println("Server is waiting for the clients,server address: " + ip + " server port " + 1234);
            while(!serverSocket.isClosed()){

                Socket socket = serverSocket.accept(); //waiting for a client to connect
                // blocking method, halted here until someone connects
                // if connect, socket object is returned to communicate

                jTextArea.append("A new client has connected!  ");
                System.out.println("A new client has connected!");
                String as = String.valueOf(socket.getRemoteSocketAddress());
                jTextArea.append("the client address "+ as+"\n"+"\n");
                System.out.println("the client address "+ as);
                ClientHandler clientHandler = new ClientHandler(socket);
                // runnable , executed by separate thread


                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        }catch (IOException e){
            closeServerSocket();
        }
    }

    //if error occurs, shut down the server socket
    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {

        // Start up the server on execution

        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);

        server.startServer();

    }


}
