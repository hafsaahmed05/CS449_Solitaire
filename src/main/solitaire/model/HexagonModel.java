package main.solitaire.model;

import main.solitaire.model.*;
/**
 * Hexagon-shaped Peg Solitaire board.
 * Rows expand to the center, then contract symmetrically.
 * Only cells within the hex shape are valid.
 */
public class HexagonModel extends SolitaireModel {

    private final int size;
    private final int base;

    public HexagonModel(int size) {
        this.size = size;
        this.base = (size + 1) / 2;
        initBoard();
    }

    @Override public int getGridSize()     { return size; }
    @Override protected int getCentreRow() { return size / 2; }
    @Override protected int getCentreCol() { return size / 2; }

    /** Allowed movement directions (6 for hex grid). */
    @Override
    protected int[][] getDirections() {
        return new int[][] {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1},
                {-1, -1}, {1, 1},
                {-1,  1}, {1, -1}
        };
    }

    @Override
    public boolean needsRowOffset() { return true; }

    /** Returns width of a given row. */
    private int rowWidth(int r) {
        return base + Math.min(r, size - 1 - r);
    }

    /** Checks if (row, col) is inside the hex shape. */
    @Override
    public boolean isValidCell(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) return false;

        int w = rowWidth(row);
        int minCol = (size - w) / 2;
        int maxCol = minCol + w - 1;

        return col >= minCol && col <= maxCol;
    }
}