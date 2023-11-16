import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

class CenterTableCellRenderer extends DefaultTableCellRenderer {
    public CenterTableCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER); // Set text to be centered
    }
}
