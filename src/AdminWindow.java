import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class AdminWindow extends JDialog {
    private JComboBox<String> dropdown;
    private JLabel randomMemberLabel;
    private JButton detailsButton;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/votevista";
    private static final String USER = "root";
    private static final String PASS = "root";

    public AdminWindow(Frame parentFrame) {
        super(parentFrame, "Administration", true);
        setSize(500, 250);
        setLayout(new BorderLayout(10, 10)); // Spacing between components

        // Initialize the components
        setupComponents();

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(createHeaderLabel("Administration", 24, Font.BOLD));

        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.setAlignmentX(Component.CENTER_ALIGNMENT); 

        JLabel timeLabel = new JLabel(getCurrentTime());
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        int voterCount = getVoterCount();
        JLabel votersLabel = createHeaderLabel("There are " + voterCount + " voters in total", 18, Font.BOLD);
        votersLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        votersLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        verticalPanel.add(timeLabel);
        verticalPanel.add(votersLabel);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.insets = new Insets(5, 5, 5, 5); // Padding around each component
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createHeaderLabel("Positions", 16, Font.BOLD), gbc);

        gbc.gridx = 1;
        panel.add(createHeaderLabel("Candidate Winner", 16, Font.BOLD), gbc);

        gbc.gridx = 2;
        panel.add(createHeaderLabel("More Details", 16, Font.BOLD), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(dropdown, gbc);

        gbc.gridx = 1;
        panel.add(randomMemberLabel, gbc);

        gbc.gridx = 2;
        panel.add(detailsButton, gbc);

        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Add panels to the dialog
        add(titlePanel, BorderLayout.NORTH);
        add(verticalPanel, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        setLocation(730, 430);
    }

    private String getCurrentTime() {
        // This pattern represents a common US date and time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu hh:mm a", Locale.US);
        return LocalDateTime.now().format(formatter);
    }

    private int getVoterCount() {
        int voterCount = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(DISTINCT VoterID) AS VoterCount FROM votes")) {
    
            if (rs.next()) {
                voterCount = rs.getInt("VoterCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voterCount;
    }    

    private void setupComponents() {
        // Dropdown
        dropdown = new JComboBox<>();
        loadPositionsIntoDropdown();
        dropdown.addActionListener(e -> updateMaxVotesForSelectedPosition());

        // Random member label
        randomMemberLabel = new JLabel("No candidates");
        randomMemberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        randomMemberLabel.setPreferredSize(new Dimension(150, 20)); // Ensure the label does not resize

        // Details button
        detailsButton = new JButton("Details");
        detailsButton.addActionListener(e -> {
            String selectedPosition = (String) dropdown.getSelectedItem();
            int positionID = getPositionID(selectedPosition);
            Map<String, String> candidateDetails = getCandidatesForPosition(positionID); // Change the Map type to <String, String>
            displayCandidatesWindow(candidateDetails);
        });
        
    }

    // Helper method to get positionID from the selected position name
    private int getPositionID(String positionName) {
        int positionID = -1;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement("SELECT PositionID FROM positions WHERE PositionName = ?")) {
            pstmt.setString(1, positionName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    positionID = rs.getInt("PositionID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positionID;
    }

    // Helper method to get candidate names and their vote counts for a given positionID
    private Map<String, String> getCandidatesForPosition(int positionID) {
        Map<String, String> candidates = new LinkedHashMap<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT c.FirstName, c.LastName, pp.PartyName, COUNT(*) AS VoteCount " +
                            "FROM votes v " +
                            "JOIN candidates c ON v.CandidateID = c.CandidateID " +
                            "JOIN politicalparties pp ON c.PartyID = pp.PartyID " +
                            "WHERE v.PositionID = ? " +
                            "GROUP BY v.CandidateID, pp.PartyName " +
                            "ORDER BY VoteCount DESC")) {
            pstmt.setInt(1, positionID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String fullName = rs.getString("FirstName") + " " + rs.getString("LastName");
                    String partyName = rs.getString("PartyName");
                    int voteCount = rs.getInt("VoteCount");
                    String candidateInfo = String.format("%s (%s) - Votes: %d", fullName, partyName, voteCount);
                    candidates.put(candidateInfo, fullName);  // Use candidateInfo as key to preserve unique entries
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidates;
    }

    // Method to display the candidate details in a new window
    private void displayCandidatesWindow(Map<String, String> candidateDetails) {
        JDialog candidatesDialog = new JDialog(this, "Candidate Details", true);
        candidatesDialog.setSize(300, 200);
        candidatesDialog.setLayout(new BorderLayout());

        // If there are no candidate details, display a message
        if (candidateDetails.isEmpty()) {
            JLabel noCandidatesLabel = new JLabel("There are no votes for this position.");
            noCandidatesLabel.setHorizontalAlignment(JLabel.CENTER);
            candidatesDialog.add(noCandidatesLabel, BorderLayout.CENTER);
        } else {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (String details : candidateDetails.keySet()) {
                listModel.addElement(details);
            }
            JList<String> list = new JList<>(listModel);
            JScrollPane scrollPane = new JScrollPane(list);
            candidatesDialog.add(scrollPane, BorderLayout.CENTER);
        }

        candidatesDialog.setLocationRelativeTo(this);
        candidatesDialog.setVisible(true);
    }

    private void updateMaxVotesForSelectedPosition() {
        String selectedPosition = (String) dropdown.getSelectedItem();
        String candidateName = getTopCandidateForPosition(selectedPosition);
        if (candidateName != null && !candidateName.isEmpty()) {
            randomMemberLabel.setText(candidateName);
        } else {
            randomMemberLabel.setText("No candidates");
        }
    }

    private String getTopCandidateForPosition(String positionName) {
        String topCandidateName = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT c.FirstName, c.LastName, COUNT(*) AS VoteCount " +
                             "FROM votes v " +
                             "JOIN candidates c ON v.CandidateID = c.CandidateID " +
                             "JOIN positions p ON c.PositionID = p.PositionID " +
                             "WHERE p.PositionName = ? " +
                             "GROUP BY v.CandidateID " +
                             "ORDER BY VoteCount DESC " +
                             "LIMIT 1")) {

            pstmt.setString(1, positionName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    topCandidateName = rs.getString("FirstName") + " " + rs.getString("LastName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topCandidateName;
    }

    private void loadPositionsIntoDropdown() {
        // Add a blank entry as the first item
        dropdown.addItem("");
    
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT PositionName FROM positions")) {
    
            while (rs.next()) {
                dropdown.addItem(rs.getString("PositionName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        dropdown.setSelectedItem("");
    }
    
    private JLabel createHeaderLabel(String text, int fontSize, int fontStyle) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", fontStyle, fontSize));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
}