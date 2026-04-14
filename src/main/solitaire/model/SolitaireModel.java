package main.solitaire.model;
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

    public static final int SIZE_CONST = 7;

    protected int[][] board;
    protected int pegsRemaining;

    // ------------------------------------------------------------------
    // Abstract contract
    // ------------------------------------------------------------------

    public abstract int getGridSize();
    public abstract boolean isValidCell(int row, int col);
    protected abstract int getCentreRow();
    protected abstract int getCentreCol();

    protected int[][] getDirections() {
        return new int[][] {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };
    }

    // ------------------------------------------------------------------
    // Initialisation
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
    // Move logic
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
        if (getCellState(r1, c1) != PEG)   return false;
        if (getCellState(r2, c2) != EMPTY) return false;

        for (int[] d : getDirections()) {
            int midR  = r1 + d[0];
            int midC  = c1 + d[1];
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
                    for (int[] d : getDirections()) {
                        if (makeMove(r, c, r + 2*d[0], c + 2*d[1])) return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sprint 4: Returns {fromRow, fromCol, toRow, toCol} of the next valid
     * move WITHOUT applying it, so the controller can record it before
     * calling makeAnyMove(). Returns null if no move exists.
     */
    public int[] getNextMove() {
        int g = getGridSize();
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                if (board[r][c] == PEG) {
                    for (int[] d : getDirections()) {
                        int toR = r + 2 * d[0];
                        int toC = c + 2 * d[1];
                        if (isLegalMove(r, c, toR, toC)) {
                            return new int[]{r, c, toR, toC};
                        }
                    }
                }
            }
        }
        return null;
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
                    if (board[r][c] == PEG) pegsRemaining++;
                } else {
                    board[r][c] = INVALID;
                }
            }
        }

        // Ensure at least one empty space
        if (board[getCentreRow()][getCentreCol()] == PEG) {
            board[getCentreRow()][getCentreCol()] = EMPTY;
            pegsRemaining--;
        }
    }

    // ------------------------------------------------------------------
    // Game-over detection
    // ------------------------------------------------------------------

    public boolean isGameOver() {
        int g = getGridSize();
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                if (board[r][c] == PEG) {
                    for (int[] d : getDirections()) {
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
    public int getBoardSize()                 { return getGridSize(); }

    // ------------------------------------------------------------------
    // Board state — 2D (existing, used by tests)
    // ------------------------------------------------------------------

    /** Returns a full deep copy of the board as a 2D array. */
    public int[][] getBoardCopy() {
        int g = getGridSize();
        int[][] copy = new int[g][g];
        for (int r = 0; r < g; r++) System.arraycopy(board[r], 0, copy[r], 0, g);
        return copy;
    }

    /** Set board from a 2D array (used by tests). */
    public void setBoardState(int[][] newBoard) {
        int g = getGridSize();
        pegsRemaining = 0;
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                board[r][c] = newBoard[r][c];
                if (board[r][c] == PEG) pegsRemaining++;
            }
        }
    }

    // ------------------------------------------------------------------
    // Board state — flattened 1D (used by GameRecorder)
    // ------------------------------------------------------------------

    /**
     * Returns the board as a flat int[] (row by row).
     * e.g. a 7x7 board → int[49]
     */
    public int[] getBoardState() {
        int g = getGridSize();
        int[] flat = new int[g * g];
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                flat[r * g + c] = board[r][c];
            }
        }
        return flat;
    }

    /**
     * Restores the board from a flat int[] produced by getBoardState().
     * Used by the controller during replay.
     */
    public void setBoardState(int[] flat) {
        int g = getGridSize();
        pegsRemaining = 0;
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                board[r][c] = flat[r * g + c];
                if (board[r][c] == PEG) pegsRemaining++;
            }
        }
    }
}