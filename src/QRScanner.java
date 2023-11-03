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

    public QRScanner(VoteVistaUI.TablePanel tablePanel) { // Modified constructor
        this.tablePanel = tablePanel;
        webcam = Webcam.getDefault();
        qrCodeReader = new MultiFormatReader();
    }

    void handleQRCodeResult(Result result) {
        try {
            String qrCodeText = result.getText();
            JSONObject jsonObject = new JSONObject(qrCodeText);

            // Extract data from JSON
            String firstName = jsonObject.optString("firstName", "N/A");
            String lastName = jsonObject.optString("lastName", "N/A");
            String dateOfBirth = jsonObject.optString("dateOfBirth", "N/A");
            String imageUrl = jsonObject.optString("imagePath", "");

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


                        if (imageUrl != null) {
                            // Resize the image to the desired size
                            int targetWidth = 300; // for example, set your desired width
                            int targetHeight = 200; // for example, set your desired height
                            BufferedImage resizedImage = resizeImage(urlImage, targetWidth, targetHeight);

                            // Continue with the resized image
                            urlImage = resizedImage;
                        } else {
                            System.out.println("Failed to decode image from URL: " + imageUrl);
                        }

                        // Update UI with the QR code info and image
                        BufferedImage finalUrlImage = urlImage;
                        SwingUtilities.invokeLater(() -> {
                          tablePanel.updateUIWithInfoAndImage(info, finalUrlImage);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Log or handle the case where the image cannot be loaded from the URL
                    }
                }).start();
            } else {
                // Update UI without the image
                SwingUtilities.invokeLater(() -> {
                  tablePanel.updateUIWithInfoAndImage(info, null);
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