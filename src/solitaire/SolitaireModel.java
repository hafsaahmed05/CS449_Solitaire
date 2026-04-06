package solitaire;

/**
 * SolitaireModel — abstract base class for all board types.
 *
 * All game logic (move validation, game-over detection, peg counting) lives
 * here and is shared by every board type.
 *
 * Subclasses must implement:
 *   - getGridSize()     : the width/height of the backing 2-D array
 *   - isValidCell()     : which cells belong to this board shape
 *   - getCentreRow/Col(): which cell starts empty
 */
public abstract class SolitaireModel {

    public static final int INVALID = -1;
    public static final int EMPTY   =  0;
    public static final int PEG     =  1;

    // Still needed by SolitaireView / BoardPanel for sizing the English board.
    // Each subclass exposes its own grid size via getGridSize().
    public static final int SIZE_CONST = 7;

    private static final int[][] DIRECTIONS = {
            {-1,  0}, { 1,  0}, { 0, -1}, { 0,  1}
    };

    protected int[][] board;
    protected int pegsRemaining;

    // ------------------------------------------------------------------
    // Abstract contract
    // ------------------------------------------------------------------

    /** Side length of the square backing array (may be larger than "size"). */
    public abstract int getGridSize();

    /** True when (row, col) is a playable cell for this board shape. */
    public abstract boolean isValidCell(int row, int col);

    /** Row index of the cell that starts empty. */
    protected abstract int getCentreRow();

    /** Column index of the cell that starts empty. */
    protected abstract int getCentreCol();

    protected int[][] getDirections() {
        return new int[][] {
                {-1, 0}, {1, 0},   // vertical
                {0, -1}, {0, 1}    // horizontal
        };
    }
    // ------------------------------------------------------------------
    // Initialisation  (shared)
    // ------------------------------------------------------------------

    public void initBoard() {
        int g = getGridSize();
        board = new int[g][g];
        pegsRemaining = 0;

        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                if (isValidCell(r, c)) {
                    board[r][c] = PEG;
                    pegsRemaining++;
                } else {
                    board[r][c] = INVALID;
                }
            }
        }

        board[getCentreRow()][getCentreCol()] = EMPTY;
        pegsRemaining--;
    }

    // ------------------------------------------------------------------
    // Move logic  (shared)
    // ------------------------------------------------------------------

    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isLegalMove(fromRow, fromCol, toRow, toCol)) return false;

        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;

        board[fromRow][fromCol] = EMPTY;
        board[midRow][midCol]   = EMPTY;
        board[toRow][toCol]     = PEG;
        pegsRemaining--;
        return true;
    }

    public boolean isLegalMove(int r1, int c1, int r2, int c2) {

        if (!isValidCell(r1, c1) || !isValidCell(r2, c2)) return false;
        if (getCellState(r1, c1) != PEG) return false;
        if (getCellState(r2, c2) != EMPTY) return false;

        for (int[] d : getDirections()) {

            int midR = r1 + d[0];
            int midC = c1 + d[1];

            int destR = r1 + 2 * d[0];
            int destC = c1 + 2 * d[1];

            if (destR == r2 && destC == c2) {
                return isValidCell(midR, midC)
                        && getCellState(midR, midC) == PEG;
            }
        }

        return false;
    }

    public boolean makeAnyMove() {
        int g = getGridSize();
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                if (board[r][c] == PEG) {
                    for (int[] d : getDirections()) {  // FIXED
                        if (makeMove(r, c, r + 2*d[0], c + 2*d[1])) return true;
                    }
                }
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Randomize Board
    // ------------------------------------------------------------------
    public void randomizeBoard() {
        java.util.Random rand = new java.util.Random();

        int g = getGridSize();
        pegsRemaining = 0;

        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {

                if (isValidCell(r, c)) {
                    board[r][c] = rand.nextBoolean() ? PEG : EMPTY;

                    if (board[r][c] == PEG) {
                        pegsRemaining++;
                    }
                } else {
                    board[r][c] = INVALID;
                }
            }
        }

        // Ensure at least one empty space (so moves are possible)
        int centerR = getCentreRow();
        int centerC = getCentreCol();

        if (board[centerR][centerC] == PEG) {
            board[centerR][centerC] = EMPTY;
            pegsRemaining--;
        }
    }

    // ------------------------------------------------------------------
    // Game-over detection  (shared)
    // ------------------------------------------------------------------

    public boolean isGameOver() {
        int g = getGridSize();
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                if (board[r][c] == PEG) {
                    for (int[] d : getDirections()) {  // FIXED
                        if (isLegalMove(r, c, r + 2*d[0], c + 2*d[1])) return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isWon() { return pegsRemaining == 1; }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    public int getCellState(int row, int col) { return board[row][col]; }
    public int getPegsRemaining()             { return pegsRemaining; }

    /** Convenience alias so old code calling getBoardSize() still works. */
    public int getBoardSize() { return getGridSize(); }

    // ------------------------------------------------------------------
    // Test helpers  (package-private)
    // ------------------------------------------------------------------

    void setBoardState(int[][] newBoard) {
        int g = getGridSize();
        pegsRemaining = 0;
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                board[r][c] = newBoard[r][c];
                if (board[r][c] == PEG) pegsRemaining++;
            }
        }
    }

    public int[][] getBoardCopy() {
        int g = getGridSize();
        int[][] copy = new int[g][g];
        for (int r = 0; r < g; r++) System.arraycopy(board[r], 0, copy[r], 0, g);
        return copy;
    }
}