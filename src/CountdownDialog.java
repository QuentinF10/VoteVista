import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CountdownDialog extends JDialog {
    private JLabel countdownLabel;
    private int countdown;
    private Timer countdownTimer;

    public CountdownDialog(JFrame owner, String title, boolean modal, int countdownSeconds) {
        super(owner, title, modal);
        this.countdown = countdownSeconds;
        initUI();
        startCountdown();
    }

    private void initUI() {
        countdownLabel = new JLabel("", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Serif", Font.BOLD, 20));
        add(countdownLabel, BorderLayout.CENTER);
        setSize(300, 200);
        setLocationRelativeTo(null);
    }

    private void startCountdown() {
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdown > 0) {
                    countdownLabel.setText("Stand still to the camera. Picture in: " + countdown + " seconds");
                    countdown--;
                } else {
                    countdownTimer.stop();
                    CountdownDialog.this.dispose();
                    // Here you can trigger the photo capture process
                }
            }
        });
        countdownTimer.start();
    }

    // Getter method for the countdown
    public int getCountdown() {
        return countdown;
    }
}
