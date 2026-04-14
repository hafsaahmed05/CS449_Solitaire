package main.solitaire.view;

import javax.swing.*;
import java.awt.*;

import main.solitaire.model.*;

/**
 * BoardPanel is a JPanel that displays the game board using a grid of CellButtons.
 * - Creates a CellButton for each cell on the board
 * - Updates the buttons to match the current state of the model
 * - Forwards click events to the controller
 * - Keeps track of which cell is currently selected and highlights it
 *
 * Decoupling improvements (Sprint 4):
 * - Uses CellButton.CellState enum instead of SolitaireModel int constants
 * - Uses model.needsRowOffset() instead of instanceof HexagonModel
 */
public class BoardPanel extends JPanel implements CellButton.CellClickListener {

    // State
    private SolitaireModel model;
    private CellButton[][] cells;

    /** External listener supplied by the controller/view. */
    private CellButton.CellClickListener externalListener;

    private int selectedRow = -1;
    private int selectedCol = -1;

    // Construction
    public BoardPanel() {
        setOpaque(false);
    }

    // ── Model binding ────────────────────────────────────────────────

    public void setModel(SolitaireModel model) {
        this.model = model;
        rebuildGrid();
    }

    private void rebuildGrid() {
        removeAll();

        int size = model.getBoardSize();
        cells = new CellButton[size][size];

        // Use model method instead of instanceof — reduces coupling
        boolean needsOffset = model.needsRowOffset();

        setLayout(new GridLayout(size, 1));

        for (int r = 0; r < size; r++) {
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
            rowPanel.setOpaque(false);

            if (needsOffset && r % 2 != 0) {
                rowPanel.add(Box.createHorizontalStrut(CellButton.CELL_SIZE / 2));
            }

            rowPanel.add(Box.createHorizontalGlue());

            for (int c = 0; c < size; c++) {
                int raw = model.getCellState(r, c);
                CellButton.CellState state = toCellState(raw);

                if (state != CellButton.CellState.INVALID) {
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

    // ── Refresh / selection ──────────────────────────────────────────

    public void refresh() {
        if (model == null || cells == null) return;

        int size = model.getBoardSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (cells[r][c] != null) {
                    cells[r][c].setCellState(toCellState(model.getCellState(r, c)));
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

    // ── CellClickListener — forward to the external listener ─────────

    public void setCellClickListener(CellButton.CellClickListener listener) {
        this.externalListener = listener;
    }

    @Override
    public void onCellClicked(int row, int col) {
        if (externalListener != null) {
            externalListener.onCellClicked(row, col);
        }
    }

    // ── Private helpers ──────────────────────────────────────────────

    /**
     * Maps a model int state to a view-level CellState enum.
     * This is the only place BoardPanel knows about model int constants,
     * keeping the mapping contained in one method.
     */
    private CellButton.CellState toCellState(int s) {
        if (s == SolitaireModel.PEG)   return CellButton.CellState.PEG;
        if (s == SolitaireModel.EMPTY) return CellButton.CellState.EMPTY;
        return CellButton.CellState.INVALID;
    }
}