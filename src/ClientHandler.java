import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    // socket connected to the main server, communicate with the main server
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private String clientUsername;

    private String clientPort;



    public ClientHandler(Socket socket) throws IOException {
        try{
            this.socket = socket;
            // char stream, byte stream
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // After creation of a new client
            String c = bufferedReader.readLine();
            String[] strarray = c.split("_");

            this.clientUsername = strarray[0];
            this.clientPort = strarray[1];

            // Store the new client in the map.
            Server.clientsInServer.put(clientUsername,socket);
            // store information
            Server.name_port.put(clientUsername,Integer.parseInt(clientPort));

            System.out.println(clientUsername+" has entered the chat! Port: "+Integer.parseInt(clientPort));
            // server broadcast to announce all the clients
            s_broadcast(clientUsername+" has entered the chat! Port: "+Integer.parseInt(clientPort));

        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);

        }
    }

    @Override
    public void run() {
        // message received from a client
        String messageFromClient;

        while(socket.isConnected()){
            try{

                messageFromClient = bufferedReader.readLine();
                if(messageFromClient.length()!= 0){

                    // add to command history of this client
                    String com = "";
                    if(Server.commands.containsKey(clientUsername)){
                    com = Server.commands.get(clientUsername);

                    }
                    Server.commands.put(clientUsername,com+" "+messageFromClient);


                    messageFromClient = messageFromClient.replace("{","");
                    messageFromClient = messageFromClient.replace("}","");


                    // BROADCAST_CONTENT
                    // MESSAGE_ID_CONTENT
                    String[] strarray=messageFromClient.split("_");

                    System.out.println(strarray[0]);

                    // SEND PRIVATE MESSAGE
                    if(strarray[0].equals("MESSAGE")){

                    String receiver = strarray[1];
                    String content = strarray[2];
                        int peerPort = Server.name_port.get(receiver);

                        bufferedWriter.write("MESSAGE_"+peerPort+"_"+content+"_"+clientUsername);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                    }

                   else if(strarray[0].equals("MULTICAST")){
                       String content = strarray[1];

                        String[] receivers = Dijkstra.getOSPF(clientUsername);
//                        int peerPort = Server.name_port.get(receivers[0]);
//                        int peerPort2 = Server.name_port.get(receivers[1]);
//                        int peerPort3 = Server.name_port.get(receivers[2]);

//                        bufferedWriter.write("MULTICAST_"+peerPort+"_"+content+"_"+clientUsername);
//                        bufferedWriter.newLine();
//                        bufferedWriter.flush();
//
//                        bufferedWriter.write("MULTICAST_"+peerPort2+"_"+content+"_"+clientUsername);
//                        bufferedWriter.newLine();
//                        bufferedWriter.flush();
//
//                        bufferedWriter.write("MULTICAST_"+peerPort3+"_"+content+"_"+clientUsername);
//                        bufferedWriter.newLine();
//                        bufferedWriter.flush();

                        for(int i = 0; i<receivers.length;i++){
                            bufferedWriter.write("MULTICAST_"+Server.name_port.get(receivers[i])+"_"+content+"_"+clientUsername);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }

                    }



                    // AST FOR THE CURRENT ONLINE USER LIST
                    else if (strarray[0].equals("LIST")){
                        String list = "";
                        // iterate through the static list stored in the server
                        for (String peer : Server.clientsInServer.keySet()) {
                                list= list + " "+peer;
                            }

                        bufferedWriter.write("LIST_"+list);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();


                    }

                    // BROADCAST MESSAGE TO ALL THE CLIENT CONNECTED TO THE SERVER
                else if (strarray[0].equals("BROADCAST")){
                    String content = strarray[1];
                    broadcast(content);

                }
                // ASK FOR ALL THE COMMAND USED BY THE USER
                    // STATS_ID
                else if (strarray[0].equals("STATS")){
                        String cmd = Server.commands.get(strarray[1]);
                        if(cmd!=null){
                        bufferedWriter.write("STATS,"+cmd+","+strarray[1]);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        }
                    }
                // KICK A SELECTED CLIENT
                    //KICK_ID
                else if (strarray[0].equals("KICK")) {

                        String receiver = strarray[1];

                        int peerPort = Server.name_port.get(receiver);
                        bufferedWriter.write("KICK_"+peerPort+"_"+clientUsername);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        removeClient(receiver);

                        // broadcast this event to all the others

                        s_broadcast(receiver + " has beed kicked by "+clientUsername);


                    }

                    }
                } catch (IOException e) {
                try {

                    //exits like window exits and so on
                    closeEverything(socket,bufferedReader,bufferedWriter);
                    s_broadcast(clientUsername+" has left the chat!");
                    break;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }

        }
    }


    // remove a client in the server
    public void removeClient(String name){
        Server.clientsInServer.remove(name);
        Server.name_port.remove(name);

    }

    // bradcast message to all the users except the current user that sends the message
    public void broadcast(String msg) throws IOException {
            for (String peer : Server.clientsInServer.keySet()) {
                if (Server.clientsInServer.get(peer) != socket) {
                    BufferedWriter bwTarget = new BufferedWriter(new OutputStreamWriter(Server.clientsInServer.get(peer).getOutputStream()));
                    bwTarget.write("BROADCAST_"+msg+"_"+clientUsername);
                    bwTarget.newLine();
                    bwTarget.flush();
                }

            }

    }

    // broadcast to all the users including the current user
    public static void s_broadcast(String msg) throws IOException {


        for (String peer : Server.clientsInServer.keySet()) {
                BufferedWriter bwTarget = new BufferedWriter(new OutputStreamWriter(Server.clientsInServer.get(peer).getOutputStream()));
                bwTarget.write("SBROADCAST_"+msg);
                bwTarget.newLine();
                bwTarget.flush();
        }

    }



    // CLOSE
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {
        removeClient(clientUsername);

        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }

            if(socket != null){
                socket.close(); // will also inputoutput stream
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }


}
