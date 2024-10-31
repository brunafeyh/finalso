package prodconsumsemaphore;

import prodconsumsemaphore.view.ProdutorConsumidorGUI;

public class Principal {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ProdutorConsumidorGUI gui = new ProdutorConsumidorGUI();
            gui.setVisible(true);
        });
    }
}
