package solitaire;

import javax.swing.SwingUtilities;

/**
 * Main — application entry point.
 * Creates the Model-View-Controller  on the Swing Event Dispatch Thread and makes the
 * window visible.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SolitaireModel      model      = new SolitaireModel();
            SolitaireView       view       = new SolitaireView();
            SolitaireController controller = new SolitaireController(model, view);
            view.setVisible(true);
        });
    }
}