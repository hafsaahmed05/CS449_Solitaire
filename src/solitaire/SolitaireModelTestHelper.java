package solitaire;

/**
 * SolitaireModelTestHelper — builds special board states for unit tests.
 *
 * Kept in the same package as SolitaireModel so it can access the
 * package-private setBoardState() method.
 */
class SolitaireModelTestHelper {

    private static final int I = SolitaireModel.INVALID;
    private static final int E = SolitaireModel.EMPTY;
    private static final int P = SolitaireModel.PEG;

    /**
     * Returns a model with exactly one peg at the centre (3,3).
     * This represents a "won" state.
     */
    SolitaireModel buildWonBoard() {
        // All valid cells empty, centre has one peg
        int[][] b = {
                {I, I, E, E, E, I, I},
                {I, I, E, E, E, I, I},
                {E, E, E, E, E, E, E},
                {E, E, E, P, E, E, E},
                {E, E, E, E, E, E, E},
                {I, I, E, E, E, I, I},
                {I, I, E, E, E, I, I}
        };
        SolitaireModel m = new SolitaireModel();
        m.setBoardState(b);
        return m;
    }

    /**
     * Returns a model that has multiple pegs but NO legal moves.
     *
     * Achieved by placing two isolated pegs that cannot reach each other
     * (e.g. (2,2) and (4,4) with no peg between them on a straight line).
     */
    SolitaireModel buildStuckBoard() {
        int[][] b = {
                {I, I, E, E, E, I, I},
                {I, I, E, E, E, I, I},
                {E, E, P, E, E, E, E},
                {E, E, E, E, E, E, E},
                {E, E, E, E, P, E, E},
                {I, I, E, E, E, I, I},
                {I, I, E, E, E, I, I}
        };
        SolitaireModel m = new SolitaireModel();
        m.setBoardState(b);
        return m;
    }
}