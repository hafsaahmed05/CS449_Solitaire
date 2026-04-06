package solitaire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * SolitaireModelTest — JUnit 5 tests for all Sprint 3 user stories.
 *
 * US1  – Choose board size
 * US2  – Choose board type (English / Hexagon / Diamond)
 * US3  – Start a new game
 * US4  – Make a move in a manual game
 * US5  – A manual game is over
 * US6  – Make a move in an automated game  (makeAnyMove)
 * US7  – An automated game is over
 * US8  – Randomize the board
 */
class SolitaireModelTest {

    private SolitaireModel model;

    @BeforeEach
    void setUp() {
        model = new EnglishModel(7);
    }

    // =======================================================================
    // US1 – Choose board size
    // =======================================================================

    @Test
    @DisplayName("US1-AC1: English size 7 grid is 7")
    void testBoardSizeIsSeven() {
        assertEquals(7, model.getBoardSize());
    }

    @Test
    @DisplayName("US1-AC2: Board layout is a 7x7 array")
    void testBoardDimensions() {
        int[][] board = model.getBoardCopy();
        assertEquals(7, board.length);
        assertEquals(7, board[0].length);
    }

    @Test
    @DisplayName("US1-AC3: English size 9 grid is 9")
    void testBoardSizeNine() {
        SolitaireModel m = new EnglishModel(9);
        assertEquals(9, m.getBoardSize());
    }

    @Test
    @DisplayName("US1-AC4: Diamond size 7 grid is 7")
    void testDiamondBoardSize() {
        SolitaireModel m = new DiamondModel(7);
        assertEquals(7, m.getBoardSize());
    }

    @Test
    @DisplayName("US1-AC5: Hexagon size 7 grid is 7")
    void testHexagonBoardSize() {
        SolitaireModel m = new HexagonModel(7);
        assertEquals(7, m.getBoardSize());
    }

    // =======================================================================
    // US2 – Choose board type
    // =======================================================================

    // --- English ---

    @Test
    @DisplayName("US2-AC1: English board starts with 32 pegs")
    void testEnglishInitialPegCount() {
        assertEquals(32, model.getPegsRemaining());
    }

    @Test
    @DisplayName("US2-AC2: English centre cell (3,3) starts EMPTY")
    void testEnglishCentreCellIsEmpty() {
        assertEquals(SolitaireModel.EMPTY, model.getCellState(3, 3));
    }

    @Test
    @DisplayName("US2-AC3: English corner (0,0) is INVALID")
    void testEnglishCornerIsInvalid() {
        assertEquals(SolitaireModel.INVALID, model.getCellState(0, 0));
    }

    @Test
    @DisplayName("US2-AC4: English cross shape — arm rows only allow cols 2-4")
    void testEnglishValidCellLayout() {
        for (int r : new int[]{0, 1, 5, 6}) {
            for (int c = 0; c < 7; c++) {
                if (c >= 2 && c <= 4) assertTrue(model.isValidCell(r, c),
                        "Expected valid at (" + r + "," + c + ")");
                else assertFalse(model.isValidCell(r, c),
                        "Expected invalid at (" + r + "," + c + ")");
            }
        }
        for (int r = 2; r <= 4; r++) {
            for (int c = 0; c < 7; c++) {
                assertTrue(model.isValidCell(r, c));
            }
        }
    }

    // --- Diamond ---

    @Test
    @DisplayName("US2-AC5: Diamond centre cell starts EMPTY")
    void testDiamondCentreCellIsEmpty() {
        SolitaireModel m = new DiamondModel(7);
        int centre = m.getBoardSize() / 2;
        assertEquals(SolitaireModel.EMPTY, m.getCellState(centre, centre));
    }

    @Test
    @DisplayName("US2-AC6: Diamond corner (0,0) is INVALID")
    void testDiamondCornerIsInvalid() {
        SolitaireModel m = new DiamondModel(7);
        assertEquals(SolitaireModel.INVALID, m.getCellState(0, 0));
    }

    @Test
    @DisplayName("US2-AC7: Diamond widest row (middle) is fully valid")
    void testDiamondMiddleRowFullyValid() {
        SolitaireModel m = new DiamondModel(7);
        int mid = m.getBoardSize() / 2;
        for (int c = 0; c < m.getBoardSize(); c++) {
            assertTrue(m.isValidCell(mid, c),
                    "Expected valid at middle row col " + c);
        }
    }

    @Test
    @DisplayName("US2-AC8: Diamond top cell (0, mid) is valid, neighbours invalid")
    void testDiamondTopCell() {
        SolitaireModel m = new DiamondModel(7);
        int mid = m.getBoardSize() / 2;
        assertTrue(m.isValidCell(0, mid));
        assertFalse(m.isValidCell(0, mid - 1));
        assertFalse(m.isValidCell(0, mid + 1));
    }

    // --- Hexagon ---

    @Test
    @DisplayName("US2-AC9: Hexagon centre cell starts EMPTY")
    void testHexagonCentreCellIsEmpty() {
        SolitaireModel m = new HexagonModel(7);
        int centre = m.getBoardSize() / 2;
        assertEquals(SolitaireModel.EMPTY, m.getCellState(centre, centre));
    }

    @Test
    @DisplayName("US2-AC10: Hexagon corner (0,0) is INVALID")
    void testHexagonCornerIsInvalid() {
        SolitaireModel m = new HexagonModel(7);
        assertEquals(SolitaireModel.INVALID, m.getCellState(0, 0));
    }

    @Test
    @DisplayName("US2-AC11: Hexagon middle row is fully valid")
    void testHexagonMiddleRowFullyValid() {
        SolitaireModel m = new HexagonModel(7);
        int mid = m.getBoardSize() / 2;
        for (int c = 0; c < m.getBoardSize(); c++) {
            assertTrue(m.isValidCell(mid, c),
                    "Expected valid at middle row col " + c);
        }
    }

    @Test
    @DisplayName("US2-AC12: Hexagon top row has fewer valid cells than middle row")
    void testHexagonTopRowNarrowerThanMiddle() {
        SolitaireModel m = new HexagonModel(7);
        int mid = m.getBoardSize() / 2;

        int topValid = 0;
        int midValid = 0;

        for (int c = 0; c < m.getBoardSize(); c++) {
            if (m.isValidCell(0, c))   topValid++;
            if (m.isValidCell(mid, c)) midValid++;
        }
        assertTrue(topValid < midValid);
    }

    // =======================================================================
    // US3 – Start a new game
    // =======================================================================

    @Test
    @DisplayName("US3-AC1: initBoard resets peg count to 32")
    void testNewGameResetsPegCount() {
        model.makeMove(3, 1, 3, 3);
        model.initBoard();
        assertEquals(32, model.getPegsRemaining());
    }

    @Test
    @DisplayName("US3-AC2: initBoard restores centre cell to EMPTY")
    void testNewGameResetsCentreCell() {
        model.makeMove(3, 1, 3, 3);
        model.initBoard();
        assertEquals(SolitaireModel.EMPTY, model.getCellState(3, 3));
    }

    @Test
    @DisplayName("US3-AC3: initBoard restores moved peg")
    void testNewGameRestoresPeg() {
        model.makeMove(3, 1, 3, 3);
        model.initBoard();
        assertEquals(SolitaireModel.PEG, model.getCellState(3, 1));
    }

    @Test
    @DisplayName("US3-AC4: Hexagon initBoard resets to fresh state")
    void testHexagonInitBoard() {
        SolitaireModel m = new HexagonModel(7);
        int pegsAfterInit = m.getPegsRemaining();
        m.makeMove(3, 1, 3, 3);
        m.initBoard();
        assertEquals(pegsAfterInit, m.getPegsRemaining());
    }

    @Test
    @DisplayName("US3-AC5: Diamond initBoard resets to fresh state")
    void testDiamondInitBoard() {
        SolitaireModel m = new DiamondModel(7);
        int pegsAfterInit = m.getPegsRemaining();
        m.makeMove(3, 1, 3, 3);
        m.initBoard();
        assertEquals(pegsAfterInit, m.getPegsRemaining());
    }

    // =======================================================================
    // US4 – Make a move in a manual game
    // =======================================================================

    @Test
    @DisplayName("US4-AC1: Legal move returns true")
    void testLegalMoveReturnsTrue() {
        assertTrue(model.makeMove(3, 1, 3, 3));
    }

    @Test
    @DisplayName("US4-AC2: Source cell becomes EMPTY after move")
    void testAfterMoveSourceIsEmpty() {
        model.makeMove(3, 1, 3, 3);
        assertEquals(SolitaireModel.EMPTY, model.getCellState(3, 1));
    }

    @Test
    @DisplayName("US4-AC3: Jumped peg is removed")
    void testAfterMoveJumpedCellIsEmpty() {
        model.makeMove(3, 1, 3, 3);
        assertEquals(SolitaireModel.EMPTY, model.getCellState(3, 2));
    }

    @Test
    @DisplayName("US4-AC4: Destination cell receives PEG")
    void testAfterMoveDestinationHasPeg() {
        model.makeMove(3, 1, 3, 3);
        assertEquals(SolitaireModel.PEG, model.getCellState(3, 3));
    }

    @Test
    @DisplayName("US4-AC5: Legal move decrements peg count by 1")
    void testLegalMoveDecrementsPegCount() {
        int before = model.getPegsRemaining();
        model.makeMove(3, 1, 3, 3);
        assertEquals(before - 1, model.getPegsRemaining());
    }

    @Test
    @DisplayName("US4-AC6: Illegal move (empty source) returns false")
    void testIllegalMoveReturnsFalse() {
        assertFalse(model.makeMove(3, 3, 3, 5));
    }

    @Test
    @DisplayName("US4-AC7: Move to occupied cell is illegal")
    void testMoveToOccupiedCellReturnsFalse() {
        model.makeMove(3, 1, 3, 3);
        assertFalse(model.makeMove(1, 3, 3, 3));
    }

    @Test
    @DisplayName("US4-AC8: Illegal move does not change peg count")
    void testIllegalMoveDoesNotChangePegCount() {
        int before = model.getPegsRemaining();
        model.makeMove(0, 0, 0, 2);
        assertEquals(before, model.getPegsRemaining());
    }

    @Test
    @DisplayName("US4-AC9: Diagonal move is illegal")
    void testDiagonalMoveIsIllegal() {
        assertFalse(model.makeMove(2, 2, 4, 4));
    }

    @Test
    @DisplayName("US4-AC10: isLegalMove reflects board state before and after move")
    void testIsLegalMoveBeforeAndAfter() {
        assertTrue(model.isLegalMove(3, 1, 3, 3));
        model.makeMove(3, 1, 3, 3);
        assertFalse(model.isLegalMove(3, 1, 3, 3));
    }

    @Test
    @DisplayName("US4-AC11: Legal move on Hexagon board works correctly")
    void testHexagonLegalMove() {
        SolitaireModel m = new HexagonModel(7);
        // centre (3,3) is empty; (3,1) has a peg; (3,2) has a peg
        assertTrue(m.makeMove(3, 1, 3, 3));
        assertEquals(SolitaireModel.EMPTY, m.getCellState(3, 1));
        assertEquals(SolitaireModel.EMPTY, m.getCellState(3, 2));
        assertEquals(SolitaireModel.PEG,   m.getCellState(3, 3));
    }

    @Test
    @DisplayName("US4-AC12: Legal move on Diamond board works correctly")
    void testDiamondLegalMove() {
        SolitaireModel m = new DiamondModel(7);
        // centre (3,3) is empty; (3,1) has a peg; (3,2) has a peg
        assertTrue(m.makeMove(3, 1, 3, 3));
        assertEquals(SolitaireModel.EMPTY, m.getCellState(3, 1));
        assertEquals(SolitaireModel.EMPTY, m.getCellState(3, 2));
        assertEquals(SolitaireModel.PEG,   m.getCellState(3, 3));
    }

    // =======================================================================
    // US5 – A manual game is over
    // =======================================================================

    @Test
    @DisplayName("US5-AC1: New board is not game over")
    void testNewBoardNotGameOver() {
        assertFalse(model.isGameOver());
    }

    @Test
    @DisplayName("US5-AC2: New board is not won")
    void testNewBoardNotWon() {
        assertFalse(model.isWon());
    }

    @Test
    @DisplayName("US5-AC3: One peg remaining is a win")
    void testOnePegIsWon() {
        SolitaireModel winModel = new SolitaireModelTestHelper().buildWonBoard();
        assertTrue(winModel.isWon());
        assertTrue(winModel.isGameOver());
    }

    @Test
    @DisplayName("US5-AC4: No legal moves results in game over")
    void testNoMovesIsGameOver() {
        SolitaireModel stuckModel = new SolitaireModelTestHelper().buildStuckBoard();
        assertTrue(stuckModel.isGameOver());
        assertFalse(stuckModel.isWon());
    }

    @Test
    @DisplayName("US5-AC5: Hexagon game over when no moves remain")
    void testHexagonGameOver() {
        SolitaireModel m = new SolitaireModelTestHelper().buildHexagonStuckBoard();
        assertTrue(m.isGameOver());
        assertFalse(m.isWon());
    }

    @Test
    @DisplayName("US5-AC6: Diamond game over when no moves remain")
    void testDiamondGameOver() {
        SolitaireModel m = new SolitaireModelTestHelper().buildDiamondStuckBoard();
        assertTrue(m.isGameOver());
        assertFalse(m.isWon());
    }

    // =======================================================================
    // US6 – Make a move in an automated game (makeAnyMove)
    // =======================================================================

    @Test
    @DisplayName("US6-AC1: makeAnyMove returns true when a move is available")
    void testMakeAnyMoveReturnsTrueWhenMoveAvailable() {
        assertTrue(model.makeAnyMove());
    }

    @Test
    @DisplayName("US6-AC2: makeAnyMove decrements peg count by 1")
    void testMakeAnyMoveDecrementsPegs() {
        int before = model.getPegsRemaining();
        model.makeAnyMove();
        assertEquals(before - 1, model.getPegsRemaining());
    }

    @Test
    @DisplayName("US6-AC3: makeAnyMove returns false when no moves remain")
    void testMakeAnyMoveReturnsFalseWhenStuck() {
        SolitaireModel stuck = new SolitaireModelTestHelper().buildStuckBoard();
        assertFalse(stuck.makeAnyMove());
    }

    @Test
    @DisplayName("US6-AC4: makeAnyMove leaves board in a consistent state")
    void testMakeAnyMoveBoardConsistency() {
        model.makeAnyMove();
        // Count pegs manually and compare to getPegsRemaining()
        int counted = 0;
        for (int r = 0; r < model.getBoardSize(); r++) {
            for (int c = 0; c < model.getBoardSize(); c++) {
                if (model.getCellState(r, c) == SolitaireModel.PEG) counted++;
            }
        }
        assertEquals(counted, model.getPegsRemaining());
    }

    @Test
    @DisplayName("US6-AC5: makeAnyMove works on Hexagon board")
    void testMakeAnyMoveHexagon() {
        SolitaireModel m = new HexagonModel(7);
        int before = m.getPegsRemaining();
        assertTrue(m.makeAnyMove());
        assertEquals(before - 1, m.getPegsRemaining());
    }

    @Test
    @DisplayName("US6-AC6: makeAnyMove works on Diamond board")
    void testMakeAnyMoveDiamond() {
        SolitaireModel m = new DiamondModel(7);
        int before = m.getPegsRemaining();
        assertTrue(m.makeAnyMove());
        assertEquals(before - 1, m.getPegsRemaining());
    }

    // =======================================================================
    // US7 – An automated game is over
    // =======================================================================

    @Test
    @DisplayName("US7-AC1: Repeated makeAnyMove eventually ends the game")
    void testAutoplayEventuallyEndsGame() {
        while (!model.isGameOver()) {
            model.makeAnyMove();
        }
        assertTrue(model.isGameOver());
    }

    @Test
    @DisplayName("US7-AC2: Autoplay peg count is consistent at game over")
    void testAutoplayPegCountAtGameOver() {
        while (!model.isGameOver()) {
            model.makeAnyMove();
        }
        int counted = 0;
        for (int r = 0; r < model.getBoardSize(); r++) {
            for (int c = 0; c < model.getBoardSize(); c++) {
                if (model.getCellState(r, c) == SolitaireModel.PEG) counted++;
            }
        }
        assertEquals(counted, model.getPegsRemaining());
    }

    @Test
    @DisplayName("US7-AC3: Autoplay on Hexagon eventually ends the game")
    void testAutoplayHexagonEndsGame() {
        SolitaireModel m = new HexagonModel(7);
        int maxMoves = m.getPegsRemaining();
        int moves = 0;
        while (!m.isGameOver() && moves < maxMoves) {
            m.makeAnyMove();
            moves++;
        }
        assertTrue(m.isGameOver());
    }

    @Test
    @DisplayName("US7-AC4: Autoplay on Diamond eventually ends the game")
    void testAutoplayDiamondEndsGame() {
        SolitaireModel m = new DiamondModel(7);
        int maxMoves = m.getPegsRemaining();
        int moves = 0;
        while (!m.isGameOver() && moves < maxMoves) {
            m.makeAnyMove();
            moves++;
        }
        assertTrue(m.isGameOver());
    }

    // =======================================================================
    // US8 – Randomize the board
    // =======================================================================

    @Test
    @DisplayName("US8-AC1: randomizeBoard only places PEG or EMPTY on valid cells")
    void testRandomizeOnlyValidStates() {
        model.randomizeBoard();
        for (int r = 0; r < model.getBoardSize(); r++) {
            for (int c = 0; c < model.getBoardSize(); c++) {
                int state = model.getCellState(r, c);
                if (model.isValidCell(r, c)) {
                    assertTrue(state == SolitaireModel.PEG || state == SolitaireModel.EMPTY,
                            "Valid cell (" + r + "," + c + ") has unexpected state: " + state);
                } else {
                    assertEquals(SolitaireModel.INVALID, state,
                            "Invalid cell (" + r + "," + c + ") should remain INVALID");
                }
            }
        }
    }

    @Test
    @DisplayName("US8-AC2: randomizeBoard leaves centre cell EMPTY")
    void testRandomizeCentreIsEmpty() {
        model.randomizeBoard();
        assertEquals(SolitaireModel.EMPTY, model.getCellState(3, 3));
    }

    @Test
    @DisplayName("US8-AC3: randomizeBoard peg count matches actual board")
    void testRandomizePegCountIsConsistent() {
        model.randomizeBoard();
        int counted = 0;
        for (int r = 0; r < model.getBoardSize(); r++) {
            for (int c = 0; c < model.getBoardSize(); c++) {
                if (model.getCellState(r, c) == SolitaireModel.PEG) counted++;
            }
        }
        assertEquals(counted, model.getPegsRemaining());
    }

    @Test
    @DisplayName("US8-AC4: randomizeBoard changes the board state")
    void testRandomizeChangesBoardState() {
        int[][] before = model.getBoardCopy();
        // Run several times — extremely unlikely all produce identical boards
        boolean changed = false;
        for (int attempt = 0; attempt < 10; attempt++) {
            model.randomizeBoard();
            int[][] after = model.getBoardCopy();
            for (int r = 0; r < model.getBoardSize(); r++) {
                for (int c = 0; c < model.getBoardSize(); c++) {
                    if (before[r][c] != after[r][c]) { changed = true; break; }
                }
                if (changed) break;
            }
            if (changed) break;
        }
        assertTrue(changed, "randomizeBoard should produce a different board state");
    }

    @Test
    @DisplayName("US8-AC5: randomizeBoard works on Hexagon board")
    void testRandomizeHexagon() {
        SolitaireModel m = new HexagonModel(7);
        m.randomizeBoard();
        int counted = 0;
        for (int r = 0; r < m.getBoardSize(); r++) {
            for (int c = 0; c < m.getBoardSize(); c++) {
                if (m.getCellState(r, c) == SolitaireModel.PEG) counted++;
            }
        }
        assertEquals(counted, m.getPegsRemaining());
    }

    @Test
    @DisplayName("US8-AC6: randomizeBoard works on Diamond board")
    void testRandomizeDiamond() {
        SolitaireModel m = new DiamondModel(7);
        m.randomizeBoard();
        int counted = 0;
        for (int r = 0; r < m.getBoardSize(); r++) {
            for (int c = 0; c < m.getBoardSize(); c++) {
                if (m.getCellState(r, c) == SolitaireModel.PEG) counted++;
            }
        }
        assertEquals(counted, m.getPegsRemaining());
    }
}