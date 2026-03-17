package solitaire;

/**
 * SolitaireModel contains the game logic for a standard English Peg Solitaire board (size 7).
 *
 * Each cell on the board can be:
 *   INVALID (-1) – not part of the playable board
 *   EMPTY   (0)  – a valid spot with no peg
 *   PEG     (1)  – a peg is present
 *
 * As of now, the board uses the traditional English cross shape. The center cell (3,3)
 * starts empty and all other valid cells start with a peg.
 */

public class SolitaireModel {

    public static final int INVALID = -1;
    public static final int EMPTY   =  0;
    public static final int PEG     =  1;

    private static final int SIZE = 7;
    /** Exposed so the view can size itself without hard-coding 7. */
    public  static final int SIZE_CONST = SIZE;

    private int[][] board;
    private int pegsRemaining;

    // Four orthogonal directions: {dRow, dCol}
    private static final int[][] DIRECTIONS = {
            {-1,  0},  // up
            { 1,  0},  // down
            { 0, -1},  // left
            { 0,  1}   // right
    };

    public SolitaireModel() {
        initBoard();
    }

    // Initialisation
    /** Reset the board to the standard English starting position. */
    public void initBoard() {
        board = new int[SIZE][SIZE];
        pegsRemaining = 0;

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (isValidCell(r, c)) {
                    board[r][c] = PEG;
                    pegsRemaining++;
                } else {
                    board[r][c] = INVALID;
                }
            }
        }

        // The centre cell starts empty
        board[3][3] = EMPTY;
        pegsRemaining--;
    }

    /**
     * Returns true if (row, col) is inside the playable English cross shape.
     */
    public boolean isValidCell(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return false;
        // Corner cutouts: rows 0-1 and 5-6 only have cols 2-4
        if ((row < 2 || row > 4) && (col < 2 || col > 4)) return false;
        return true;
    }

    // Move logic
    /**
     * Attempts to perform a move where a peg jumps over an adjacent peg
     * and lands in an empty cell two spaces away.
     *
     * Preconditions:
     * - (fromRow, fromCol) contains a PEG
     * - The move is horizontal or vertical
     * - The destination cell is EMPTY
     * - There is a PEG between the source and destination
     *
     * Postconditions (if the move is legal):
     * - The source cell becomes EMPTY
     * - The jumped peg is removed
     * - The destination cell becomes a PEG
     *
     * @return true if the move was valid and applied, false otherwise.
     */
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

    /**
     * Returns true if moving from (fromRow,fromCol) to (toRow,toCol) is legal.
     */
    public boolean isLegalMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Both cells must be on the board
        if (!isValidCell(fromRow, fromCol) || !isValidCell(toRow, toCol)) return false;

        // Source must have a peg, destination must be empty
        if (board[fromRow][fromCol] != PEG)   return false;
        if (board[toRow][toCol]     != EMPTY) return false;

        int dr = toRow - fromRow;
        int dc = toCol - fromCol;

        // Must be exactly 2 steps in one orthogonal direction
        if (!((Math.abs(dr) == 2 && dc == 0) || (dr == 0 && Math.abs(dc) == 2))) return false;

        // The jumped cell must contain a peg
        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        return board[midRow][midCol] == PEG;
    }

    // Game-over detection

    /**
     * Returns true when no legal move exists anywhere on the board.
     */
    public boolean isGameOver() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == PEG) {
                    for (int[] d : DIRECTIONS) {
                        int mr = r + d[0];
                        int mc = c + d[1];
                        int tr = r + 2 * d[0];
                        int tc = c + 2 * d[1];
                        if (isLegalMove(r, c, tr, tc)) return false;
                    }
                }
            }
        }
        return true;
    }

    /** Returns true if the player has won (exactly 1 peg remaining). */
    public boolean isWon() {
        return pegsRemaining == 1;
    }

    // Getters

    public int getCellState(int row, int col) {
        return board[row][col];
    }

    public int getPegsRemaining() {
        return pegsRemaining;
    }

    public int getBoardSize() {
        return SIZE;
    }

    /**
     * Test-only: overwrite the board and recount pegs.
     * Package-private so only test helpers in the same package can call it.
     */
    void setBoardState(int[][] newBoard) {
        pegsRemaining = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                board[r][c] = newBoard[r][c];
                if (board[r][c] == PEG) pegsRemaining++;
            }
        }
    }

    /** Returns a deep copy of the board (useful for tests). */
    public int[][] getBoardCopy() {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(board[r], 0, copy[r], 0, SIZE);
        }
        return copy;
    }
}