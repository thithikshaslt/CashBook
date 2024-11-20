package expense_income_tracker;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class LoginSignupUI extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private static final ReentrantLock fileLock = new ReentrantLock();

    public LoginSignupUI() {
        setTitle("Login / Signup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        setPreferredSize(new Dimension(500, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Row 0
        add(usernameLabel, gbc);

        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1; // Column 1
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(passwordLabel, gbc);

        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.addActionListener(e -> login());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(loginButton, gbc);

        JButton signupButton = new JButton("Signup");
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.addActionListener(e -> signup());
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(signupButton, gbc);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String passwordHash = PasswordUtils.hashPassword(password);
        List<User> users = readUsersFromFile();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPasswordHash().equals(passwordHash)) {
                JOptionPane.showMessageDialog(this, "Login successful!");

                ExpensesIncomesTracker tracker = new ExpensesIncomesTracker();
                tracker.loadUserRecords(username);
                tracker.setVisible(true);

                dispose();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void signup() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String passwordHash = PasswordUtils.hashPassword(password);

        fileLock.lock();
        try {
            List<User> users = readUsersFromFile();
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
                writer.write(username + ":" + passwordHash);
                writer.newLine();
                JOptionPane.showMessageDialog(this, "Signup successful! You can now log in.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            fileLock.unlock(); // Ensure lock is released
        }
    }

    private List<User> readUsersFromFile() {
        List<User> users = Collections.synchronizedList(new ArrayList<>());
        fileLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.add(new User(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading from file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            fileLock.unlock(); // Ensure lock is released
        }
        return users;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginSignupUI::new);
    }
}
