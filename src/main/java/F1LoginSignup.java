import com.mysql.cj.log.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class F1LoginSignup extends JFrame {

    private Connection connection;

    private final JTextField fieldEmail;
    private final JPasswordField fieldPassword;
    private final JTextArea message;

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
    private final int SIGNUP_BUTTON_X = (int)(WINDOW_WIDTH / 2 - BUTTON_WIDTH * 1.25);
    private final int LOGIN_BUTTON_X = (int)(WINDOW_WIDTH / 2 + BUTTON_WIDTH * 0.25);
    private final int BUTTON_Y = MESSAGE_Y + MESSAGE_HEIGHT + LABEL_HEIGHT;


    public F1LoginSignup(Connection connection) {
        this.connection = connection;

        // Set up the database login frame.
        this.setTitle("Login/Signup to our formula 1 database");
        this.setSize(WINDOW_WIDTH ,WINDOW_HEIGHT);
        this.setLayout(null);
        this.setMaximumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add username label.
        JLabel labelEmail = new JLabel("F1 email: ");
        labelEmail.setBounds(LABEL_X, LABEL_USERNAME_Y, LABEL_WIDTH, LABEL_HEIGHT);
        labelEmail.setVerticalAlignment(SwingConstants.CENTER);
        this.add(labelEmail);

        // Add password label.
        JLabel labelPassword = new JLabel("F1 password: ");

        labelPassword.setBounds(LABEL_X, LABEL_PASSWORD_Y, LABEL_WIDTH, LABEL_HEIGHT);
        labelPassword.setVerticalAlignment(SwingConstants.CENTER);
        this.add(labelPassword);

        // Add username text field.
        fieldEmail = new JTextField();
        fieldEmail.setBounds(FIELD_X, LABEL_USERNAME_Y, FIELD_WIDTH, FIELD_HEIGHT);
        this.add(fieldEmail);

        // Add password text field.
        fieldPassword = new JPasswordField();
        fieldPassword.setBounds(FIELD_X, LABEL_PASSWORD_Y, FIELD_WIDTH, FIELD_HEIGHT);
        this.add(fieldPassword);

        // Add message text area used to show messages for connection failure, etc.
        message = new JTextArea("Please login or signup with your email and password. For sign up, password cannot contain spaces");
        message.setEditable(false);
        message.setBounds(LABEL_X, MESSAGE_Y, MESSAGE_WIDTH, MESSAGE_HEIGHT);
        message.setBackground(this.getBackground());
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        this.add(message);

        // Add "Login" button.
        JButton buttonLogin = new JButton("Login");

        buttonLogin.setBounds(LOGIN_BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        // Set up button click action for the connect button.

        this.add(buttonLogin);

        // Add "Signup" button.
        JButton buttonSignup = new JButton("Signup");
        buttonSignup.setBounds(SIGNUP_BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        // Set up button click action for the connect button.

        this.add(buttonSignup);

        buttonSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    signUp();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    logIn();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Read the input from username and password fields. Password can only contain special characters, letters, or numbers.
     */
    public void signUp() throws SQLException {
        String email = fieldEmail.getText().trim();
        String password = new String(fieldPassword.getPassword());
        // If password contains not only contain numbers, letters or special characters, it's invalid.
        if (!password.matches("^[A-Za-z0-9_@./#&+-]*$")) {
            message.setText("Invalid password; can only contain numbers, letters or special characters; please try again");
        }
        // If no password, invalid.
        else if (password.length() == 0){
            message.setText("Password cannot be empty, please try again");
        }
        // If not email, invalid.
        else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            message.setText("Invalid email address; please try again");
        }
        else {
            CallableStatement stmt = connection.prepareCall("{CALL sign_up(?, ?, ?)}");
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.INTEGER);
            try {
                stmt.execute();
                int success = stmt.getInt(3);
                if (success == 1) {
                    message.setText("Sign up successful");
                    new MainPage(connection, email);
                    this.dispose();
                }
            }
            catch (SQLException e) {
                if (e.getSQLState().equals("45000")) {
                    message.setText("Signup failed; email already registered");
                }
                else {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * Read user's email and password input, and try to sign the user in.
     * @throws SQLException  If MySQL procedure execution encounters some error.
     */
    public void logIn() throws SQLException {
        String email = fieldEmail.getText().trim();
        String password = new String(fieldPassword.getPassword());
        CallableStatement stmt = connection.prepareCall("{CALL log_in(?, ?, ?)}");
        stmt.registerOutParameter(3, Types.VARCHAR);
        stmt.setString(1, email);
        stmt.setString(2, password);


        stmt.execute();
        String outcome = stmt.getString(3);
        // Error in sign in.
        if (outcome.equals("User not found") || outcome.equals("Incorrect password")) {
            message.setText(outcome);
        }
        // Sign in successful.
        else {
            message.setText(outcome);
            new MainPage(connection, email);
            this.dispose();
        }
    }


}
