package solitaire;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GameRecorder handles recording and replaying Solitaire games.
 * Records moves, randomize events, and board metadata to a text file.
 */
public class GameRecorder {

    private List<String> moveLog = new ArrayList<>();
    private boolean recording = false;

    // ─────────────────────────────────────────
    // Recording
    // ─────────────────────────────────────────

    /**
     * Start a new recording session. Clears any previous log.
     *
     * @param boardType  e.g. "English", "Hexagon", "Diamond"
     * @param boardSize  e.g. 7
     * @param initialState  flattened board array (1 = peg, 0 = empty, -1 = invalid)
     */
    public void startRecording(String boardType, int boardSize, int[] initialState) {
        moveLog.clear();
        recording = true;
        moveLog.add("BOARD_TYPE:" + boardType);
        moveLog.add("BOARD_SIZE:" + boardSize);
        moveLog.add("INITIAL_STATE:" + flattenArray(initialState));
    }

    /**
     * Record a single move (from one cell to another).
     *
     * @param fromRow  row of the peg being moved
     * @param fromCol  col of the peg being moved
     * @param toRow    row of the destination
     * @param toCol    col of the destination
     */
    public void recordMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!recording) return;
        moveLog.add("MOVE:" + fromRow + "," + fromCol + "," + toRow + "," + toCol);
    }

    /**
     * Record a randomize event by saving the full board state snapshot.
     *
     * @param boardState  current board state after randomizing
     */
    public void recordRandomize(int[] boardState) {
        if (!recording) return;
        moveLog.add("RANDOMIZE:" + flattenArray(boardState));
    }

    /**
     * Stop recording.
     */
    public void stopRecording() {
        recording = false;
    }

    /**
     * Returns true if currently recording.
     */
    public boolean isRecording() {
        return recording;
    }

    // ─────────────────────────────────────────
    // File I/O
    // ─────────────────────────────────────────

    /**
     * Save the recorded game log to a text file.
     *
     * @param filename  path/name of the file to save (e.g. "game_record.txt")
     * @throws IOException if file cannot be written
     */
    public void saveToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : moveLog) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Load a game log from a text file.
     *
     * @param filename  path/name of the file to load
     * @return  list of raw log lines
     * @throws IOException if file cannot be read
     */
    public List<String> loadFromFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        }
        return lines;
    }

    // ─────────────────────────────────────────
    // Parsing helpers (used by Controller on replay)
    // ─────────────────────────────────────────

    /**
     * Parse the board type from a log line.
     * e.g. "BOARD_TYPE:English" → "English"
     */
    public static String parseBoardType(String line) {
        return line.replace("BOARD_TYPE:", "").trim();
    }

    /**
     * Parse the board size from a log line.
     * e.g. "BOARD_SIZE:7" → 7
     */
    public static int parseBoardSize(String line) {
        return Integer.parseInt(line.replace("BOARD_SIZE:", "").trim());
    }

    /**
     * Parse a board state array from an INITIAL_STATE or RANDOMIZE line.
     * e.g. "INITIAL_STATE:1,1,0,1" → int[]{1,1,0,1}
     */
    public static int[] parseBoardState(String line) {
        String data = line.contains(":") ? line.substring(line.indexOf(':') + 1) : line;
        String[] tokens = data.trim().split(",");
        int[] state = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            state[i] = Integer.parseInt(tokens[i].trim());
        }
        return state;
    }

    /**
     * Parse a move from a MOVE line.
     * e.g. "MOVE:3,3,3,5" → int[]{3,3,3,5}  (fromRow, fromCol, toRow, toCol)
     */
    public static int[] parseMove(String line) {
        String data = line.replace("MOVE:", "").trim();
        String[] tokens = data.split(",");
        return new int[]{
                Integer.parseInt(tokens[0].trim()),
                Integer.parseInt(tokens[1].trim()),
                Integer.parseInt(tokens[2].trim()),
                Integer.parseInt(tokens[3].trim())
        };
    }

    /**
     * Check what type of entry a log line is.
     */
    public static boolean isBoardType(String line)    { return line.startsWith("BOARD_TYPE:"); }
    public static boolean isBoardSize(String line)    { return line.startsWith("BOARD_SIZE:"); }
    public static boolean isInitialState(String line) { return line.startsWith("INITIAL_STATE:"); }
    public static boolean isMove(String line)         { return line.startsWith("MOVE:"); }
    public static boolean isRandomize(String line)    { return line.startsWith("RANDOMIZE:"); }

    // ─────────────────────────────────────────
    // Private utility
    // ─────────────────────────────────────────

    /**
     * Convert an int array to a comma-separated string.
     * e.g. {1,0,1} → "1,0,1"
     */
    private String flattenArray(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(",");
        }
        return sb.toString();
    }
}