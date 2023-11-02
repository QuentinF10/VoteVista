import javax.swing.*;

public class VoteVista {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Welcome to the voting system!");
            new VoteVistaUI();
        });

    }
}
