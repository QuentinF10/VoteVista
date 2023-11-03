import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;


public class VoteVistaUI {
    private JFrame frame;
    private TablePanel tablePanel;
    private Image printerImage, cameraImage, backgroundImage, oregonImage;
    private Webcam webcam;
    private WebcamPanel webcamPanel;


    public VoteVistaUI() {

        // Initialize the webcam
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcamPanel = new WebcamPanel(webcam, false);
        webcamPanel.setMirrored(true);
        webcamPanel.setFPSDisplayed(true);
        webcamPanel.setFillArea(true);

        // Create the main window
        frame = new JFrame("VoteVista");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setResizable(false); // Prevent the window from being resized

        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\Quentin\\OneDrive - ESIEE Paris\\UNM\\CS460\\VoteVista\\src/american.jpg")); // Replace with your background image path
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }
        // Load and resize the printer image
        try {
            Image originalImage = ImageIO.read(new File("C:\\Users\\Quentin\\OneDrive - ESIEE Paris\\UNM\\CS460\\VoteVista\\src/printer.png")); // Replace with your image path
            // Resize image to new width and height
            int imageWidth = 150; // Desired width
            int imageHeight = 150; // Desired height
            printerImage = originalImage.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            printerImage = null;
        }
        // Load the oregon image
        try {
            oregonImage = ImageIO.read(new File("C:\\Users\\Quentin\\OneDrive - ESIEE Paris\\UNM\\CS460\\VoteVista\\src/oregon.png")); // Replace with your background image path
        } catch (IOException e) {
            e.printStackTrace();
            oregonImage = null;
        }

        // Load and resize the camera image
        try {
            Image originalCameraImage = ImageIO.read(new File("C:\\Users\\Quentin\\OneDrive - ESIEE Paris\\UNM\\CS460\\VoteVista\\src/camera.png")); // Replace with your camera image path
            int cameraImageWidth = 50; // Desired width
            int cameraImageHeight = 30; // Desired height
            cameraImage = originalCameraImage.getScaledInstance(cameraImageWidth, cameraImageHeight, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            cameraImage = null;
        }


        // Create the table panel where the screen and printer will be placed
        tablePanel = new TablePanel();
        frame.add(tablePanel);

        // Display the window.
        frame.setVisible(true);
        tablePanel = new TablePanel();

    }

    // Inner class for table panel
    class TablePanel extends JPanel {
        private JPanel screen;
        private JLabel screenMessage;
        // New field to track camera status
        private boolean isCameraOn = false;


        public TablePanel() {
            // Initialize the screen as a JPanel
            screen = new JPanel();
            screen.setBounds(333, 263, 550, 300); // Set the position and size to match your screen area
            screen.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5)); // Black border to represent the screen edge

            // Initialize the screen message as a JLabel
            screenMessage = new JLabel("<html><center>Welcome To Oregon State,<br>Touch the screen to start voting</center></html>");
            screenMessage.setForeground(Color.BLACK);
            screenMessage.setHorizontalAlignment(JLabel.CENTER);
            screenMessage.setVerticalAlignment(JLabel.CENTER);

            // Add the message to the screen panel
            screen.add(screenMessage);
            screen.setLayout(new BorderLayout());
            screen.add(screenMessage, BorderLayout.CENTER);

            // Set the background of the screen to white to represent the inactive screen
            screen.setBackground(Color.WHITE);

            // When the screen is touched
            screen.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Adjust the size of the webcam panel
                    Dimension size = new Dimension(320, 240);
                    webcam.setViewSize(size); // Set the new size for the webcam view

                    // Create a new panel for the webcam if not already done
                    WebcamPanel webcamPanel = new WebcamPanel(webcam);
                    webcamPanel.setPreferredSize(size);

                    // Remove existing components from screen if necessary
                    screen.removeAll();

                    // Add the webcam panel to the screen
                    screen.add(webcamPanel, BorderLayout.CENTER);


                    // Refresh the screen panel to show the webcam panel
                    screen.revalidate();
                    screen.repaint();

                    // Set the camera status to on
                    isCameraOn = true;
                    TablePanel.this.repaint(); // This refers to the outer class instance of TablePanel

                    startQRScanning();
                }
            });

            // Add the screen to the TablePanel
            this.setLayout(null); // Use absolute positioning
            this.add(screen);
        }

        public void startQRScanning() {
            QRScanner qrScanner = new QRScanner(this);
            new Thread(() -> {
                // Open the webcam if not already opened
                if (!webcam.isOpen()) {
                    webcam.open();
                }

                // Start processing frames
                while (true) {
                    // Check if the frame is available
                    BufferedImage frameImage = webcam.getImage();
                    if (frameImage != null) {
                        System.out.println("looking for QR code");
                        // Process the image to detect QR code
                        LuminanceSource source = new BufferedImageLuminanceSource(frameImage);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                        try {
                            Result result = new MultiFormatReader().decode(bitmap);
                            if (result != null) {
                                // QR code found, handle result
                               qrScanner.handleQRCodeResult(result);
                                break; // Break the loop if you want to stop scanning after the first QR code is found
                            }
                        } catch (NotFoundException e) {
                            // This is normal, not every frame will contain a QR code
                        }
                    }

                    // Sleep briefly to control the loop timing and reduce CPU usage
                    try {
                        Thread.sleep(100); // Sleep for 100 milliseconds
                    } catch (InterruptedException e) {
                        // Handle the interruption appropriately
                        break; // Exit the loop if the thread is interrupted
                    }
                }
            }).start();
        }

        public void updateUIWithInfoAndImage(String info, BufferedImage image) {

            // Debugging: Check if the image is not null and print its size
            if (image != null) {
                System.out.println("Image loaded: " + image.getWidth() + "x" + image.getHeight());
            } else {
                System.out.println("Image is null.");
            }
            // Stop the webcam feed and remove the webcam view
            webcam.close();
            screen.removeAll();

            // Update the screen message with the voter info
            screenMessage.setText(info);

            // Add the message label back to the screen panel
            screen.add(screenMessage, BorderLayout.NORTH);

            // If there is an image, add it to the screen panel
            if (image != null) {
                ImageIcon imageIcon = new ImageIcon(image);
                if (imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    JLabel imageLabel = new JLabel(imageIcon);
                    screen.add(imageLabel, BorderLayout.CENTER);
                    System.out.println("Image added to the screen.");
                } else {
                    System.out.println("Image could not be loaded.");
                }
            }
            // Refresh the screen panel to show the updated information
            screen.revalidate();
            screen.repaint();

            // Set the camera status to off
            isCameraOn = false;
            TablePanel.this.repaint(); // Repaint to remove the red light from the camera icon
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);


            // Draw the background image first
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }


            // Draw the tabletop
            int tabletopWidth = getWidth() - 100; // Table width is less than panel width
            int tabletopHeight = 100; // Arbitrary height for tabletop
            int tabletopX = 50; // X position starts at 50
            int tabletopY = getHeight() - 150; // Y position to allow space for table legs
            g.setColor(Color.LIGHT_GRAY); // Table color
            g.fillRect(tabletopX, tabletopY, tabletopWidth, tabletopHeight);

            // Draw the background image first
            if (oregonImage != null) {
                g.drawImage(oregonImage, 100, 615, 100, 100, this);
            }
            // Draw the table legs
            int legWidth = 10;
            int legHeight = 100;
            g.setColor(Color.DARK_GRAY); // Legs color
            g.fillRect(tabletopX, tabletopY + tabletopHeight, legWidth, legHeight); // Left front leg
            g.fillRect(tabletopX + tabletopWidth - legWidth, tabletopY + tabletopHeight, legWidth, legHeight); // Right front leg

            // Draw the screen stand and screen
            int screenWidth = 550;
            int screenHeight = 300;
            int screenX = getWidth() / 2 - 260;
            int screenY = tabletopY - screenHeight - 50; // Position the screen on the table
            int borderWidth = 5; // The thickness of the screen border
            int standHeight = 50;
            int standWidth = borderWidth; // The width of the stand is the same as the border width for simplicity


            // Draw the stand
            g.setColor(Color.GRAY);
            int standX = screenX + screenWidth / 2 - standWidth / 2;
            int standY = screenY + screenHeight;
            g.fillRect(standX, standY, standWidth, standHeight);

            // Draw the base of the stand (the black line)
            int baseWidth = standWidth + 200; // The base can be slightly wider than the stand
            int baseHeight = 5; // The thickness of the base
            int baseX = standX - (baseWidth - standWidth) / 2;
            int baseY = standY + standHeight; // The base sits directly below the stand

            // Draw the base
            g.setColor(Color.BLACK);
            g.fillRect(baseX, baseY, baseWidth, baseHeight);

            // Draw the black border of the screen
            g.setColor(Color.BLACK);
            g.fillRect(screenX - borderWidth, screenY - borderWidth, screenWidth + 2 * borderWidth, screenHeight + 2 * borderWidth);

            // Draw the screen (white area)
            g.setColor(Color.WHITE);
            g.fillRect(screenX, screenY, screenWidth, screenHeight);


            // Draw the printer image if it's loaded
            if (printerImage != null) {
                int printerX = getWidth() / 2 + 350; // Position the printer next to the screen
                int printerY = getHeight() - 150 - 80; // Align with the screen on the table
                g.drawImage(printerImage, printerX, printerY, this);
            }

            // Draw the camera image if it's loaded
            if (cameraImage != null) {
                int cameraX = getWidth() / 2 - cameraImage.getWidth(null) / 2 + 13; // Centered above the screen
                int cameraY = screenY - cameraImage.getHeight(null) - 2; // Positioned above the screen
                g.drawImage(cameraImage, cameraX, cameraY, this);

                // Draw red light if the camera is on
                if (isCameraOn) {
                    g.setColor(Color.RED);
                    int lightDiameter = 12;
                    int lightX = cameraX + cameraImage.getWidth(null) - 31; // Position the light to the right edge of the camera icon
                    int lightY = cameraY + 6; // Position the light at the top edge of the camera icon
                    g.fillOval(lightX, lightY, lightDiameter, lightDiameter);
                }
            }
        }
    }
}