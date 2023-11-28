package VoteVista;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Voting {
    private boolean hasVoted = false; 
    private JFrame frame; 

    public static void main(String[] args) {
        Voting voting = new Voting();
        voting.showWindow();
    }

    public void showWindow() {
        frame = new JFrame("VoteVista");
        Font titleFont = new Font("SansSerif", Font.BOLD, 24);
        JLabel titleLabel = new JLabel("VoteVista");
        titleLabel.setFont(titleFont);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        JButton userButton = new JButton("User");
        JButton adminButton = new JButton("Admin");

        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!hasVoted) {
                    frame.setVisible(false);
                    UserInput userInputForm = new UserInput(Voting.this); 
                    userInputForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "You have already voted.", "Already Voted", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code for admin
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.anchor = GridBagConstraints.CENTER;

        buttonPanel.add(userButton, gbc);
        gbc.gridy = 1;
        buttonPanel.add(adminButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void setMainFrameVisible(boolean visible) {
        frame.setVisible(visible);
    }
}
