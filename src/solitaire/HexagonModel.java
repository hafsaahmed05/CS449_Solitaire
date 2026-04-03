package solitaire;

/**
 * HexagonModel — a hexagon shaped board.
 *
 * For size N (the widest row), the board has (2*N - 1) rows arranged like:
 *   rows 0     .. N-2  : width increases from (N - (N-1-r)) = r+1+? …
 *
 * More precisely, for size N the row widths are:
 *   row r  (0-based):
 *     if r < N:   width = N - 1 + (r % N == 0 ? 1 : r ... )
 *
 * Simpler formula:  half = N - 1
 *   rowWidth(r) = N + min(r, 2*half - r)   where  0 ≤ r ≤ 2*half
 *
 * Example N=4 (size 4): rows 0..6, widths = 4,5,6,7,6,5,4
 * Example N=5 (size 5): rows 0..8, widths = 5,6,7,8,9,8,7,6,5  ← too big
 *
 * Wait — looking at the screenshots for the assignment, "size 7" gives a
 * hexagon whose widest row is 7, so rowWidths = 4,5,6,7,6,5,4 (7 rows).
 * That matches N = 7 with half = 3:
 *   rowWidth(r) = (N+1)/2 + min(r, N-1-r)
 *              = 4 + min(r, 6-r)   for N=7
 *   r=0: 4+0=4, r=1: 4+1=5, r=2: 4+2=6, r=3: 4+3=7, r=4: 4+2=6 ...  ✓
 *
 * So for any odd size N:  base = (N+1)/2,  rowWidth(r) = base + min(r, N-1-r)
 * Grid size = N rows × N cols (widest row = N).
 *
 * Cells are centred: for row r, valid cols span
 *   from  (N - rowWidth(r)) / 2
 *   to    (N + rowWidth(r)) / 2 - 1
 *
 * Centre cell = (N/2, N/2) starts empty.
 */
public class HexagonModel extends SolitaireModel {

    private final int size;   // number of rows = widest row width
    private final int base;   // (size + 1) / 2  — narrowest row width

    public HexagonModel(int size) {
        this.size = size;
        this.base = (size + 1) / 2;
        initBoard();
    }

    @Override public int getGridSize()     { return size; }
    @Override protected int getCentreRow() { return size / 2; }
    @Override protected int getCentreCol() { return size / 2; }

    @Override
    protected int[][] getDirections() {
        return new int[][] {
                {-1, 0}, {1, 0},     // vertical
                {0, -1}, {0, 1},     // horizontal
                {-1, -1}, {1, 1}     // hex diagonal axis
        };
    }

    /** Width of row r for this hexagon. */
    private int rowWidth(int r) {
        return base + Math.min(r, size - 1 - r);
    }

    @Override
    public boolean isValidCell(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) return false;
        int w      = rowWidth(row);
        int minCol = (size - w) / 2;
        int maxCol = minCol + w - 1;
        return col >= minCol && col <= maxCol;
    }
}