import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class QRScanner {

    private final Webcam webcam;
    private final MultiFormatReader qrCodeReader;
    private final VoteVistaUI.TablePanel tablePanel; // Reference to the TablePanel
    private final VoteVistaUI voteVistaUI; // Add this field
    String firstName,lastName;
    String dateOfBirth;
    String address;
    String sex;
    String expDate;

    public String imageUrl;

    public QRScanner(VoteVistaUI.TablePanel tablePanel, VoteVistaUI voteVistaUI) { // Modified constructor
        this.tablePanel = tablePanel;
        webcam = Webcam.getDefault();
        qrCodeReader = new MultiFormatReader();
        this.voteVistaUI = voteVistaUI; // Assign the passed VoteVistaUI instance to the field



    }

    void handleQRCodeIDResult(Result result) {
        try {
            String qrCodeText = result.getText();
            JSONObject jsonObject = new JSONObject(qrCodeText);

            // Extract data from JSON
             firstName = jsonObject.optString("firstName", "N/A");
             lastName = jsonObject.optString("lastName", "N/A");
             dateOfBirth = jsonObject.optString("dateOfBirth", "N/A");
             address = jsonObject.optString("Address");
             sex = jsonObject.getString("Sex");
             expDate = jsonObject.getString("Exp");
             imageUrl = jsonObject.optString("imagePath", "");

            // Format the information for display
            final String info = String.format(
                    "<html><center>First Name: %s<br>Last Name: %s<br>Date of Birth: %s<br>Address: %s<br>Sex: %s<br>Exp: %s</center></html>",
                    firstName, lastName, dateOfBirth, address,sex,expDate
            );

            // If there is a URL, fetch the image in a separate thread
            if (!imageUrl.isEmpty()) {
                new Thread(() -> {
                    try {
                        URL url = new URL(imageUrl);
                        BufferedImage urlImage = ImageIO.read(url); // Load image from the URL

                        // Resize the image to the desired size
                        int targetWidth = 250; // for example, set your desired width
                        int targetHeight = 150; // for example, set your desired height

                        // Continue with the resized image
                        urlImage = resizeImage(urlImage, targetWidth, targetHeight);

                        // Update UI with the QR code info and image
                        BufferedImage finalUrlImage = urlImage;

                        SwingUtilities.invokeLater(() -> {
                            tablePanel.updateUIWithInfoAndImage(info, finalUrlImage);
                            // Prompt user to confirm the information
                            ConfirmationDialog dialog = new ConfirmationDialog(voteVistaUI.frame, "Confirm Information", "Are these info corrects?", () -> {
                                tablePanel.promptForIDScan(); // This will be run if "No" is clicked
                            });
                            // Set location
                            dialog.setLocation(800, 400); // Set your desired X and Y coordinates
                            int response = dialog.showDialog();
                            if (response == JOptionPane.YES_OPTION) {
                                // If the user confirms the information is correct
                                voteVistaUI.idScanned = true;
                                voteVistaUI.firstName = firstName;
                                voteVistaUI.lastName = lastName;

                                tablePanel.displayStep();
                                tablePanel.startQRScanning();


                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();

                        // Show error dialog if the image can't be loaded
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, "Can't load image.", "Error", JOptionPane.ERROR_MESSAGE);
                            // Clear the info and ask to scan again
                            tablePanel.updateUIWithInfoAndImage("", null);
                        });
                    }
                }).start();
            } else {
                // Update UI without the image
                JOptionPane.showMessageDialog(voteVistaUI.frame, "Some informations are missing, please scan your ID again.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
    }

    void handleQRCodeVotingResult(Result result) throws JSONException {

            String qrCodeText = result.getText();
            JSONObject jsonObject = new JSONObject(qrCodeText);

            // Extract data from JSON
            String firstNameVoting = jsonObject.optString("firstName", "N/A");
            String lastNameVoting = jsonObject.optString("lastName", "N/A");
            String votingNumber = jsonObject.optString("votingNumber", "N/A");
            String expDateVoting = jsonObject.optString("Expires");
            String State = jsonObject.getString("State");

            // Format the information for display
            final String info = String.format(
                    "<html><center><br><br><br>First Name: %s<br>Last Name: %s<br>Voting Number: %s<br>Expires: %s<br>State: %s</center></html>",
                    firstNameVoting, lastNameVoting, votingNumber, expDateVoting, State
            );

        SwingUtilities.invokeLater(() -> {
            tablePanel.updateUIWithInfoAndImage(info, null);
            // Prompt user to confirm the information
            // This will be run if "No" is clicked
            ConfirmationDialog dialog = new ConfirmationDialog(voteVistaUI.frame, "Confirm Information", "Are these info corrects?", tablePanel::promptForIDScan);
            dialog.setLocation(570, 450); // Set your desired X and Y coordinates
            int response = dialog.showDialog();
            if (response == JOptionPane.YES_OPTION) {
                // If the user confirms the information is correct
                System.out.println("Voting Scan - First Name: " + voteVistaUI.firstName + ", Last Name: " + voteVistaUI.lastName);

                if(firstNameVoting.equals(voteVistaUI.firstName) && lastNameVoting.equals(voteVistaUI.lastName)){
                    voteVistaUI.idScanned = false;
                    tablePanel.displayVotingInterface();
                }else{

                    tablePanel.promptForIDScan();
                }

            }else {
                // If user says the info is incorrect, clear the info and ask to scan again
                tablePanel.updateUIWithInfoAndImage("", null);
            }
        });

    }
    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();

        // Draw the original image resized into the new image
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }

}