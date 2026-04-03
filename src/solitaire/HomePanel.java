package solitaire;

import javax.swing.*;
import java.awt.*;

/**
 * HomePanel — welcome screen with working board-type and size selectors.
 *
 * Exposes:
 *   getSelectedType() — "English" / "Hexagon" / "Diamond"
 *   getSelectedSize() — integer from the spinner
 */
public class HomePanel extends JPanel {

    private static final Color BG_COLOR   = new Color(0xFAF3E0);
    private static final Color TEXT_COLOR = new Color(0x2C3E50);

    public interface StartListener {
        void onStartGame();
    }

    // Keep refs so the view can read them
    private final JRadioButton      englishBtn;
    private final JRadioButton      hexagonBtn;
    private final JRadioButton      diamondBtn;
    private final SpinnerNumberModel spinnerModel;

    public HomePanel(StartListener listener) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_COLOR);

        // --- Title ---
        JLabel title = new JLabel("Peg Solitaire");
        title.setFont(new Font("Georgia", Font.BOLD, 30));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcome = new JLabel("Welcome!");
        welcome.setFont(new Font("Georgia", Font.ITALIC, 18));
        welcome.setForeground(TEXT_COLOR);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Board Type ---
        JLabel typeLabel = new JLabel("Board Type:");
        typeLabel.setFont(new Font("Georgia", Font.BOLD, 15));
        typeLabel.setForeground(TEXT_COLOR);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        englishBtn = makeRadio("English");
        hexagonBtn = makeRadio("Hexagon");
        diamondBtn = makeRadio("Diamond");
        englishBtn.setSelected(true);   // default

        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(englishBtn);
        typeGroup.add(hexagonBtn);
        typeGroup.add(diamondBtn);

        JPanel typePanel = new JPanel();
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.Y_AXIS));
        typePanel.setBackground(BG_COLOR);
        typePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        typePanel.add(englishBtn);
        typePanel.add(hexagonBtn);
        typePanel.add(diamondBtn);

        // --- Board Size ---
        JLabel sizeLabel = new JLabel("Board Size:");
        sizeLabel.setFont(new Font("Georgia", Font.BOLD, 15));
        sizeLabel.setForeground(TEXT_COLOR);
        sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // min 5, max 13, step 2 (only odd sizes work cleanly)
        spinnerModel = new SpinnerNumberModel(7, 5, 13, 2);
        JSpinner sizeSpinner = new JSpinner(spinnerModel);
        sizeSpinner.setFont(new Font("Georgia", Font.PLAIN, 14));
        sizeSpinner.setMaximumSize(new Dimension(80, 30));
        sizeSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Start Button ---
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Georgia", Font.BOLD, 14));
        startButton.setBackground(new Color(0xE74C3C));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> listener.onStartGame());

        // --- Layout ---
        add(Box.createVerticalGlue());
        add(title);
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(welcome);
        add(Box.createRigidArea(new Dimension(0, 28)));
        add(typeLabel);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(typePanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(sizeLabel);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(sizeSpinner);
        add(Box.createRigidArea(new Dimension(0, 28)));
        add(startButton);
        add(Box.createVerticalGlue());
    }

    // ------------------------------------------------------------------
    // Accessors (read by SolitaireView, passed to controller)
    // ------------------------------------------------------------------

    public String getSelectedType() {
        if (hexagonBtn.isSelected()) return "Hexagon";
        if (diamondBtn.isSelected()) return "Diamond";
        return "English";
    }

    public int getSelectedSize() {
        return spinnerModel.getNumber().intValue();
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------

    private JRadioButton makeRadio(String text) {
        JRadioButton btn = new JRadioButton(text);
        btn.setFont(new Font("Georgia", Font.PLAIN, 14));
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(BG_COLOR);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}