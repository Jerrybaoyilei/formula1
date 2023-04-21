import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Formula1DataChecker extends JFrame {

    private final JTextField fieldUsername;
    private final JPasswordField fieldPassword;

    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/formula1";
    private final int WINDOW_WIDTH = 500;
    private final int WINDOW_HEIGHT = 350;
    private final int LABEL_WIDTH = WINDOW_WIDTH / 4;
    private final int LABEL_HEIGHT = WINDOW_HEIGHT / 12;
    private final int FIELD_WIDTH = LABEL_WIDTH * 2;
    private final int FIELD_HEIGHT = LABEL_HEIGHT;
    private final int LABEL_X = WINDOW_WIDTH / 8;
    private final int LABEL_USERNAME_Y = WINDOW_HEIGHT / 6;
    private final int LABEL_PASSWORD_Y = LABEL_USERNAME_Y + LABEL_HEIGHT * 2;
    private final int FIELD_X = LABEL_X + LABEL_WIDTH;
    private final int MESSAGE_Y = LABEL_PASSWORD_Y + LABEL_HEIGHT * 2;
    private final int MESSAGE_HEIGHT = LABEL_HEIGHT * 2;
    private final int MESSAGE_WIDTH = LABEL_WIDTH + FIELD_WIDTH;
    private final int BUTTON_WIDTH = LABEL_WIDTH;
    private final int BUTTON_HEIGHT = LABEL_HEIGHT;
    private final int BUTTON_X = (WINDOW_WIDTH) / 2 - LABEL_WIDTH / 2;
    private final int BUTTON_Y = MESSAGE_Y + MESSAGE_HEIGHT + LABEL_HEIGHT;

    public Formula1DataChecker() {

        // Set up the database login frame.
        this.setTitle("Connect to MySQL Database");
        this.setSize(WINDOW_WIDTH ,WINDOW_HEIGHT);
        this.setLayout(null);
        this.setMaximumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add username label.
        JLabel labelUsername = new JLabel("MySQL username: ");
        labelUsername.setBounds(LABEL_X, LABEL_USERNAME_Y, LABEL_WIDTH, LABEL_HEIGHT);
        labelUsername.setVerticalAlignment(SwingConstants.CENTER);
        this.add(labelUsername);

        // Add password label.
        JLabel labelPassword = new JLabel("MySQL password: ");

        labelPassword.setBounds(LABEL_X, LABEL_PASSWORD_Y, LABEL_WIDTH, LABEL_HEIGHT);
        labelPassword.setVerticalAlignment(SwingConstants.CENTER);
        this.add(labelPassword);

        // Add username text field.
        fieldUsername = new JTextField();
        fieldUsername.setBounds(FIELD_X, LABEL_USERNAME_Y, FIELD_WIDTH, FIELD_HEIGHT);
        fieldUsername.setText("root");
        this.add(fieldUsername);

        // Add password text field.
        fieldPassword = new JPasswordField();
        fieldPassword.setBounds(FIELD_X, LABEL_PASSWORD_Y, FIELD_WIDTH, FIELD_HEIGHT);
        this.add(fieldPassword);

        // Add message text area used to show messages for connection failure, etc.
        JTextArea message = new JTextArea("Please provide your MySQL username and password; default username is \"root\"");
        message.setEditable(false);
        message.setBounds(LABEL_X, MESSAGE_Y, MESSAGE_WIDTH, MESSAGE_HEIGHT);
        message.setBackground(this.getBackground());
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        this.add(message);

        // Add "Connect" button.
        JButton buttonConnect = new JButton("Connect");

        buttonConnect.setBounds(BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        // Set up button click action for the connect button.
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                establishConnection(message);
            }
        });
        this.add(buttonConnect);

        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void establishConnection(JTextArea message) {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL, fieldUsername.getText(), new String(fieldPassword.getPassword()));
            new F1LoginSignup(connection);
            this.dispose();
        } catch (SQLException e) {
            message.setText("Cannot connect to the database. Check your MySQL username or password");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Formula1DataChecker();
    }
}
