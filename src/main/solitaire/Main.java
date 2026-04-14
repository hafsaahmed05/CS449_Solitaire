package main.solitaire;

import javax.swing.SwingUtilities;

import main.solitaire.view.*;
import main.solitaire.controller.*;

/**
 * Main — application entry point.
 * Creates the Model-View-Controller  on the Swing Event Dispatch Thread and makes the
 * window visible.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SolitaireView view = new SolitaireView();
            new SolitaireController(view);
            view.setVisible(true);
        });
    }
}