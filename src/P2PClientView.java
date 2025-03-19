

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class P2PClientView {


    private JFrame frame;


    private JTextField inputMessage = new JTextField();
    //Display information box
    private JTextArea displayMessage = new JTextArea();

    JButton jb, jb1,jb2, jb3, jb4,jb5,jb6,jb7;



    // The view for each p2p client
    public P2PClientView(){
        // define the frame
        frame = new JFrame("Welcome to Yanxiu's P2P");
        frame.setBounds(400,300, 600, 400);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setTitle("Client" );
        frame.setSize(600, 400);

        // Define basic information of the frame

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();


        int ww = frame.getWidth();
        int wh = frame.getHeight();

        int sw = screenSize.width;
        int sh = screenSize.height;

        // fix the interface into the middle of the screen

        frame.setLocation(sw/2-ww/2, sh/2-wh/2);

        frame.setVisible(true);


        // input text
        inputMessage.setFont(new Font("", 0, 17));

        // display message field
        displayMessage.setFont(new Font("", Font.BOLD, 17));


        // command buttons
        final JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(4,2));
        p2.setBorder(new TitledBorder("Command buttons"));

        jb = new JButton("Send Command");
        p2.add(jb);

        jb1 = new JButton("MESSAGE");
        p2.add(jb1);

        jb2 = new JButton("BROADCAST");
        p2.add(jb2);

        jb3 = new JButton("STATS");
        p2.add(jb3);

        jb4 = new JButton("LIST");
        p2.add(jb4);

        jb5 = new JButton("EXIT");
        p2.add(jb5);


        jb6 = new JButton("KICK");
        p2.add(jb6);
        jb7 = new JButton("HELP");
        p2.add(jb7);
        jb7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Welcome to Yanxiu's P2P Chat\n"
                                + "You can only chat when the server is online\n"
                                + "You can either enter command and press enter or click the Send Command button\n"
                                + "or type in the message and click on the command buttons for your own need\n"
                                + "For buttons STATS and KICK you have to provide valid username as input\n"
                                + "For button LIST and EXIT, they nothing to do with your input\n\n"

                                + "Command {BROADCAST_{content}} enables a client to send text to all the other \n" +
                                "clients connected to the server\n"
                                + "Command {MESSAGE_ID_{content}} enables a client to send a text to the client \n" +
                                "with the given ID directly as a P2P connection\n"
                                + "Command {LIST} displays a list of all client IDs currently connected to the server\n"
                                + "Command {KICK_ ID} closes the connection between the server and the IP client, \n" +
                                "and also announces this to all clients"
                                + "Command {STATS_ ID} gets a list of all commands used by the client identified by\n" +
                                "the ID",

                        "ABOUT", JOptionPane.DEFAULT_OPTION);
            }

        });


        frame.add(p2, BorderLayout.NORTH);

        //input panel
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        JLabel jLabel = new JLabel("Press ENTER to utilize command", JLabel.CENTER);
        jLabel.setFont(new Font("", 0, 14));
        jPanel.add(jLabel, BorderLayout.NORTH);
        jPanel.add(inputMessage, BorderLayout.CENTER);

        inputMessage.setHorizontalAlignment(JTextField.LEFT);
        frame.add(jPanel, BorderLayout.SOUTH);


        // Since it is a display, it cannot be editable
        displayMessage.setEditable(false);
        displayMessage.add(new JScrollPane());
        frame.add(new JScrollPane(displayMessage), BorderLayout.CENTER);





    }


    // getter methods to interact with the back-end
    public JTextField getInputMessage() {
        return inputMessage;
    }


    public JTextArea getDisplayMessage() {
        return displayMessage;
    }

    public JButton getJb() {
        return jb;
    }
    public JButton getJb1() {
        return jb1;
    }
    public JButton getJb2() {
        return jb2;
    }
    public JButton getJb3() {
        return jb3;
    }
    public JButton getJb4() {
        return jb4;
    }
    public JButton getJb5() {
        return jb5;
    }
    public JButton getJb6() {
        return jb6;
    }

    // create a view while execution
    public static void main(String[] args) {
        new P2PClientView();
    }

}
