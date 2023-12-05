import java.sql.*;
import java.util.concurrent.ThreadLocalRandom;

public class VoteInsertion {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/votevista";
    private static final String USER = "root";
    private static final String PASS = "root";

    public static void main(String[] args) {
        // Loop 100 times to insert 100 votes
        for (int i = 0; i < 100; i++) {
            int voterID = ThreadLocalRandom.current().nextInt(1, 101); // VoterID between 1 and 100
            int candidateID = ThreadLocalRandom.current().nextInt(1, 41); // CandidateID between 1 and 40
            Timestamp voteTimestamp = getRandomTimestamp();

            insertVote(voterID, candidateID, voteTimestamp);
        }
    }

    private static void insertVote(int voterID, int candidateID, Timestamp voteTimestamp) {
        String insertSql = "INSERT INTO votes (VoterID, CandidateID, Timestamp, PositionID) VALUES (?, ?, ?, (SELECT PositionID FROM candidates WHERE CandidateID = ?))";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            pstmt.setInt(1, voterID);
            pstmt.setInt(2, candidateID);
            pstmt.setTimestamp(3, voteTimestamp);
            pstmt.setInt(4, candidateID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Vote successfully recorded for VoterID: " + voterID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Timestamp getRandomTimestamp() {
        long offset = Timestamp.valueOf("2022-12-04 00:00:00").getTime();
        long end = Timestamp.valueOf("2023-12-04 00:00:00").getTime();
        long diff = end - offset + 1;
        Timestamp randTimestamp = new Timestamp(offset + (long) (Math.random() * diff));
        return randTimestamp;
    }
}
