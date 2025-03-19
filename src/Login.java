import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;


public class Login extends JFrame {



    public Login() {

        // Define basic information of the frame

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();


        int ww = this.getWidth();
        int wh = this.getHeight();

        int sw = screenSize.width;
        int sh = screenSize.height;

        // fix the interface into the middle of the screen

        this.setLocation(sw/2-ww/2, sh/2-wh/2);


        this.setTitle("Welcome to Yanxiu's p2p chat");
        this.setSize(500, 300);

        /**

         * Borderlayout layout
         * @param input_user_data: A text field to get user data input
         * @param hintForInput: A prompt for user data input
         */

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        //provide hint for the user input
        JLabel hintForInput = new JLabel("Enter username_port (press ENTER/Click on the cat)", JLabel.LEFT);
        hintForInput.setFont(new Font("", Font.BOLD, 17));

        //input username and port (username_port)
        JTextField input_user_data = new JTextField(1);
        input_user_data.setFont(new Font("", Font.ITALIC, 22));
        input_user_data.setHorizontalAlignment(JTextField.CENTER); //Input from the middle


        // The clickable image(cat) as the enter for the login activity
        JLabel loginButton = new JLabel();


        ImageIcon imageIcon = new ImageIcon("res/xx.jpg"); // load the image to a imageIcon
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(180, 180,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        imageIcon = new ImageIcon(newimg);  // transform it back

        loginButton.setIcon(imageIcon);
        loginButton.setVerticalTextPosition(JLabel.BOTTOM); //Place the picture in the middle of the box with the one below
        loginButton.setHorizontalTextPosition(JLabel.CENTER);
        loginButton.setText("LOGIN");
        loginButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            // login with the data of input field with a mouse click
            public void mouseClicked(MouseEvent e) {
                String client = input_user_data.getText().trim();

                // warn the user if the input field is empty
                if (client.equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter information", "Warning!", JOptionPane.ERROR_MESSAGE);
                } else {

                    String client1 = input_user_data.getText().trim();
                    String[] str = client1.split("_");
                    String name = str[0];
                    String port = str[1];

                    //Close the Settings page and launch the chat box page
                    setVisible(false);
                    try {

                        // Run the client interface after logged in

                        Client clientt = new Client(name,Integer.parseInt(port));
                        new ClientRun(clientt);

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });


        panel.add(hintForInput, BorderLayout.NORTH);
        panel.add(input_user_data, BorderLayout.CENTER);
        panel.add(loginButton, BorderLayout.EAST);

        this.add(panel, BorderLayout.CENTER);


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set the window size to be immutable
        this.setResizable(false);
        this.setVisible(true);

        // login through enter key
        input_user_data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //Get UserName
                String client = input_user_data.getText().trim();

                if (client.equals("")) {
                    //The user name cannot be empty
                    JOptionPane.showMessageDialog(null, "The username can't be null", "Warning!!!", JOptionPane.ERROR_MESSAGE);
                } else {
                    String client1 = input_user_data.getText().trim();
                    String[] str = client1.split("_");
                    String name = str[0];
                    String port = str[1];

                    //Close the Settings page and launch the chat box page
                    setVisible(false);
                    try {

                        // Run the client interface after logged in

                        Client clientt = new Client(name,Integer.parseInt(port));
                        new ClientRun(clientt);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    public static void main(String[] args) {
        new Login();
    }
}
