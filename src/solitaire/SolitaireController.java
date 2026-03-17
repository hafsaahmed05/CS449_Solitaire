package solitaire;

/**
 * SolitaireController connects the SolitaireModel and SolitaireView.
 * It handles user input from the UI and updates the model and view as needed.
 *
 * The controller listens for the New Game button and for clicks on board cells.
 * Moves are handled with a two-click system:
 * the player first selects a peg,
 * then clicks a destination cell to attempt the move.
 */

public class SolitaireController
        implements SolitaireView.ViewListener,
        CellButton.CellClickListener {

    private final SolitaireModel model;
    private final SolitaireView  view;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private static final String DEFAULT_STATUS = "Select a peg and jump over another peg.";

    // Construction
    public SolitaireController(SolitaireModel model, SolitaireView view) {
        this.model = model;
        this.view  = view;

        view.setViewListener(this);
        view.setCellClickListener(this);
        view.setModel(model);
        syncView();
    }

    // ViewListener
    @Override
    public void onNewGame() {
        model.initBoard();
        clearSelection();
        syncView();
        view.setStatus("New game started. Select a peg to move.");
    }

    // CellClickListener
    @Override
    public void onCellClicked(int row, int col) {

        if (!model.isValidCell(row, col)) {
            clearSelection();
            view.setStatus(DEFAULT_STATUS);
            return;
        }

        int state = model.getCellState(row, col);

        if (selectedRow == -1) {

            if (state == SolitaireModel.PEG) {
                selectedRow = row;
                selectedCol = col;
                view.setSelectedCell(row, col);
            } else {
                view.setStatus(DEFAULT_STATUS);
            }

            return;
        }

        if (row == selectedRow && col == selectedCol) {
            clearSelection();
            view.setStatus(DEFAULT_STATUS);
            return;
        }

        if (state == SolitaireModel.PEG) {
            selectedRow = row;
            selectedCol = col;
            view.setSelectedCell(row, col);
            return;
        }

        boolean moved = model.makeMove(selectedRow, selectedCol, row, col);

        clearSelection();

        if (moved) {

            syncView();

            if (model.isWon()) {
                view.showGameOver(true);
            }
            else if (model.isGameOver()) {
                view.showGameOver(false);
            }
            else {
                view.setStatus(DEFAULT_STATUS);
            }

        } else {
            view.setStatus("Invalid move.");
        }
    }

    // Helpers
    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        view.clearSelection();
    }

    private void syncView() {
        view.setPegsLabel(model.getPegsRemaining());
        view.refreshBoard();
    }
}