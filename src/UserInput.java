import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserInput extends JFrame {
    private JTextField nameField;
    private JTextField lastNameField;
    private JTextField ageField;


    public UserInput(Voting votingWindow) {
        setTitle("User Input Form");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameField = new JTextField();
        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField();
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String lastName = lastNameField.getText();
                int age = Integer.parseInt(ageField.getText());

                insertIntoDatabase(name, lastName, age);
                openVotingTab();
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(ageLabel);
        panel.add(ageField);
        panel.add(new JLabel()); 
        panel.add(submitButton);
        add(panel);
        setLocationRelativeTo(null);
    }

    private void openVotingTab() {
        this.dispose();
        EventQueue.invokeLater(() -> {
            new VotingTab().setVisible(true);
        });
    }

    private void insertIntoDatabase(String name, String lastName, int age) {
        String url = "jdbc:mysql://localhost:3306/votevista";
        String username = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO Voters (LastName, FirstName, Age) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setInt(3, age);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "You can vote now!");
            dispose();

        } catch (SQLException exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while saving data: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
