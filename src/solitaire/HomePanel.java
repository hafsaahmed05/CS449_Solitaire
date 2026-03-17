package solitaire;

import javax.swing.*;
import java.awt.*;

/**
 * HomePanel displays the welcome screen shown when the program first starts.
 *
 * It shows a short welcome message and the default game settings
 * (English board, 7x7 size). These options are displayed for the user
 * but are not selectable in this version of the program.
 *
 * The panel also includes a "Start Game" button that notifies the
 * controller when the user is ready to begin the game.
 */

public class HomePanel extends JPanel {

    private static final Color BG_COLOR   = new Color(0xFAF3E0);
    private static final Color TEXT_COLOR = new Color(0x2C3E50);

    public interface StartListener {
        void onStartGame();
    }

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

        JRadioButton englishBtn  = new JRadioButton("English");
        JRadioButton hexagonBtn  = new JRadioButton("Hexagon");
        JRadioButton diamondBtn  = new JRadioButton("Diamond");

        // Style each radio button
        for (JRadioButton btn : new JRadioButton[]{englishBtn, hexagonBtn, diamondBtn}) {
            btn.setFont(new Font("Georgia", Font.PLAIN, 14));
            btn.setForeground(TEXT_COLOR);
            btn.setBackground(BG_COLOR);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setEnabled(false); // not selectable yet
        }

        // Default: English selected
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(englishBtn);
        typeGroup.add(hexagonBtn);
        typeGroup.add(diamondBtn);
        englishBtn.setSelected(true);

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

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(7, 5, 15, 2);
        JSpinner sizeSpinner = new JSpinner(spinnerModel);
        sizeSpinner.setFont(new Font("Georgia", Font.PLAIN, 14));
        sizeSpinner.setEnabled(false); // not selectable yet
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
}