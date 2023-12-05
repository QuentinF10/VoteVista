import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableCellRenderer;


public class VoteVistaUI {
    public JFrame frame;
    private TablePanel tablePanel;
    private Image printerImage, cameraImage, backgroundImage, oregonImage, adminImage,handsImage;
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    public boolean idScanned = false, isReceipt = false;
    public String firstName, lastName, DateOfBirth;
    public CustomTableModel mainModel;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/votevista";
    private static final String USER = "root";
    private static final String PASS = "root";

    public VoteVistaUI() {

        NetworkCheck networkCheck = new NetworkCheck(this);
        networkCheck.startNetworkMonitoring();
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
        frame.setLocationRelativeTo(null);
        frame.setResizable(false); // Prevent the window from being resized

        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File("src/american.jpg")); // Replace with your background image path
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }
        // Load and resize the printer image
        try {
            Image originalImage = ImageIO.read(new File("src/printer.png")); // Replace with your image path
            // Resize image to new width and height
            int imageWidth = 150; // Desired width
            int imageHeight = 150; // Desired height
            printerImage = originalImage.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            printerImage = null;
        }

        try {
            Image originalhandsImage = ImageIO.read(new File("src/mains.png")); // Replace with your image path
            // Resize image to new width and height
            int imageWidth = 500; // Desired width
            int imageHeight = 600; // Desired height
            handsImage = originalhandsImage.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            handsImage = null;
        }
        // Load the oregon image
        try {
            oregonImage = ImageIO.read(new File("src/oregon.png")); // Replace with your background image path
        } catch (IOException e) {
            e.printStackTrace();
            oregonImage = null;
        }

        // Load and resize the camera image
        try {
            Image originalCameraImage = ImageIO.read(new File("src/camera.png")); // Replace with your camera image path
            int cameraImageWidth = 50; // Desired width
            int cameraImageHeight = 30; // Desired height
            cameraImage = originalCameraImage.getScaledInstance(cameraImageWidth, cameraImageHeight, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            cameraImage = null;
        }


        try {
            Image originalAdminImage = ImageIO.read(new File("src/admin.png")); // Replace with your image path
            int adminImageWidth = 30; // Desired width
            int adminImageHeight = 30; // Desired height
            adminImage = originalAdminImage.getScaledInstance(adminImageWidth, adminImageHeight, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            adminImage = null;
        }
        // Create the table panel where the screen and printer will be placed
        tablePanel = new TablePanel();
        frame.add(tablePanel);
        // Display the window.
        frame.setVisible(true);
    }


    // Inner class for table panel
    class TablePanel extends JPanel {
        private JPanel screen;
        private JLabel screenMessage,admin;
        // New field to track camera status
        private boolean isCameraOn = false, isPrinterOn = false;
        private JLabel paperLabel;
        private boolean isDraggingPaper = false;
        private int paperCount = 20; // Number of papers in the stack
        private Receipt currentReceipt = null;



        public TablePanel() {


            // Initialize the paper label (invisible at first)
            paperLabel = new JLabel();
            paperLabel.setBackground(Color.WHITE); // Set the paper color
            paperLabel.setOpaque(true);
            paperLabel.setSize(100, 70); // Set paper size
            paperLabel.setVisible(false);

            // Mouse listener for the paper stack
            this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (isClickOnPaperStack(e.getPoint()) && paperCount > 0 && !isDraggingPaper && !isPrinterOn) {
                        takeOnePaper(e.getPoint());
                    }
                }
            });

            // Mouse motion listener for dragging the paper
            paperLabel.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (isDraggingPaper) {
                        Point p = SwingUtilities.convertPoint(paperLabel, e.getPoint(), TablePanel.this);
                        paperLabel.setLocation(p.x - paperLabel.getWidth() / 2, p.y - paperLabel.getHeight() / 2);
                    }
                }
            });

            // Mouse listener for dropping the paper
            paperLabel.addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    if (isDraggingPaper && isPaperOverPrinter(paperLabel.getLocation())) {
                        System.out.println("Paper dropped on printer!");
                        isPrinterOn = true;
                        // Decrease paper count and reset label
                        paperCount--;
                        isDraggingPaper = false;
                        paperLabel.setVisible(false);
                        repaint(); // Repaint to update the paper stack

                        // Trigger the voting process here
                    } else if (isDraggingPaper) {
                        // If not dropped on printer, hide the paper again
                        paperLabel.setVisible(false);
                        isDraggingPaper = false;
                        isPrinterOn = false;
                    }
                }
            });

            this.add(paperLabel);




            // Initialize the screen as a JPanel
            screen = new JPanel();
            screen.setBounds(273, 263, 700, 300); // Set the position and size to match your screen area
            screen.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5)); // Black border to represent the screen edge


            // Initialize and add the admin label
            if (adminImage != null) {
                admin = new JLabel(new ImageIcon(adminImage));
                int xPos = screen.getWidth() - adminImage.getWidth(this) - 10;
                int yPos = 10;
                admin.setBounds(xPos, yPos, adminImage.getWidth(this), adminImage.getHeight(this));

                admin.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        AdminDialog loginDialog = new AdminDialog(frame); // 'frame' is your main JFrame
                        loginDialog.setVisible(true);
                    }
                });
                screen.add(admin);
            }

            // Initialize the screen message as a JLabel
            screenMessage = new JLabel("<html><center>Welcome To Oregon State,<br>Drop paper on printer & Touch the screen to start voting</center></html>");
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
                    if(isPrinterOn) {
                        displayStep();
                    }
                }
            });

            // Add the screen to the TablePanel
            this.setLayout(null); // Use absolute positioning
            this.add(screen);
        }

        public boolean hasVoted(String firstName, String lastName) {
            boolean exists = false;
            String query = "SELECT COUNT(*) FROM voters WHERE FirstName = ? AND LastName = ?";

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // If count is greater than 0, then the voter exists in the table
                        exists = rs.getInt(1) > 0;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exceptions appropriately
            }

            return exists;
        }


        public void displayStep() {
            boolean voterExists = hasVoted(firstName, lastName);
            if(!idScanned) {
                // Create a JOptionPane
                JOptionPane optionPane = new JOptionPane(
                        "<html><center>First, scan your National Identification Card</center></html>",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Create a JDialog from JOptionPane
                JDialog dialog = optionPane.createDialog("Instructions");

                // Set location
                dialog.setLocation(screen.getX()+200, screen.getY()+120); // Set your desired X and Y coordinates
                //dialog.setLocationRelativeTo(null);

                // Show the dialog
                dialog.setVisible(true);

                // After the dialog is dismissed
                startQRScanning();
            }
            else{
                if (idScanned && !voterExists) {
                    // Create a JOptionPane
                    JOptionPane optionPane = new JOptionPane(
                            "<html><center>Now, scan your Voter Identification Card</center></html>",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Create a JDialog from JOptionPane
                    JDialog dialog = optionPane.createDialog("Instructions");

                    // Set location
                    dialog.setLocation(screen.getX()+200, screen.getY()+120); // Set your desired X and Y coordinates

                    //dialog.setLocationRelativeTo(null);
                    // Show the dialog
                    dialog.setVisible(true);

                    // After the dialog is dismissed
                    startQRScanning();
                }
                else{
                    JOptionPane.showMessageDialog(null, "You have already voted. VoteVista will reset.", "Voting Error", JOptionPane.ERROR_MESSAGE);
                    resetVotingProcess();

                }
            }
        }

        // Method to update the screen with vote summary
        public void showVoteSummary(Map<String, String> selectedVotes) {
            screen.removeAll(); // Clear the existing content
            screen.setLayout(new BoxLayout(screen, BoxLayout.Y_AXIS)); // Set layout for the summary

            // Create a title label for the summary
            JLabel summaryTitle = new JLabel("Vote Summary");
            summaryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Add a line break
            screen.add(Box.createRigidArea(new Dimension(0, 20)));
            summaryTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
            summaryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            screen.add(summaryTitle);

            // Add labels for each selected vote
            for (Map.Entry<String, String> entry : selectedVotes.entrySet()) {
                String position = entry.getKey();
                String candidate = entry.getValue();
                JLabel label = new JLabel(position + ": " + candidate +"\n");
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                screen.add(label);
            }
            // Add a line break
            screen.add(Box.createRigidArea(new Dimension(0, 20)));

            // Change My Vote Button
            JButton changeVoteButton = new JButton("Change My Vote");
            changeVoteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            changeVoteButton.addActionListener(e -> displayVotingInterface());
            screen.add(changeVoteButton);

            // Print Button
            JButton printButton = new JButton("Print");
            printButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            printButton.addActionListener(e -> showPrintingMessageAndPrintSummary(selectedVotes));
            screen.add(printButton);
            screen.setBackground(Color.WHITE);

            screen.revalidate();
            screen.repaint();
        }

        public int getVoterID(String firstName, String lastName) {
            int voterID = -1;
            String selectSql = "SELECT VoterID FROM voters WHERE FirstName = ? AND LastName = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    voterID = rs.getInt("VoterID");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return voterID;
        }

        public void insertVotes(Map<String, String> selectedVotes, int voterID) {
            String insertSql = "INSERT INTO votes (VoterID, CandidateID, PositionID, Timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

                System.out.println("Inserting votes for VoterID: " + voterID);

                for (Map.Entry<String, String> vote : selectedVotes.entrySet()) {
                    String position = vote.getKey();
                    String candidateName = vote.getValue();

                    int candidateID = getCandidateIDByName(candidateName);
                    int positionID = getPositionIDByName(position);

                    System.out.println("Position: " + position + " (" + positionID + ")");
                    System.out.println("Candidate: " + candidateName + " (" + candidateID + ")");

                    // Only proceed if the candidateID is valid (greater than 0)
                    if (candidateID > 0) {
                        pstmt.setInt(1, voterID);
                        pstmt.setInt(2, candidateID);
                        pstmt.setInt(3, positionID);

                        // Print the prepared statement to check the final SQL query
                        System.out.println("Executing SQL: " + pstmt.toString());

                        int affectedRows = pstmt.executeUpdate();
                        System.out.println("Rows affected: " + affectedRows);
                    } else {
                        // Handle the case where the candidate ID was not found
                        System.out.println("Error: Candidate not found - " + candidateName);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("SQLException: " + e.getMessage());
            }
        }



        public void insertVoterDetails(String firstName, String lastName, String dateOfBirth) {
            // SQL query to insert a new voter
            String insertSql = "INSERT INTO voters (FirstName, LastName, DateOfBirth) VALUES (?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date dob = dateFormat.parse(dateOfBirth);
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setDate(3, new java.sql.Date(dob.getTime()));
                pstmt.executeUpdate();
                System.out.println("Voter details inserted successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void showPrintingMessageAndPrintSummary(Map<String, String> selectedVotes) {
            insertVoterDetails(firstName, lastName, DateOfBirth);
            int voterID = getVoterID(firstName, lastName);
            insertVotes(tablePanel.getSelectedVotes(), voterID);
            screen.removeAll();

            // Update the screen message
            screenMessage.setText("<html><center>Thanks for voting. Your ballot is now printing...</center></html>");
            screenMessage.setForeground(Color.BLACK);
            screenMessage.setHorizontalAlignment(JLabel.CENTER);
            screenMessage.setVerticalAlignment(JLabel.CENTER);

            // Add the message to the screen panel
            screen.add(screenMessage);
            screen.setLayout(new BorderLayout());
            screen.add(screenMessage, BorderLayout.CENTER);

            screen.revalidate();
            screen.repaint();

            // Create a timer to wait for a few seconds before printing the summary
            new javax.swing.Timer(5000, e -> {
                printVoteSummary(selectedVotes);
                ((javax.swing.Timer)e.getSource()).stop(); // Stop the timer after execution
            }).start();
        }

        private void printVoteSummary(Map<String, String> selectedVotes) {
            String formattedDateOfBirth = "";
            try {
                // Parse the DateOfBirth String into a Date object using the correct format
                Date dob = new SimpleDateFormat("MM/dd/yyyy").parse(DateOfBirth);
                // Format the Date object into a String for display in the format "dd/MM/yyyy"
                formattedDateOfBirth = new SimpleDateFormat("dd/MM/yyyy").format(dob);
            } catch (ParseException e) {
                System.out.println("Failed to parse DateOfBirth: " + DateOfBirth);
                e.printStackTrace();
                formattedDateOfBirth = "Invalid Date";
            }

            String userInfo = String.format("<b>Name:</b> %s %s<br/><b>Date of Birth:</b> %s", firstName, lastName, formattedDateOfBirth);
            currentReceipt = new Receipt(userInfo, selectedVotes.toString());
            isReceipt = true;
            isPrinterOn = false;

            screen.setBounds(0, 0, 0, 0);
            tablePanel.revalidate();
            tablePanel.repaint();

            // Use glass pane to capture mouse clicks
            JPanel glassPane = (JPanel) frame.getGlassPane();
            glassPane.setVisible(true);
            glassPane.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    resetVotingProcess();
                    glassPane.setVisible(false);
                    glassPane.removeMouseListener(this);
                }
            });
        }

        public void resetVotingProcess() {
            // Remove the old tablePanel
            frame.remove(tablePanel);

            // Create a new instance of tablePanel
            tablePanel = new TablePanel();

            // Add the new tablePanel to the frame
            frame.add(tablePanel);
            frame.revalidate();
            frame.repaint();
            isReceipt = false;
        }
        private boolean isClickOnPaperStack(Point clickPoint) {
            // Define the area where the paper stack is
            Rectangle paperStackArea = new Rectangle(800, 620, 100, 150);
            return paperStackArea.contains(clickPoint);
        }

        private void takeOnePaper(Point startPoint) {
            paperLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            paperLabel.setLocation(startPoint);
            paperLabel.setVisible(true);
            isDraggingPaper = true;
        }
        private boolean isPaperOverPrinter(Point location) {
            // Define the printer area bounds
            // (You need to adjust these values based on your UI)
            Rectangle printerArea = new Rectangle(getWidth() / 2 + 385, getHeight() - 150 - 80,150,150);
            return printerArea.contains(paperLabel.getBounds());
        }
        public void setupWebcam() {
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
        }


        public void startQRScanning() {

            setupWebcam();

            QRScanner qrScanner = new QRScanner(this, VoteVistaUI.this);
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
                                if(!idScanned){
                                    qrScanner.handleQRCodeIDResult(result);
                                }else {
                                    qrScanner.handleQRCodeVotingResult(result);
                                }

                                break; // Break the loop if you want to stop scanning after the first QR code is found
                            }
                        } catch (NotFoundException e) {
                            // This is normal, not every frame will contain a QR code
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
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

        public void promptForIDScan() {
            // Show a message to the user
            JOptionPane.showMessageDialog(frame, "Scan your ID again.");
            // Clear the screen and prepare it for showing the webcam again
            screen.removeAll();
            screen.revalidate();
            screen.repaint();

            // Start the webcam for scanning
            startQRScanning();
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


        public void displayVotingInterface() {
            // Stop the webcam feed and remove the webcam view
            // webcam.close(); // Uncomment if webcam is being used
            screen.removeAll();

            // Initialize the custom table model
            mainModel = new CustomTableModel(5, 8);

            mainModel.setColumnIdentifiers(new Object[]{"","U.S. Senator", "State Senator", "State Representative", "Commissioner", "Sheriff", "Mayor", "District Judge"});
            // Populate the table with cand idate names (example names used here)
            //Names of candidates
            Object[] candidates = {
                    // Democratic
                    "Lillie Matthews", "Ben Reed", "Angel Parker", "Danielle Anders", "John Jordan", "Rhonda Barker", "Louis McCoy",
                    // Republican
                    "Douglas Key", "Mari Ortiz", "Steve Son", "Karen Morrison", "Justin Hanks", "Yvonne Owens", "Patricia Francis",
                    // Conservative
                    "Kyle Burch", "Corey Klein", "Angie Franklin", "Penny Carver", "Bill Borchart", "Jeremy Leon", "Samuel Welch",
                    // Working Families
                    "David Tapia", "Carl Patterson", "Kathy Korver", "Evan Elliot", "Tanner Harford", "Simon Crane", "Leslie Sanchez",
                    // Independence
                    "Terri Carter", "Bell Francine", "Theodore Smith", "Arturo Cervan", "Scott Marion", "Antoine Scott", "Jerry Baker"
            };

            int candidateIndex = 0;

            String[] rowNames = {"Democratic", "Republican", "Conservative", "Working Families", "Independence"};
            for (int i = 0; i < rowNames.length; i++) {
                mainModel.setValueAt(rowNames[i], i, 0);
            }
            for (int row = 0; row < mainModel.getRowCount(); row++) {
                for (int col = 1; col < mainModel.getColumnCount(); col++) {
                    if (candidateIndex < candidates.length) {
                        mainModel.setValueAt(candidates[candidateIndex], row, col);
                        candidateIndex++;
                    } else {
                        mainModel.setValueAt("", row, col);
                    }
                }
            }

            // Create and set up the main table
            JTable mainTable = new JTable(mainModel);
            mainTable.setRowHeight(screen.getHeight() / 6); // Adjust row height


            // Custom cell renderer
            mainTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (((CustomTableModel) table.getModel()).isSelected(row, column)) {
                        c.setBackground(Color.GREEN);
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                    return c;
                }
            });

            // Mouse listener for cell selection
            mainTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = mainTable.rowAtPoint(e.getPoint());
                    int column = mainTable.columnAtPoint(e.getPoint());
                    if(column>0){
                        mainModel.setSelected(row, column, !mainModel.isSelected(row, column));
                    }

                    // Check if at least one cell in each column is selected
                    if (isEachColumnSelected(mainTable)) {
                        int confirm = JOptionPane.showConfirmDialog(
                                frame,
                                "All columns have at least one selection. Do you want to see a recap of your votes?",
                                "Confirm Vote Recap",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            // Logic to show recap of votes
                            displayVoteSummaryAndProceed();
                            // Add logic to print the vote summary

                        }
                    }

                }
            });


            // Add the table to the screen within a scroll pane
            JScrollPane scrollPane = new JScrollPane(mainTable);
            screen.add(scrollPane, BorderLayout.CENTER);

            // Update the screen
            screen.revalidate();
            screen.repaint();
        }

        private void displayVoteSummaryAndProceed() {
            Map<String, String> selectedVotes = getSelectedVotes();
            tablePanel.showVoteSummary(selectedVotes);
        }

        private Map<String, String> getSelectedVotes() {
            Map<String, String> selectedVotes = new HashMap<>();

            for (int col = 1; col < mainModel.getColumnCount(); col++) {
                for (int row = 0; row < mainModel.getRowCount(); row++) {
                    if (mainModel.isSelected(row, col)) {
                        String position = mainModel.getColumnName(col);
                        String candidate = (String) mainModel.getValueAt(row, col);
                        selectedVotes.put(position, candidate);
                        break; // Assuming only one candidate can be selected per position
                    }
                }
            }
            return selectedVotes;
        }

        public void insertVotesIntoDatabase(Map<String, String> selectedVotes, String voterId) {
            // Assuming you have a `Connection` object named `conn`
            String insertSql = "INSERT INTO votes (VoterID, CandidateID, PositionID, Timestamp) VALUES (?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

                // You would need to get the current timestamp to insert
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                for (Map.Entry<String, String> vote : selectedVotes.entrySet()) {
                    String positionName = vote.getKey();
                    String candidateName = vote.getValue();

                    // Assuming you have methods `getPositionIDByName` and `getCandidateIDByName` to get IDs
                    int positionID = getPositionIDByName(positionName);
                    int candidateID = getCandidateIDByName(candidateName);

                    pstmt.setString(1, voterId);
                    pstmt.setInt(2, candidateID);
                    pstmt.setInt(3, positionID);
                    pstmt.setTimestamp(4, timestamp);

                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exceptions, maybe show a dialog to the user that something went wrong
            }
        }

        public int getPositionIDByName(String positionName) {
            String query = "SELECT PositionID FROM positions WHERE PositionName = ?";
            int positionID = -1;

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, positionName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        positionID = rs.getInt("PositionID");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exceptions, maybe show a dialog to the user that something went wrong
            }

            return positionID;
        }

        public int getCandidateIDByName(String candidateName) {
            // Assuming candidateName is a full name in "First Last" format
            String[] parts = candidateName.split(" ");
            String firstName = parts[0];
            String lastName = parts[1];

            String query = "SELECT CandidateID FROM candidates WHERE FirstName = ? AND LastName = ?";
            int candidateID = -1;

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        candidateID = rs.getInt("CandidateID");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exceptions, maybe show a dialog to the user that something went wrong
            }

            return candidateID;
        }


        private boolean isEachColumnSelected(JTable table) {
            CustomTableModel model = (CustomTableModel) table.getModel();
            for (int col = 1; col < table.getColumnCount(); col++) {
                boolean columnHasSelection = false;
                for (int row = 0; row < table.getRowCount(); row++) {
                    if (model.isSelected(row, col)) {
                        columnHasSelection = true;
                        break;
                    }
                }
                if (!columnHasSelection) {
                    return false;
                }
            }
            return true;
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
            int screenWidth = 700;
            int screenHeight = 300;
            int screenX = getWidth() / 2 - 320;
            int screenY = tabletopY - screenHeight - 50; // Position the screen on the table
            int borderWidth = 5; // The thickness of the screen border
            int standHeight = 50;
            int standWidth = borderWidth; // The width of the stand is the same as the border width for simplicity


            // Draw the stand
            g.setColor(Color.GRAY);
            int standX = 610;
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
                int printerX = getWidth() / 2 + 385; // Position the printer next to the screen
                int printerY = getHeight() - 150 - 80; // Align with the screen on the table
                g.drawImage(printerImage, printerX, printerY, this);

                if (isPrinterOn) {
                    g.setColor(Color.GREEN);
                    int lightDiameter = 12;
                    int lightX = printerX + 115;
                    int lightY = printerY + 45;
                    g.fillOval(lightX, lightY, lightDiameter, lightDiameter);
                }
            }

            drawPaperStack(g, 800, 620); // Adjust x and y to position the stack



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

            if (isReceipt){
                if (handsImage != null && currentReceipt != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    // Draw the hands image
                    int imageX = handsImage.getWidth(null) -150; // X coordinate
                    int imageY = handsImage.getHeight(null)-425; // Y coordinate
                    g2d.drawImage(handsImage, imageX, imageY, this);

                    // Render HTML content on image
                    JEditorPane editorPane = new JEditorPane();
                    editorPane.setContentType("text/html");
                    editorPane.setText(currentReceipt.getFormattedText());
                    // You may need to position and size the editorPane appropriately
                    editorPane.setSize(new Dimension(150, 1));
                    editorPane.setBackground(new Color(0,0,0,0));
                    g2d.translate(imageX+150, imageY+100);
                    editorPane.paint(g2d);
                }
            }

        }
        private void drawPaperStack(Graphics g, int x, int y) {
            int paperWidth = 100;
            int paperHeight = 70;
            int shadowOffset = 2; // Offset for the shadow effect

            for (int i = 0; i < paperCount; i++) {
                // Draw shadow
                g.setColor(Color.GRAY);
                g.fillRect(x + shadowOffset, y + shadowOffset + i, paperWidth, paperHeight);

                // Draw paper
                g.setColor(Color.WHITE);
                g.fillRect(x, y + i, paperWidth, paperHeight);
            }
        }
    }
    public void updateUIForNetworkIssue() {
        // Call this method from the Swing event dispatch thread
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, "Connection lost, restart voting process.");
            tablePanel.resetVotingProcess();
        });
    }

}