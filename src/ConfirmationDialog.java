import javax.swing.*;
import java.awt.*;

class ConfirmationDialog extends JDialog {
    private int result = JOptionPane.CLOSED_OPTION;

    public ConfirmationDialog(Frame owner, String title, String message, Runnable onNo) {
        super(owner, title, true); // true for modal

        // Set the dialog message
        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create YES and NO buttons and their event handlers
        JButton yesButton = new JButton("Yes");
        yesButton.addActionListener(e -> {
            result = JOptionPane.YES_OPTION;
            dispose(); // Close the dialog
        });

        JButton noButton = new JButton("No");
        noButton.addActionListener(e -> {
            result = JOptionPane.NO_OPTION;
            dispose(); // Close the dialog
            onNo.run(); // Run the provided Runnable when "No" is clicked
        });

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        // Add components to the dialog
        this.setLayout(new BorderLayout());
        this.add(messageLabel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog size and location
        this.setSize(180, 100); // Set your desired size
        this.setLocationRelativeTo(owner); // Center it over the owner (can be null for screen center)
    }

    public int showDialog() {
        this.setVisible(true);
        return result; // Return the result of the dialog
    }

    public void setLocationRelativeTo(int i, int i1) {
    }
}
