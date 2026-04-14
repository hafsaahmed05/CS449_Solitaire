package main.solitaire.view;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

import main.solitaire.model.*;

/**
 * SolitaireView represents the main window of the application.
 * It creates the toolbar, board panel, and status bar, and
 * provides methods the controller uses to update the UI.
 *
 * Sprint 4 decoupling: file chooser dialogs moved here from controller,
 * keeping all UI concerns in the view layer.
 */
public class SolitaireView extends JFrame {

    private static final Color BG_COLOR    = new Color(0xFAF3E0);
    private static final Color BOARD_COLOR = new Color(0x8B6914);
    private static final Color TEXT_COLOR  = new Color(0x2C3E50);

    private final BoardPanel boardPanel;
    private final JLabel statusLabel;
    private final JLabel pegsLabel;
    private final JButton newGameButton;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private static final String HOME = "HOME";
    private static final String GAME = "GAME";

    private boolean gameShowing = false;

    private HomePanel homePanel;

    private JButton randomizeButton;
    private JButton autoplayButton;
    private JCheckBox recordGameCheckbox;
    private JButton replayButton;

    /** Listener for high-level UI events (implemented by SolitaireController). */
    public interface ViewListener {
        void onNewGame();
        void onRandomize();
        void onAutoplay();
        void onReplay();
        void onGoHome();
        void onStartRecording();
        void onStopRecording();
    }

    private ViewListener viewListener;

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    public SolitaireView() {
        super("Peg Solitaire — English (7x7)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_COLOR);

        // Top toolbar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        topBar.setBackground(BOARD_COLOR);

        JLabel titleLabel = new JLabel("PEG  SOLITAIRE");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        pegsLabel = new JLabel("Pegs: 32");
        pegsLabel.setFont(new Font("Georgia", Font.PLAIN, 15));
        pegsLabel.setForeground(new Color(0xFAF3E0));

        newGameButton = new JButton("Home");
        newGameButton.setFont(new Font("Georgia", Font.BOLD, 13));
        newGameButton.setBackground(new Color(0xE74C3C));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusPainted(false);
        newGameButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        newGameButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newGameButton.addActionListener(e -> {
            if (!isGameShowing()) return;
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Return to the home screen?",
                    "Home",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                if (viewListener != null) viewListener.onGoHome();
                showHome();
            }
        });

        randomizeButton = new JButton("Randomize");
        randomizeButton.setFont(new Font("Georgia", Font.BOLD, 13));
        randomizeButton.setBackground(new Color(0x3498DB));
        randomizeButton.setForeground(Color.WHITE);
        randomizeButton.setFocusPainted(false);
        randomizeButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        randomizeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        randomizeButton.addActionListener(e -> {
            if (viewListener != null) viewListener.onRandomize();
        });

        autoplayButton = new JButton("Autoplay");
        autoplayButton.setFont(new Font("Georgia", Font.BOLD, 13));
        autoplayButton.setBackground(new Color(0x2ECC71));
        autoplayButton.setForeground(Color.WHITE);
        autoplayButton.setFocusPainted(false);
        autoplayButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        autoplayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        autoplayButton.addActionListener(e -> {
            if (viewListener != null) viewListener.onAutoplay();
        });

        recordGameCheckbox = new JCheckBox("Record game");
        recordGameCheckbox.setFont(new Font("Georgia", Font.PLAIN, 13));
        recordGameCheckbox.setBackground(BOARD_COLOR);
        recordGameCheckbox.setForeground(Color.WHITE);
        recordGameCheckbox.setFocusPainted(false);
        recordGameCheckbox.addActionListener(e -> {
            if (viewListener != null) {
                if (recordGameCheckbox.isSelected()) {
                    viewListener.onStartRecording();
                } else {
                    viewListener.onStopRecording();
                }
            }
        });

        replayButton = new JButton("Replay");
        replayButton.setFont(new Font("Georgia", Font.BOLD, 13));
        replayButton.setBackground(new Color(0x9B59B6));
        replayButton.setForeground(Color.WHITE);
        replayButton.setFocusPainted(false);
        replayButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        replayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        replayButton.addActionListener(e -> {
            if (viewListener != null) viewListener.onReplay();
        });

        recordGameCheckbox.setVisible(false);
        replayButton.setVisible(false);
        randomizeButton.setVisible(false);
        autoplayButton.setVisible(false);

        topBar.add(titleLabel);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(pegsLabel);
        topBar.add(newGameButton);
        topBar.add(autoplayButton);
        topBar.add(randomizeButton);
        topBar.add(replayButton);
        topBar.add(recordGameCheckbox);
        add(topBar, BorderLayout.NORTH);

        // Panels that switch between HOME and GAME
        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);

        // Game panel
        boardPanel = new BoardPanel();
        int boardPx = SolitaireModel.SIZE_CONST * CellButton.CELL_SIZE
                + (SolitaireModel.SIZE_CONST - 1) * 2
                + 20;
        boardPanel.setPreferredSize(new Dimension(boardPx, boardPx));

        JPanel gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBackground(BG_COLOR);
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gamePanel.add(boardPanel);

        // Home panel
        homePanel = new HomePanel(() -> {
            if (viewListener != null) viewListener.onNewGame();
            showGame();
        });

        mainPanel.add(homePanel, HOME);
        mainPanel.add(gamePanel, GAME);
        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("Select a peg to move.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Georgia", Font.ITALIC, 14));
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 10, 0));
        add(statusLabel, BorderLayout.SOUTH);

        cardLayout.show(mainPanel, HOME);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    public String getSelectedBoardType() { return homePanel.getSelectedType(); }
    public int    getSelectedBoardSize() { return homePanel.getSelectedSize(); }
    public boolean isRecordingEnabled()  { return recordGameCheckbox.isSelected(); }

    // ------------------------------------------------------------------
    // Wiring
    // ------------------------------------------------------------------

    public void setViewListener(ViewListener listener)                      { this.viewListener = listener; }
    public void setCellClickListener(CellButton.CellClickListener listener) { boardPanel.setCellClickListener(listener); }
    public void setModel(SolitaireModel model)                              { boardPanel.setModel(model); }

    // ------------------------------------------------------------------
    // Update methods (called by controller)
    // ------------------------------------------------------------------

    public void refreshBoard()                    { boardPanel.refresh(); }
    public void setSelectedCell(int row, int col) { boardPanel.setSelectedCell(row, col); }
    public void clearSelection()                  { boardPanel.clearSelection(); }
    public void setStatus(String msg)             { statusLabel.setText(msg); }
    public void setPegsLabel(int count)           { pegsLabel.setText("Pegs: " + count); }

    // ------------------------------------------------------------------
    // File dialogs (Sprint 4: moved from controller to keep UI in view)
    // ------------------------------------------------------------------

    /**
     * Shows a save file dialog filtered to .txt files.
     * Returns the chosen absolute path (with .txt extension guaranteed),
     * or null if the user cancelled.
     */
    public String showSaveDialog() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save recorded game as...");
        fc.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        int result = fc.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return null;
        String filename = fc.getSelectedFile().getAbsolutePath();
        if (!filename.endsWith(".txt")) filename += ".txt";
        return filename;
    }

    /**
     * Shows an open file dialog filtered to .txt files.
     * Returns the chosen absolute path, or null if the user cancelled.
     */
    public String showOpenDialog() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select a recorded game file");
        fc.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        int result = fc.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return null;
        return fc.getSelectedFile().getAbsolutePath();
    }

    // ------------------------------------------------------------------
    // Screen switching
    // ------------------------------------------------------------------

    public void showGame() {
        cardLayout.show(mainPanel, GAME);
        gameShowing = true;
        randomizeButton.setVisible(true);
        autoplayButton.setVisible(true);
        replayButton.setVisible(true);
        recordGameCheckbox.setVisible(true);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    public void showHome() {
        cardLayout.show(mainPanel, HOME);
        gameShowing = false;
        randomizeButton.setVisible(false);
        autoplayButton.setVisible(false);
        replayButton.setVisible(false);
        recordGameCheckbox.setVisible(false);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private boolean isGameShowing() { return gameShowing; }

    public void showGameOver(boolean won) {
        String msg = won
                ? "You won! Only 1 peg left!"
                : "No more moves — Game Over!  " + pegsLabel.getText();
        JOptionPane.showMessageDialog(
                this, msg,
                won ? "Congratulations!" : "Game Over",
                won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
}