package solitaire;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SolitaireModelTest — JUnit 5 tests for SolitaireModel.
 * Tests are grouped by the User Story / Acceptance Criterion they cover.
 */

class SolitaireModelTest {

    private SolitaireModel model;

    @BeforeEach
    void setUp() {
        model = new SolitaireModel();
    }

    // =======================================================================
    // User Story 1 – Choose board size
    // =======================================================================

    @Test
    @DisplayName("User Story 1-AC1: Default board size is 7")
    void testBoardSizeIsSeven() {
        assertEquals(7, model.getBoardSize());
    }

    @Test
    @DisplayName("User Story 1-AC2: Board layout corresponds to a 7x7 board")
    void testBoardDimensions() {
        int[][] board = model.getBoardCopy();
        assertEquals(7, board.length);
        assertEquals(7, board[0].length);
    }

    // =======================================================================
    // User Story 2 – Choose board type
    // =======================================================================

    @Test
    @DisplayName("User Story 2-AC1: English board starts with 32 pegs")
    void testInitialPegCount() {
        assertEquals(32, model.getPegsRemaining());
    }

    @Test
    @DisplayName("User Story 2-AC2: Centre cell starts EMPTY")
    void testCentreCellIsEmpty() {
        assertEquals(SolitaireModel.EMPTY, model.getCellState(3, 3));
    }

    @Test
    @DisplayName("User Story 2-AC3: Corner cell (0,0) is INVALID")
    void testCornerCellIsInvalid() {
        assertEquals(SolitaireModel.INVALID, model.getCellState(0, 0));
    }

    @Test
    @DisplayName("User Story 2-AC4: All valid non-centre cells start with a PEG")
    void testAllValidNonCentreCellsHavePegs() {

        for (int r = 0; r < model.getBoardSize(); r++) {
            for (int c = 0; c < model.getBoardSize(); c++) {

                if (model.isValidCell(r, c) && !(r == 3 && c == 3)) {
                    assertEquals(
                            SolitaireModel.PEG,
                            model.getCellState(r, c)
                    );
                }
            }
        }
    }

    @Test
    @DisplayName("User Story 2-AC5: Valid cell layout matches English cross")
    void testValidCellLayout() {

        for (int r : new int[]{0,1,5,6}) {
            for (int c = 0; c < 7; c++) {

                if (c >= 2 && c <= 4) {
                    assertTrue(model.isValidCell(r, c));
                } else {
                    assertFalse(model.isValidCell(r, c));
                }
            }
        }

        for (int r = 2; r <= 4; r++) {
            for (int c = 0; c < 7; c++) {
                assertTrue(model.isValidCell(r, c));
            }
        }
    }

    // =======================================================================
    // User Story 3 – Start a new game
    // =======================================================================

    @Test
    @DisplayName("User Story 3-AC1: initBoard resets peg count to 32")
    void testNewGameResetsPegCount() {

        model.makeMove(3,1,3,3);
        model.initBoard();

        assertEquals(32, model.getPegsRemaining());
    }

    @Test
    @DisplayName("User Story 3-AC2: initBoard restores centre cell to EMPTY")
    void testNewGameResetsCentreCell() {

        model.makeMove(3,1,3,3);
        model.initBoard();

        assertEquals(SolitaireModel.EMPTY, model.getCellState(3,3));
    }

    @Test
    @DisplayName("User Story 3-AC3: initBoard restores moved peg")
    void testNewGameRestoresPeg() {

        model.makeMove(3,1,3,3);
        model.initBoard();

        assertEquals(SolitaireModel.PEG, model.getCellState(3,1));
    }

    // =======================================================================
    // User Story 4 – Make a move
    // =======================================================================

    @Test
    @DisplayName("User Story 4-AC1: Legal move returns true")
    void testLegalMoveReturnsTrue() {

        assertTrue(model.makeMove(3,1,3,3));
    }

    @Test
    @DisplayName("User Story 4-AC2: Source cell becomes EMPTY after move")
    void testAfterMoveSourceIsEmpty() {

        model.makeMove(3,1,3,3);

        assertEquals(SolitaireModel.EMPTY, model.getCellState(3,1));
    }

    @Test
    @DisplayName("User Story 4-AC3: Jumped peg is removed")
    void testAfterMoveJumpedCellIsEmpty() {

        model.makeMove(3,1,3,3);

        assertEquals(SolitaireModel.EMPTY, model.getCellState(3,2));
    }

    @Test
    @DisplayName("User Story 4-AC4: Destination cell receives PEG")
    void testAfterMoveDestinationHasPeg() {

        model.makeMove(3,1,3,3);

        assertEquals(SolitaireModel.PEG, model.getCellState(3,3));
    }

    @Test
    @DisplayName("User Story 4-AC5: Legal move decrements peg count")
    void testLegalMoveDecrementsPegCount() {

        int before = model.getPegsRemaining();

        model.makeMove(3,1,3,3);

        assertEquals(before - 1, model.getPegsRemaining());
    }

    @Test
    @DisplayName("User Story 4-AC6: Illegal move returns false")
    void testIllegalMoveReturnsFalse() {

        assertFalse(model.makeMove(3,3,3,5));
    }

    @Test
    @DisplayName("User Story 4-AC7: Move to occupied cell is illegal")
    void testMoveToOccupiedCellReturnsFalse() {

        model.makeMove(3,1,3,3);

        assertFalse(model.makeMove(1,3,3,3));
    }

    @Test
    @DisplayName("User Story 4-AC8: Illegal move does not change peg count")
    void testIllegalMoveDoesNotChangePegCount() {

        int before = model.getPegsRemaining();

        model.makeMove(0,0,0,2);

        assertEquals(before, model.getPegsRemaining());
    }

    @Test
    @DisplayName("User Story 4-AC9: Diagonal move is illegal")
    void testDiagonalMoveIsIllegal() {

        assertFalse(model.makeMove(2,2,4,4));
    }

    @Test
    @DisplayName("User Story 4-AC10: isLegalMove reflects board state")
    void testIsLegalMoveBeforeAndAfter() {

        assertTrue(model.isLegalMove(3,1,3,3));

        model.makeMove(3,1,3,3);

        assertFalse(model.isLegalMove(3,1,3,3));
    }

    // =======================================================================
    // User Story 5 – A game is over
    // =======================================================================

    @Test
    @DisplayName("User Story 5-AC1: New board is not game over")
    void testNewBoardNotGameOver() {

        assertFalse(model.isGameOver());
    }

    @Test
    @DisplayName("User Story 5-AC2: New board is not won")
    void testNewBoardNotWon() {

        assertFalse(model.isWon());
    }

    @Test
    @DisplayName("User Story 5-AC3: One peg remaining is a win")
    void testOnePegAtCentreIsWon() {

        SolitaireModel winModel =
                new SolitaireModelTestHelper().buildWonBoard();

        assertTrue(winModel.isWon());
        assertTrue(winModel.isGameOver());
    }

    @Test
    @DisplayName("User Story 5-AC4: No legal moves results in game over")
    void testGameOverWhenNoMovesPossible() {

        SolitaireModel stuckModel =
                new SolitaireModelTestHelper().buildStuckBoard();

        assertTrue(stuckModel.isGameOver());
        assertFalse(stuckModel.isWon());
    }
}