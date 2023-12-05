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

    /**
     * Method to get the vote Infos in the HTML format
     * @return
     */
    public String getFormattedText() {
        StringBuilder formattedVoteInfo = new StringBuilder();

        // Split the voteInfo string into individual votes
        String[] votes = voteInfo.replace("{", "").replace("}", "").split(", ");

        for (String vote : votes) {
            String[] parts = vote.split("=");
            // Wrap the position name (parts[0]) in bold tags
            formattedVoteInfo.append("<b>").append(parts[0]).append("</b>: ").append(parts[1]).append("<br/>");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        return "<html><body>" +
                "<h2 style='text-align: center;'>Voting Ballot</h2>" +
                "<h3><b>Your informations</b></h3>" + userInfo + "<br/>" + formattedDateTime + "<br/>" +
                "<b><br/><h3>Voted for:</h3></b>" + formattedVoteInfo.toString() +
                "</body></html>";
    }


}
