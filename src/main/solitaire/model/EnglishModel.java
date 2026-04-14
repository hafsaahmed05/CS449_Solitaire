package main.solitaire.model;

import main.solitaire.model.*;

/**
 * EnglishModel — the classic cross-shaped English Peg Solitaire board.
 *
 * For size 7 the grid is 7×7. The cross shape cuts off the four corners:
 *   rows 0-1 and 5-6 only allow cols 2-4.
 *   rows 2-4 allow all cols 0-6.
 *
 * The "arm width" is always size/3 (integer division), which gives 2 for
 * size 7, 3 for size 9, etc.  Only odd sizes ≥ 7 make sense.
 */
public class EnglishModel extends SolitaireModel {

    private final int size;
    private final int arm;   // width of the corner cutout = size / 3

    public EnglishModel(int size) {
        this.size = size;
        this.arm  = size / 3;
        initBoard();
    }

    @Override public int getGridSize()   { return size; }
    @Override protected int getCentreRow() { return size / 2; }
    @Override protected int getCentreCol() { return size / 2; }

    @Override
    public boolean isValidCell(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) return false;
        // Corner cutouts: outside the arm bands
        if ((row < arm || row >= size - arm) && (col < arm || col >= size - arm)) return false;
        return true;
    }
}