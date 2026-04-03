package solitaire;

import javax.swing.*;
import java.awt.*;

/**
 * BoardPanel is a JPanel that displays the game board using a grid of CellButtons:
 * - creates a CellButton for each cell on the board
 * - updates the buttons to match
 * - the current state of the model
 * - forwards click events to the controller
 * - keeps track of which cell is currently selected and highlights it
 */

public class BoardPanel extends JPanel implements CellButton.CellClickListener {

    // State
    private SolitaireModel   model;
    private CellButton[][]   cells;

    /** External listener supplied by the controller/view. */
    private CellButton.CellClickListener externalListener;

    private int selectedRow = -1;
    private int selectedCol = -1;

    // Construction
    public BoardPanel() {
        setOpaque(false);
        // Layout is set once the model is attached
    }

    // Model binding

    /**
     * Attach a model and build the cell grid.
     * Safe to call multiple times (e.g. on New Game with a new model instance).
     */
    public void setModel(SolitaireModel model) {
        this.model = model;
        rebuildGrid();
    }

    private void rebuildGrid() {
        removeAll();

        int size = model.getBoardSize();
        cells = new CellButton[size][size];

        setLayout(new GridLayout(size, 1)); // one row per row

        for (int r = 0; r < size; r++) {

            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
            rowPanel.setOpaque(false);

            rowPanel.add(Box.createHorizontalGlue());

            for (int c = 0; c < size; c++) {
                int state = model.getCellState(r, c);

                if (state != SolitaireModel.INVALID) {
                    CellButton btn = new CellButton(r, c, state, this);
                    cells[r][c] = btn;
                    rowPanel.add(btn);
                } else {
                    cells[r][c] = null;
                }
            }

            rowPanel.add(Box.createHorizontalGlue());

            add(rowPanel);
        }

        revalidate();
        repaint();
    }

    // Refresh / selection
    public void refresh() {
        if (model == null || cells == null) return;

        int size = model.getBoardSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (cells[r][c] != null) {
                    cells[r][c].setCellState(model.getCellState(r, c));
                    cells[r][c].setSelected(r == selectedRow && c == selectedCol);
                }
            }
        }
    }

    public void setSelectedCell(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        refresh();
    }

    public void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        refresh();
    }

    // CellClickListener — forward to the external listener (controller)

    /** Register the upstream listener (set by SolitaireView). */
    public void setCellClickListener(CellButton.CellClickListener listener) {
        this.externalListener = listener;
    }

    @Override
    public void onCellClicked(int row, int col) {
        if (externalListener != null) {
            externalListener.onCellClicked(row, col);
        }
    }
}