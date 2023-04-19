import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBLogin extends JFrame {

    private Connection connection;

    private JTextField fieldUsername;
    private JPasswordField fieldPassword;
    private JButton buttonConnect;

    private String DATABASE_URL = "jdbc:mysql://localhost:3306/formula1";

    private int WINDOW_WIDTH = 500;
    private int WINDOW_HEIGHT = 350;
    private int LABEL_WIDTH = WINDOW_WIDTH / 4;
    private int LABEL_HEIGHT = WINDOW_HEIGHT / 12;
    private int FIELD_WIDTH = LABEL_WIDTH * 2;
    private int FIELD_HEIGHT = LABEL_HEIGHT;

    public DBLogin() {

        // Set up the database login frame.
        this.setTitle("Connect to MySQL Database");
        this.setSize(WINDOW_WIDTH ,WINDOW_HEIGHT);
        this.setLayout(null);
        this.setMaximumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add username label.
        JLabel labelUsername = new JLabel("MySQL username: ");
        int LABEL_X = WINDOW_WIDTH / 8;
        int LABEL_USERNAME_Y = WINDOW_HEIGHT / 6;
        labelUsername.setBounds(LABEL_X, LABEL_USERNAME_Y, LABEL_WIDTH, LABEL_HEIGHT);
        labelUsername.setVerticalAlignment(SwingConstants.CENTER);
        this.add(labelUsername);

        // Add password label.
        JLabel labelPassword = new JLabel("MySQL password: ");
        int LABEL_PASSWORD_Y = LABEL_USERNAME_Y + LABEL_HEIGHT * 2;
        labelPassword.setBounds(LABEL_X, LABEL_PASSWORD_Y, LABEL_WIDTH, LABEL_HEIGHT);
        labelPassword.setVerticalAlignment(SwingConstants.CENTER);
        this.add(labelPassword);

        // Add username text field.
        fieldUsername = new JTextField();
        int FIELD_X = LABEL_X + LABEL_WIDTH;
        fieldUsername.setBounds(FIELD_X, LABEL_USERNAME_Y, FIELD_WIDTH, FIELD_HEIGHT);
        this.add(fieldUsername);

        // Add password text field.
        fieldPassword = new JPasswordField();
        fieldPassword.setBounds(FIELD_X, LABEL_PASSWORD_Y, FIELD_WIDTH, FIELD_HEIGHT);
        this.add(fieldPassword);

        // Add message text area used to show messages for connection failure, etc.
        JTextArea message = new JTextArea("Please provide your MySQL username and password; default username is \"root\"");
        int MESSAGE_Y = LABEL_PASSWORD_Y + LABEL_HEIGHT * 2;
        int MESSAGE_HEIGHT = LABEL_HEIGHT * 2;
        int MESSAGE_WIDTH = LABEL_WIDTH + FIELD_WIDTH;
        message.setBounds(LABEL_X, MESSAGE_Y, MESSAGE_WIDTH, MESSAGE_HEIGHT);
        message.setBackground(this.getBackground());
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        this.add(message);

        // Add "Connect" button.
        buttonConnect = new JButton("Connect");
        int BUTTON_WIDTH = LABEL_WIDTH;
        int BUTTON_HEIGHT = LABEL_HEIGHT;
        int BUTTON_X = (WINDOW_WIDTH) / 2 - LABEL_WIDTH / 2;
        int BUTTON_Y = MESSAGE_Y + MESSAGE_HEIGHT + LABEL_HEIGHT;
        buttonConnect.setBounds(BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.add(buttonConnect);

        // Set up button click action for the connect button.
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                establishConnection(message);
            }
        });



        this.setVisible(true);
    }

    public void establishConnection(JTextArea message) {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, fieldUsername.getText(), new String(fieldPassword.getPassword()));
            new F1LoginSignup(connection);
            this.dispose();
        } catch (SQLException e) {
            message.setText("Cannot connect to the database. Check your MySQL username or password");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new DBLogin();
    }

}
