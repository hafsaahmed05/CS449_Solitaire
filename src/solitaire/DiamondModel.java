package solitaire;

/**
 * DiamondModel — symmetric diamond with row widths:
 * 1,3,5,...,size,...,5,3,1
 *
 * Example size = 7:
 *   1
 *  3
 * 5
 *7
 * 5
 *  3
 *   1
 */
public class DiamondModel extends SolitaireModel {

    private final int size;   // must be odd (e.g. 7)
    private final int half;

    public DiamondModel(int size) {
        this.size = size;
        this.half = size / 2;
        initBoard();
    }

    @Override public int getGridSize()     { return size; }
    @Override protected int getCentreRow() { return half; }
    @Override protected int getCentreCol() { return half; }

    @Override
    public boolean isValidCell(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) return false;

        int dist = Math.abs(row - half);

        // 🔥 KEY FIX: grows by 2 each row
        int rowWidth = size - 2 * dist;

        int start = half - rowWidth / 2;
        int end   = half + rowWidth / 2;

        return col >= start && col <= end;
    }
}