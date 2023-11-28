import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoteSummaryPrinter {
    private JFrame frame;
    private Map<String, String> selectedVotes; // Selected votes passed from the UI

    public VoteSummaryPrinter(Map<String, String> selectedVotes) {
        this.selectedVotes = selectedVotes;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Vote Summary");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Font titleFont = new Font("SansSerif", Font.BOLD, 24);
        JLabel titleLabel = new JLabel("Vote Summary");
        titleLabel.setFont(titleFont);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Display the selected candidates
        selectedVotes.forEach((position, candidate) -> {
            mainPanel.add(createPositionPanel(position, candidate));
        });

        frame.add(mainPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        frame.add(closeButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private JPanel createPositionPanel(String position, String candidate) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel positionLabel = new JLabel(position + ": ");
        JLabel candidateLabel = new JLabel(candidate);

        panel.add(positionLabel);
        panel.add(candidateLabel);

        return panel;
    }

    public void display() {
        frame.setVisible(true);
    }
}
