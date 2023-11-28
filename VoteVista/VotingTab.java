package VoteVista;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VotingTab {
    private JComboBox<String> USSenatorBox;
    private JComboBox<String> MemberOfHouseOfRepresentativesBox;
    private JComboBox<String> stateSenatorBox;
    private JComboBox<String> stateRepresentativeBox;
    private JComboBox<String> stateCommissionerBox;
    private JComboBox<String> stateClerkBox;
    private JComboBox<String> stateSheriffBox;
    private JComboBox<String> stateMayorBox;
    private JComboBox<String> stateDistrictJudgeBox;
    private JFrame frame;

    public VotingTab() {
        frame = new JFrame("Voting tab");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Font titleFont = new Font("SansSerif", Font.BOLD, 24);
        JLabel titleLabel = new JLabel("Choose your candidates");
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        titleLabel.setFont(titleFont);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        String url = "jdbc:mysql://localhost:3306/votevista"; 
        String username = "root";
        String password = "root";
        ArrayList<String> candidateInfoList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT c.LastName, c.FirstName, c.PartyID, p.PartyName " +
                    "FROM candidates c " +
                    "INNER JOIN politicalparties p ON c.PartyID = p.PartyID";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String lastName = resultSet.getString("LastName");
                String firstName = resultSet.getString("FirstName");
                String partyName = resultSet.getString("PartyName");
                String candidateInfo = lastName + " " + firstName + " | " + partyName;
                candidateInfoList.add(candidateInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] listCandidates = candidateInfoList.toArray(new String[0]);

        JButton submitButton = new JButton("Submit");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (Connection connection = DriverManager.getConnection(url, username, password)) {
                    int electeurID = getLastVoterID(connection);
                    connection.setAutoCommit(false);
                    
                    updateVoteCount(connection, (String) USSenatorBox.getSelectedItem(), electeurID, getPositionIDForTitle(connection, "U.S. Senator"));
                    updateVoteCount(connection, (String) MemberOfHouseOfRepresentativesBox.getSelectedItem(), electeurID, getPositionIDForTitle(connection, "Member of House of Representatives"));
                    updateVoteCount(connection, (String) stateSenatorBox.getSelectedItem(), electeurID, getPositionIDForTitle(connection, "State Senator"));
                    updateVoteCount(connection, (String) stateRepresentativeBox.getSelectedItem(), electeurID, getPositionIDForTitle(connection, "State Representative"));
                    updateVoteCount(connection, (String) stateCommissionerBox.getSelectedItem(), electeurID, getPositionIDForTitle(connection, "Commissioner"));
                    updateVoteCount(connection, (String) stateClerkBox.getSelectedItem(), electeurID, getPositionIDForTitle(connection, "Clerk"));
                    updateVoteCount(connection, (String) stateSheriffBox.getSelectedItem(), electeurID, getPositionIDForTitle(connection, "Sheriff"));
                    updateVoteCount(connection, (String) stateMayorBox.getSelectedItem(), electeurID, getPositionIDForTitle(connection, "Mayor"));
                    updateVoteCount(connection, (String) stateDistrictJudgeBox.getSelectedItem(), electeurID, getPositionIDForTitle(connection, "District Judge"));
                    connection.commit();
                    JOptionPane.showMessageDialog(frame, "Your vote has been successfully counted", "Vote Recorded", JOptionPane.INFORMATION_MESSAGE);

                    String userInfo = getUserInfo(connection, electeurID);
                    String voteInfo = getVoteInfo(connection, electeurID);
                    
                    EventQueue.invokeLater(() -> {
                        frame.setVisible(false);
                        Receipt receipt = new Receipt(userInfo, voteInfo);
                        receipt.setVisible(true);
                    });
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error recording your vote: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        USSenatorBox = createVotingPanel("U.S. Senator", listCandidates, mainPanel);
        MemberOfHouseOfRepresentativesBox = createVotingPanel("Member of House of Representatives", listCandidates, mainPanel);
        stateSenatorBox = createVotingPanel("State Senator", listCandidates, mainPanel);
        stateRepresentativeBox = createVotingPanel("State Representative", listCandidates, mainPanel);
        stateCommissionerBox = createVotingPanel("Commissioner", listCandidates, mainPanel);
        stateClerkBox = createVotingPanel("Clerk", listCandidates, mainPanel);
        stateSheriffBox = createVotingPanel("Sheriff", listCandidates, mainPanel);
        stateMayorBox = createVotingPanel("Mayor", listCandidates, mainPanel);
        stateDistrictJudgeBox = createVotingPanel("District Judge", listCandidates, mainPanel);

        mainPanel.add(submitButton);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private int getLastVoterID(Connection connection) throws SQLException {
        int lastVoterID = 0;
        String sql = "SELECT VoterID FROM voters ORDER BY VoterID DESC LIMIT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                lastVoterID = resultSet.getInt("VoterID");
            }
        }
        return lastVoterID;
    }

    private JComboBox<String> createVotingPanel(String title, String[] candidates, JPanel mainPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(title);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        JComboBox<String> comboBox = new JComboBox<>(candidates);
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(comboBox);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(panel);
        return comboBox;
    }

    private void updateVoteCount(Connection connection, String candidateInfo, int electeurID, int positionID) throws SQLException {
        if(positionID == -1) {
            throw new SQLException("Invalid position ID: " + positionID);
        }
        String[] parts = candidateInfo.split(" \\| ")[0].split(" ");
        String lastName = parts[0];
        String firstName = parts[1];
        int candidateID = getCandidateID(connection, lastName, firstName);
    
        String sqlUpdateCandidate = "UPDATE candidates SET VoteCount = VoteCount + 1 " +
                                    "WHERE CandidateID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateCandidate)) {
            preparedStatement.setInt(1, candidateID);
            preparedStatement.executeUpdate();
        }
    
        String sqlInsertVote = "INSERT INTO Votes (VoterID, CandidateID, Timestamp, PositionID) VALUES (?, ?, NOW(), ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertVote)) {
            preparedStatement.setInt(1, electeurID);
            preparedStatement.setInt(2, candidateID);
            preparedStatement.setInt(3, positionID);
            preparedStatement.executeUpdate();
        }
    }
    
    private int getCandidateID(Connection connection, String lastName, String firstName) throws SQLException {
        String sql = "SELECT CandidateID FROM candidates WHERE LastName = ? AND FirstName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, lastName);
            preparedStatement.setString(2, firstName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("CandidateID");
                }
            }
        }
        return -1;
    }

    //Spliter les candidat pour chaque poste
    //Faire la partie Admin avec la recuperation des resultats
    //Faire en sorte que l'utilisateur ne puisse pas voter deux fois grace à la table voters et la table votes

    private String getUserInfo(Connection connection, int electeurID) throws SQLException {
        String sql = "SELECT LastName, FirstName, Age FROM voters WHERE VoterID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, electeurID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String lastName = resultSet.getString("LastName");
                    String firstName = resultSet.getString("FirstName");
                    int age = resultSet.getInt("Age");
                    return String.format("Name: %s %s<br/>Age: %d", firstName, lastName, age);
                }
            }
        }
        return "User information not found";
    }

    private int getPositionIDForTitle(Connection connection, String title) throws SQLException {
        int positionID = -1;
        String sql = "SELECT PositionID FROM Positions WHERE PositionName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, title);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    positionID = resultSet.getInt("PositionID");
                } else {
                    throw new SQLException("No position found with the title: " + title);
                }
            }
        }
        return positionID;
    }

    private String getVoteInfo(Connection connection, int electeurID) throws SQLException {
        StringBuilder voteInfoBuilder = new StringBuilder();
        String sql = "SELECT " +
                     "v.Timestamp, " +
                     "c.FirstName AS CandidateFirstName, " +
                     "c.LastName AS CandidateLastName, " +
                     "p.PartyName, " +
                     "pos.PositionName " +
                     "FROM votes AS v " +
                     "INNER JOIN candidates AS c ON v.CandidateID = c.CandidateID " +
                     "INNER JOIN politicalparties AS p ON c.PartyID = p.PartyID " +
                     "INNER JOIN positions AS pos ON v.PositionID = pos.PositionID " +
                     "WHERE v.VoterID = ? " +
                     "ORDER BY v.Timestamp";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, electeurID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String candidateFirstName = resultSet.getString("CandidateFirstName");
                    String candidateLastName = resultSet.getString("CandidateLastName");
                    String partyName = resultSet.getString("PartyName");
                    String positionName = resultSet.getString("PositionName");
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(resultSet.getTimestamp("Timestamp"));
                    voteInfoBuilder.append(String.format("<b>Position: %s</b><br/>Candidate: %s %s | %s<br/>Date: %s<br/><br/>",
                            positionName, candidateFirstName, candidateLastName, partyName, timestamp));
                }
            }
        }
        return voteInfoBuilder.toString();
    }
        

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
}