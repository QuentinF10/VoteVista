import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminDialog extends JDialog {
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private String name = "admin", pwd = "root";

    public AdminDialog(JFrame parentFrame) {
        super(parentFrame, "Admin Login", true);

        // ID Field
        idField = new JTextField(15);
        // Password Field
        passwordField = new JPasswordField(15);
        // Login Button
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> performLogin());

        // Layout
        setLayout(new FlowLayout());
        add(new JLabel("ID:"));
        add(idField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);

        pack();
        setLocationRelativeTo(parentFrame);
    }

    private void performLogin() {
        // Authentication logic goes here
        String id = idField.getText();
        String password = new String(passwordField.getPassword());
        if (id.equals(name) && password.equals(pwd)) {
            // Successful login
            dispose();
        } else {
            // Failed login
            JOptionPane.showMessageDialog(this, "Invalid ID or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
