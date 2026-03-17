package solitaire;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * CellButton represents one clickable cell on the Solitaire board.
 * Each button displays the state of its cell:
 * - nothing if the cell is invalid
 * - an empty spot if the cell is empty
 * - a peg if the cell contains a piece (highlighted when selected)
 *
 * This class only handles the visual representation and click events.
 */

public class CellButton extends JPanel {

    // Visual constants
    public static final int CELL_SIZE  = 60;
    public static final int PEG_RADIUS = 20;

    private static final Color PEG_COLOR      = new Color(0x2F2F2F);  // dark charcoal peg
    private static final Color PEG_SELECTED   = new Color(0x800020);  // muted red highlight
    private static final Color EMPTY_COLOR    = new Color(0xE6C79C);  // light wood socket
    private static final Color SOCKET_BORDER  = new Color(0x8B5A2B);  // darker wood border

    // Callback interface
    /** Implemented by BoardPanel to receive click notifications. */
    public interface CellClickListener {
        void onCellClicked(int row, int col);
    }

    // State
    private int  cellState;   // SolitaireModel.INVALID / EMPTY / PEG
    private boolean selected;
    private final int row;
    private final int col;

    // Construction
    public CellButton(int row, int col, int initialState, CellClickListener listener) {
        this.row       = row;
        this.col       = col;
        this.cellState = initialState;
        this.selected  = false;

        setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cellState != SolitaireModel.INVALID) {
                    listener.onCellClicked(row, col);
                }
            }
        });
    }

    // State setters (called by BoardPanel)
    public void setCellState(int state) {
        this.cellState = state;
        repaint();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    // Painting
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (cellState == SolitaireModel.INVALID) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = CELL_SIZE / 2;
        int cy = CELL_SIZE / 2;

        // Empty socket
        g2.setColor(EMPTY_COLOR);
        g2.fillOval(cx - PEG_RADIUS, cy - PEG_RADIUS, PEG_RADIUS * 2, PEG_RADIUS * 2);
        g2.setColor(SOCKET_BORDER);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(cx - PEG_RADIUS, cy - PEG_RADIUS, PEG_RADIUS * 2, PEG_RADIUS * 2);

        if (cellState == SolitaireModel.PEG) {
            // Drop shadow
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillOval(cx - PEG_RADIUS + 3, cy - PEG_RADIUS + 4, PEG_RADIUS * 2, PEG_RADIUS * 2);

            // Peg body
            g2.setColor(selected ? PEG_SELECTED : PEG_COLOR);
            g2.fillOval(cx - PEG_RADIUS, cy - PEG_RADIUS, PEG_RADIUS * 2, PEG_RADIUS * 2);

            // Shine highlight
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillOval(cx - PEG_RADIUS + 4, cy - PEG_RADIUS + 4, PEG_RADIUS - 4, PEG_RADIUS - 4);

            // Selection ring
            if (selected) {
                g2.setColor(PEG_SELECTED);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(cx - PEG_RADIUS - 3, cy - PEG_RADIUS - 3,
                        PEG_RADIUS * 2 + 6, PEG_RADIUS * 2 + 6);
            }
        }

        g2.dispose();
    }
}