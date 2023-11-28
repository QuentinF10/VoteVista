package VoteVista;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;

public class Receipt extends JFrame {
    private JTextPane textPane;

    public Receipt(String userInfo, String voteInfo) {
        setTitle("Voting Record");
        setSize(300, 400);
        textPane = new JTextPane();
        textPane.setEditorKit(new HTMLEditorKit());

        String formattedText = "<html><body>" +
                "<h2 style='text-align: center;'>Voting Record</h2>" +
                "<b>Your informations</b><br/>" + userInfo + "<br/><br/>" +
                "<b>Voted for:</b><br/>" + voteInfo +
                "</body></html>";

        textPane.setText(formattedText);
        textPane.setEditable(false);
        textPane.setContentType("text/html");

        JScrollPane scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);

        JButton printButton = new JButton("Print Receipt");
        printButton.addActionListener(this::printReceipt);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(this::quitApplication);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(printButton);
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void printReceipt(ActionEvent event) {
        try {
            textPane.print();
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Failed to print receipt.", "Print Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void quitApplication(ActionEvent event) {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            window.dispose();
        }
        System.exit(0);
    }
}
