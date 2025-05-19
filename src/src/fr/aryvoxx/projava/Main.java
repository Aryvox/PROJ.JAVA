package fr.aryvoxx.projava;

import fr.aryvoxx.projava.view.GameFrame;


public class Main {
    public static void main(String[] args) {
        // Utilisation de SwingUtilities pour assurer que l'interface graphique est créée dans le bon thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
