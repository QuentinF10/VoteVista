import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class CustomTableModel extends DefaultTableModel {
    private boolean[][] selectedCells;

    public CustomTableModel(int numRows, int numColumns) {
        super(numRows, numColumns);
        selectedCells = new boolean[numRows][numColumns];
        for (boolean[] row : selectedCells) {
            Arrays.fill(row, false);
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // This makes all cells non-editable
    }
    public void setSelected(int row, int column, boolean selected) {
        for (int i = 0; i < getRowCount(); i++) {
            selectedCells[i][column] = false;
        }
        selectedCells[row][column] = selected;
        fireTableDataChanged();
    }

    public boolean isSelected(int row, int column) {
        return selectedCells[row][column];
    }
}

