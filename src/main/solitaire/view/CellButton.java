package main.solitaire.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * CellButton represents one clickable cell on the Solitaire board.
 * Each button displays the state of its cell:
 * - nothing if the cell is invalid
 * - an empty spot if the cell is empty
 * - a peg if the cell contains a piece (highlighted when selected)
 *
 * This class only handles the visual representation and click events.
 * It is fully decoupled from SolitaireModel — it uses its own CellState enum.
 */
public class CellButton extends JPanel {

    // Visual constants
    public static final int CELL_SIZE  = 60;
    public static final int PEG_RADIUS = 20;

    private static final Color PEG_COLOR      = new Color(0x2F2F2F);
    private static final Color PEG_SELECTED   = new Color(0x800020);
    private static final Color EMPTY_COLOR    = new Color(0xE6C79C);
    private static final Color SOCKET_BORDER  = new Color(0x8B5A2B);

    // ── Decoupled state enum ─────────────────────────────────────────
    /** View-level cell state — no dependency on SolitaireModel. */
    public enum CellState { INVALID, EMPTY, PEG }

    // Callback interface
    public interface CellClickListener {
        void onCellClicked(int row, int col);
    }

    // State
    private CellState cellState;
    private boolean   selected;
    private final int row;
    private final int col;

    // Construction
    public CellButton(int row, int col, CellState initialState, CellClickListener listener) {
        this.row       = row;
        this.col       = col;
        this.cellState = initialState;
        this.selected  = false;

        setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        setMinimumSize(new Dimension(CELL_SIZE, CELL_SIZE));
        setMaximumSize(new Dimension(CELL_SIZE, CELL_SIZE));
        setBackground(new Color(0xFAF3E0));
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cellState != CellState.INVALID) {
                    listener.onCellClicked(row, col);
                }
            }
        });
    }

    // State setters (called by BoardPanel)
    public void setCellState(CellState state) {
        this.cellState = state;
        repaint();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (cellState == CellState.INVALID) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padding  = 6;
        int diameter = Math.min(getWidth(), getHeight()) - 2 * padding;
        int x = (getWidth()  - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        // Empty socket
        g2.setColor(EMPTY_COLOR);
        g2.fillOval(x, y, diameter, diameter);
        g2.setColor(SOCKET_BORDER);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y, diameter, diameter);

        // Peg
        if (cellState == CellState.PEG) {
            g2.setColor(selected ? PEG_SELECTED : PEG_COLOR);
            g2.fillOval(x, y, diameter, diameter);
        }

        g2.dispose();
    }
}