import javax.swing.*;

public class VoteVista {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            new VoteVistaUI();
        });

    }
}
