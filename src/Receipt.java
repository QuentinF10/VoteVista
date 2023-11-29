import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Receipt {
    private String userInfo;
    private String voteInfo;

    public Receipt(String userInfo, String voteInfo) {
        this.userInfo = userInfo;
        this.voteInfo = voteInfo;
    }

    public String getFormattedText() {
        StringBuilder formattedVoteInfo = new StringBuilder();
        // Split the voteInfo string into individual votes
        String[] votes = voteInfo.replace("{", "").replace("}", "").split(", ");
        for (String vote : votes) {
            String[] parts = vote.split("=");
            formattedVoteInfo.append(parts[0]).append(": ").append(parts[1]).append("<br/>");
        }

        LocalDateTime now = LocalDateTime.now();

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the current date and time
        String formattedDateTime = now.format(formatter);

        return "<html><body>" +
                "<h2 style='text-align: center;'>Voting Ballot</h2>" +
                "<b>Your informations</b><br/><br/>" + userInfo + "<br/>" + formattedDateTime + "<br/>"+
                "<b><br/><br/>Voted for:</b><br/><br/>" + formattedVoteInfo.toString() +
                "</body></html>";
    }

}
