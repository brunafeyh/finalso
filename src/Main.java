import prodconsum.MessageVisualization;
import prodconsumsemaphore.view.ProdutorConsumidorGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        String[] options = {"Solução Produtor e Consumidor com passagem de mensagem", "Solução Produtor e Consumidor por Semáforo"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Selecione a versão para executar:",
                "Escolha de Execução",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0:
                System.out.println("Iniciando Produtor e Consumidor sem semáforo.");
                MessageVisualization.main(new String[]{});
                break;

            case 1:
                System.out.println("Iniciando Produtor e Consumidor com semáforo.");
                javax.swing.SwingUtilities.invokeLater(() -> {
                    ProdutorConsumidorGUI gui = new ProdutorConsumidorGUI();
                    gui.setVisible(true);
                });
                break;

            default:
                System.out.println("Nenhuma opção selecionada. Encerrando o programa.");
                break;
        }
    }
}
