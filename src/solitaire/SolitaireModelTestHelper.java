package solitaire;

/**
 * SolitaireModelTestHelper — builds special board states for unit tests.
 *
 * Package-private so it can access setBoardState() on SolitaireModel.
 * Extended for Sprint 3 to support Hexagon and Diamond board states.
 */
class SolitaireModelTestHelper {

    private static final int I = SolitaireModel.INVALID;
    private static final int E = SolitaireModel.EMPTY;
    private static final int P = SolitaireModel.PEG;

    // ------------------------------------------------------------------
    // English boards
    // ------------------------------------------------------------------

    /**
     * English board with exactly one peg at centre (3,3) — a won state.
     */
    SolitaireModel buildWonBoard() {
        int[][] b = {
                {I, I, E, E, E, I, I},
                {I, I, E, E, E, I, I},
                {E, E, E, E, E, E, E},
                {E, E, E, P, E, E, E},
                {E, E, E, E, E, E, E},
                {I, I, E, E, E, I, I},
                {I, I, E, E, E, I, I}
        };
        SolitaireModel m = new EnglishModel(7);
        m.setBoardState(b);
        return m;
    }

    /**
     * English board with two isolated pegs — no legal moves, not won.
     * Pegs at (2,2) and (4,4) cannot reach each other orthogonally.
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
        SolitaireModel m = new EnglishModel(7);
        m.setBoardState(b);
        return m;
    }

    // ------------------------------------------------------------------
    // Hexagon boards
    // ------------------------------------------------------------------

    /**
     * Hexagon board with two isolated pegs — no legal moves, not won.
     * Pegs at (1,3) and (5,3) are separated by empty cells with no
     * adjacent peg to jump over.
     */
    SolitaireModel buildHexagonStuckBoard() {
        SolitaireModel m = new HexagonModel(7);
        int g = m.getBoardSize();

        // Start from a blank (all INVALID), then place only two isolated pegs
        int[][] b = new int[g][g];
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                b[r][c] = m.isValidCell(r, c) ? E : I;
            }
        }

        // Two pegs with no adjacent peg between them on any axis
        b[1][3] = P;
        b[5][3] = P;

        m.setBoardState(b);
        return m;
    }

    /**
     * Hexagon board with exactly one peg at centre — a won state.
     */
    SolitaireModel buildHexagonWonBoard() {
        SolitaireModel m = new HexagonModel(7);
        int g   = m.getBoardSize();
        int mid = g / 2;

        int[][] b = new int[g][g];
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                b[r][c] = m.isValidCell(r, c) ? E : I;
            }
        }
        b[mid][mid] = P;

        m.setBoardState(b);
        return m;
    }

    // ------------------------------------------------------------------
    // Diamond boards
    // ------------------------------------------------------------------

    /**
     * Diamond board with two isolated pegs — no legal moves, not won.
     * Pegs at (1,3) and (5,3) with nothing between them.
     */
    SolitaireModel buildDiamondStuckBoard() {
        SolitaireModel m = new DiamondModel(7);
        int g = m.getBoardSize();

        int[][] b = new int[g][g];
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                b[r][c] = m.isValidCell(r, c) ? E : I;
            }
        }

        b[1][3] = P;
        b[5][3] = P;

        m.setBoardState(b);
        return m;
    }

    /**
     * Diamond board with exactly one peg at centre — a won state.
     */
    SolitaireModel buildDiamondWonBoard() {
        SolitaireModel m = new DiamondModel(7);
        int g   = m.getBoardSize();
        int mid = g / 2;

        int[][] b = new int[g][g];
        for (int r = 0; r < g; r++) {
            for (int c = 0; c < g; c++) {
                b[r][c] = m.isValidCell(r, c) ? E : I;
            }
        }
        b[mid][mid] = P;

        m.setBoardState(b);
        return m;
    }
}