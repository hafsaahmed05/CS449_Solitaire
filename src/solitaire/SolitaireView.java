package solitaire;

import javax.swing.*;
import java.awt.*;

/**
 * SolitaireView represents the main window of the application.
 * It creates the toolbar, board panel, and status bar, and
 * provides methods the controller uses to update the UI.
 **/

public class SolitaireView extends JFrame {

    private static final Color BG_COLOR    = new Color(0xFAF3E0);
    private static final Color PEG_SELECTED = new Color(0x800020);
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

    /** Listener for high-level UI events (implemented by SolitaireController). */
    public interface ViewListener {
        void onNewGame();
    }

    private ViewListener viewListener;

    // Construction

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

            // If already on the home screen, do nothing
            if (!isGameShowing()) {
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Return to the home screen?",
                    "Home",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                showHome();
            }
        });

        topBar.add(titleLabel);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(pegsLabel);
        topBar.add(newGameButton);
        add(topBar, BorderLayout.NORTH);

        // Panels that switch between HOME and GAME
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

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
        HomePanel homePanel = new HomePanel(() -> {
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

    // Wiring

    public void setViewListener(ViewListener listener) {
        this.viewListener = listener;
    }

    public void setCellClickListener(CellButton.CellClickListener listener) {
        boardPanel.setCellClickListener(listener);
    }

    public void setModel(SolitaireModel model) {
        boardPanel.setModel(model);
    }

    // Update methods (called by controller)

    public void refreshBoard()                    { boardPanel.refresh(); }
    public void setSelectedCell(int row, int col) { boardPanel.setSelectedCell(row, col); }
    public void clearSelection()                  { boardPanel.clearSelection(); }
    public void setStatus(String msg)             { statusLabel.setText(msg); }
    public void setPegsLabel(int count)           { pegsLabel.setText("Pegs: " + count); }

    public void showGame() {
        cardLayout.show(mainPanel, GAME);
        gameShowing = true;
    }

    public void showHome() {
        cardLayout.show(mainPanel, HOME);
        gameShowing = false;
    }

    private boolean isGameShowing() { return gameShowing;}

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