package main.solitaire.controller;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

import main.solitaire.model.*;
import main.solitaire.view.*;
import main.solitaire.recorder.*;

/**
 * SolitaireController — wires SolitaireModel and SolitaireView (MVC).
 *
 * Implements:
 *   - SolitaireView.ViewListener   (New Game / Home events)
 *   - CellButton.CellClickListener (cell clicks from BoardPanel)
 *
 * When a new game is requested the controller asks the view which board
 * type and size the user selected, constructs the correct model subclass,
 * and hands it to the view.
 */
public class SolitaireController
        implements SolitaireView.ViewListener,
        CellButton.CellClickListener {

    private SolitaireModel model;
    private final SolitaireView view;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private final GameRecorder recorder = new GameRecorder();

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    public SolitaireController(SolitaireView view) {
        this.view = view;
        view.setViewListener(this);
        view.setCellClickListener(this);

        // Start with default English 7
        this.model = new EnglishModel(7);
        view.setModel(model);
        syncView();
    }

    // ------------------------------------------------------------------
    // ViewListener
    // ------------------------------------------------------------------

    @Override
    public void onNewGame() {
        String type = view.getSelectedBoardType();
        int    size = view.getSelectedBoardSize();

        model = buildModel(type, size);
        clearSelection();
        view.setModel(model);
        syncView();
        view.setStatus("New game started. Select a peg to move.");
    }

    private SolitaireModel buildModel(String type, int size) {
        switch (type) {
            case "Hexagon": return new HexagonModel(size);
            case "Diamond": return new DiamondModel(size);
            default:        return new EnglishModel(size);
        }
    }

    // ------------------------------------------------------------------
    // CellClickListener
    // ------------------------------------------------------------------

    @Override
    public void onCellClicked(int row, int col) {
        if (!model.isValidCell(row, col)) {
            clearSelection();
            return;
        }

        int state = model.getCellState(row, col);

        if (selectedRow == -1) {
            if (state == SolitaireModel.PEG) {
                selectedRow = row;
                selectedCol = col;
                view.setSelectedCell(row, col);
                view.setStatus("Peg selected at (" + row + ", " + col + "). Click a destination.");
            } else {
                view.setStatus("Please click on a peg to select it.");
            }
        } else {
            if (row == selectedRow && col == selectedCol) {
                clearSelection();
                view.setStatus("Selection cleared.");
                return;
            }

            if (state == SolitaireModel.PEG) {
                selectedRow = row;
                selectedCol = col;
                view.setSelectedCell(row, col);
                view.setStatus("Peg re-selected at (" + row + ", " + col + "). Click a destination.");
                return;
            }

            boolean moved = model.makeMove(selectedRow, selectedCol, row, col);

            if (moved) {
                recorder.recordMove(selectedRow, selectedCol, row, col);
                clearSelection();
                syncView();

                if (model.isWon()) {
                    view.showGameOver(true);
                    saveRecordingIfEnabled();
                } else if (model.isGameOver()) {
                    view.showGameOver(false);
                    saveRecordingIfEnabled();
                }
            } else {
                view.setStatus("Illegal move — select a peg and jump over an adjacent peg.");
            }
        }
    }

    @Override
    public void onAutoplay() {
        if (view.isRecordingEnabled()) {
            recorder.startRecording(
                    view.getSelectedBoardType(),
                    view.getSelectedBoardSize(),
                    model.getBoardState()
            );
        }

        new Thread(() -> {
            while (!model.isGameOver()) {
                int[] moveCoords = model.getNextMove();

                if (moveCoords == null) break;

                boolean moved = model.makeAnyMove();
                if (!moved) break;

                recorder.recordMove(moveCoords[0], moveCoords[1], moveCoords[2], moveCoords[3]);

                try {
                    Thread.sleep(300);
                } catch (InterruptedException ignored) {}

                SwingUtilities.invokeLater(this::syncView);
            }

            SwingUtilities.invokeLater(() -> {
                saveRecordingIfEnabled();
                if (model.isWon()) {
                    view.showGameOver(true);
                } else {
                    view.showGameOver(false);
                }
            });

        }).start();
    }

    @Override
    public void onRandomize() {
        model.randomizeBoard();
        clearSelection();
        syncView();
        recorder.recordRandomize(model.getBoardState());
        view.setStatus("Board randomized!");
    }

    @Override
    public void onReplay() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a recorded game file");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files (*.txt)", "txt"));

        int result = fileChooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return;

        String filename = fileChooser.getSelectedFile().getAbsolutePath();

        List<String> lines;
        try {
            lines = recorder.loadFromFile(filename);
        } catch (IOException e) {
            view.setStatus("Error loading file: " + e.getMessage());
            return;
        }

        // Validate required fields exist
        boolean hasBoardType      = lines.stream().anyMatch(GameRecorder::isBoardType);
        boolean hasBoardSize      = lines.stream().anyMatch(GameRecorder::isBoardSize);
        boolean hasInitialState   = lines.stream().anyMatch(GameRecorder::isInitialState);
        boolean hasAtLeastOneMove = lines.stream().anyMatch(GameRecorder::isMove);

        if (lines.isEmpty() || !hasBoardType || !hasBoardSize || !hasInitialState || !hasAtLeastOneMove) {
            view.setStatus("Invalid file — not a valid recorded game.");
            return;
        }

        // Validate every line is a recognized format
        for (String line : lines) {
            if (!GameRecorder.isBoardType(line)
                    && !GameRecorder.isBoardSize(line)
                    && !GameRecorder.isInitialState(line)
                    && !GameRecorder.isMove(line)
                    && !GameRecorder.isRandomize(line)) {
                view.setStatus("Invalid file — file contains unrecognized data.");
                return;
            }
        }

        // Validate move lines are parseable
        try {
            for (String line : lines) {
                if (GameRecorder.isMove(line)) {
                    int[] m = GameRecorder.parseMove(line);
                    if (m.length != 4) throw new Exception();
                } else if (GameRecorder.isInitialState(line) || GameRecorder.isRandomize(line)) {
                    GameRecorder.parseBoardState(line);
                } else if (GameRecorder.isBoardSize(line)) {
                    GameRecorder.parseBoardSize(line);
                }
            }
        } catch (Exception e) {
            view.setStatus("Invalid file — file data is corrupted or unreadable.");
            return;
        }

        // Parse header
        String boardType = "English";
        int boardSize = 7;
        int[] initialState = null;

        for (String line : lines) {
            if (GameRecorder.isBoardType(line))    boardType    = GameRecorder.parseBoardType(line);
            if (GameRecorder.isBoardSize(line))    boardSize    = GameRecorder.parseBoardSize(line);
            if (GameRecorder.isInitialState(line)) initialState = GameRecorder.parseBoardState(line);
        }

        // Rebuild model from recorded starting state
        model = buildModel(boardType, boardSize);
        if (initialState != null) model.setBoardState(initialState);
        clearSelection();
        view.setModel(model);
        syncView();
        view.setStatus("Replaying game...");

        final List<String> replayLines = lines;
        new Thread(() -> {
            for (String line : replayLines) {
                if (GameRecorder.isMove(line)) {
                    int[] m = GameRecorder.parseMove(line);
                    model.makeMove(m[0], m[1], m[2], m[3]);

                    try { Thread.sleep(400); } catch (InterruptedException ignored) {}

                    SwingUtilities.invokeLater(this::syncView);

                } else if (GameRecorder.isRandomize(line)) {
                    int[] state = GameRecorder.parseBoardState(line);
                    model.setBoardState(state);

                    try { Thread.sleep(400); } catch (InterruptedException ignored) {}

                    SwingUtilities.invokeLater(this::syncView);
                }
            }

            SwingUtilities.invokeLater(() -> view.setStatus("Replay finished."));

        }).start();
    }

    @Override
    public void onGoHome() {
        saveRecordingIfEnabled();
    }

    @Override
    public void onStartRecording() {
        recorder.startRecording(
                view.getSelectedBoardType(),
                view.getSelectedBoardSize(),
                model.getBoardState()
        );
        view.setStatus("Recording enabled.");
    }

    @Override
    public void onStopRecording() {
        saveRecordingIfEnabled();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void saveRecordingIfEnabled() {
        if (!recorder.isRecording()) return;
        recorder.stopRecording();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save recorded game as...");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files (*.txt)", "txt"));

        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filename.endsWith(".txt")) filename += ".txt";
            try {
                recorder.saveToFile(filename);
                view.setStatus("Game saved to: " + filename);
            } catch (IOException e) {
                view.setStatus("Error saving file: " + e.getMessage());
            }
        }
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        view.clearSelection();
    }

    private void syncView() {
        view.setPegsLabel(model.getPegsRemaining());
        view.refreshBoard();
    }
}