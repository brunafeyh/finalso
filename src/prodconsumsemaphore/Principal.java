package prodconsumsemaphore;

import prodconsumsemaphore.view.ProdutorConsumidorGUI;

/**
 * Classe principal para iniciar a aplicação Produtor-Consumidor com interface gráfica.
 * Executa a interface gráfica em uma thread separada da GUI.
 */
public class Principal {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ProdutorConsumidorGUI gui = new ProdutorConsumidorGUI();
            gui.setVisible(true);
        });
    }
}
