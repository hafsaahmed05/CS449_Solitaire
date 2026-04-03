package solitaire;

/**
 * SolitaireController — wires SolitaireModel and SolitaireView (MVC).
 *
 * Implements:
 *   - SolitaireView.ViewListener   (New Game / Home events)
 *   - CellButton.CellClickListener (cell clicks from BoardPanel)
 *
 * When a new game is requested the controller asks the view which board
 * type and size the user selected, constructs the correct model subclass,
 * and hands it to the view.
 */
public class SolitaireController
        implements SolitaireView.ViewListener,
        CellButton.CellClickListener {

    private SolitaireModel model;
    private final SolitaireView view;

    private int selectedRow = -1;
    private int selectedCol = -1;

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    public SolitaireController(SolitaireView view) {
        this.view = view;
        view.setViewListener(this);
        view.setCellClickListener(this);

        // Start with default English 7
        this.model = new EnglishModel(7);
        view.setModel(model);
        syncView();
    }

    // ------------------------------------------------------------------
    // ViewListener
    // ------------------------------------------------------------------

    @Override
    public void onNewGame() {
        // Read user selections from the view
        String type = view.getSelectedBoardType();  // "English" / "Hexagon" / "Diamond"
        int    size = view.getSelectedBoardSize();

        model = buildModel(type, size);
        clearSelection();
        view.setModel(model);
        syncView();
        view.setStatus("New game started. Select a peg to move.");
    }

    private SolitaireModel buildModel(String type, int size) {
        switch (type) {
            case "Hexagon": return new HexagonModel(size);
            case "Diamond": return new DiamondModel(size);
            default:        return new EnglishModel(size);
        }
    }

    // ------------------------------------------------------------------
    // CellClickListener
    // ------------------------------------------------------------------

    @Override
    public void onCellClicked(int row, int col) {
        if (!model.isValidCell(row, col)) {
            clearSelection();
            return;
        }

        int state = model.getCellState(row, col);

        if (selectedRow == -1) {
            if (state == SolitaireModel.PEG) {
                selectedRow = row;
                selectedCol = col;
                view.setSelectedCell(row, col);
                view.setStatus("Peg selected at (" + row + ", " + col + "). Click a destination.");
            } else {
                view.setStatus("Please click on a peg to select it.");
            }
        } else {
            if (row == selectedRow && col == selectedCol) {
                clearSelection();
                view.setStatus("Selection cleared.");
                return;
            }

            if (state == SolitaireModel.PEG) {
                selectedRow = row;
                selectedCol = col;
                view.setSelectedCell(row, col);
                view.setStatus("Peg re-selected at (" + row + ", " + col + "). Click a destination.");
                return;
            }

            boolean moved = model.makeMove(selectedRow, selectedCol, row, col);
            clearSelection();

            if (moved) {
                syncView();
                if (model.isWon()) {
                    view.showGameOver(true);
                } else if (model.isGameOver()) {
                    view.showGameOver(false);
                }
            } else {
                view.setStatus("Illegal move — select a peg and jump over an adjacent peg.");
            }
        }
    }

    @Override
    public void onAutoplay() {

        new Thread(() -> {

            while (!model.isGameOver()) {

                boolean moved = model.makeAnyMove();

                if (!moved) break;

                try {
                    Thread.sleep(300); // slow down for animation
                } catch (InterruptedException ignored) {}

                javax.swing.SwingUtilities.invokeLater(() -> {
                    syncView();
                });
            }

            javax.swing.SwingUtilities.invokeLater(() -> {
                if (model.isWon()) {
                    view.showGameOver(true);
                } else {
                    view.showGameOver(false);
                }
            });

        }).start();
    }

    @Override
    public void onRandomize() {
        model.randomizeBoard();
        clearSelection();
        syncView();
        view.setStatus("Board randomized!");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

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