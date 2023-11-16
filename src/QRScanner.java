import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public class QRScanner {

    private final Webcam webcam;
    private final MultiFormatReader qrCodeReader;
    private final VoteVistaUI.TablePanel tablePanel; // Reference to the TablePanel
    private final VoteVistaUI voteVistaUI; // Add this field

    public String imageUrl;

    public QRScanner(VoteVistaUI.TablePanel tablePanel, VoteVistaUI voteVistaUI) { // Modified constructor
        this.tablePanel = tablePanel;
        webcam = Webcam.getDefault();
        qrCodeReader = new MultiFormatReader();
        this.voteVistaUI = voteVistaUI; // Assign the passed VoteVistaUI instance to the field



    }

    void handleQRCodeResult(Result result) {
        try {
            String qrCodeText = result.getText();
            JSONObject jsonObject = new JSONObject(qrCodeText);

            // Extract data from JSON
            String firstName = jsonObject.optString("firstName", "N/A");
            String lastName = jsonObject.optString("lastName", "N/A");
            String dateOfBirth = jsonObject.optString("dateOfBirth", "N/A");
             imageUrl = jsonObject.optString("imagePath", "");

            // Format the information for display
            final String info = String.format(
                    "<html><center>First Name: %s<br>Last Name: %s<br>Date of Birth: %s</center></html>",
                    firstName, lastName, dateOfBirth
            );

            // If there is a URL, fetch the image in a separate thread
            if (!imageUrl.isEmpty()) {
                new Thread(() -> {
                    try {
                        URL url = new URL(imageUrl);
                        BufferedImage urlImage = ImageIO.read(url); // Load image from the URL

                        // Resize the image to the desired size
                        int targetWidth = 300; // for example, set your desired width
                        int targetHeight = 200; // for example, set your desired height

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
                            int response = dialog.showDialog();
                            if (response == JOptionPane.YES_OPTION) {
                                // If the user confirms the information is correct
                                tablePanel.startPhotoCaptureProcess();


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
                SwingUtilities.invokeLater(() -> {
                    // Prompt user to confirm the information
                    int response = JOptionPane.showConfirmDialog(null, "Are these info corrects?", "Confirm Information", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (response == JOptionPane.YES_OPTION) {
                        // If user confirms the info is correct, proceed with facial recognition
                        // Show the camera for facial recognition
                    } else {
                        // If user says the info is incorrect, clear the info and ask to scan again
                        tablePanel.updateUIWithInfoAndImage("", null);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
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