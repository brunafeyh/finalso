package prodconsumsemaphore.view;

import prodconsumsemaphore.controller.SimulationController;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface gráfica para a simulação do problema Produtor-Consumidor.
 * Permite ao usuário iniciar e parar a simulação, configurar parâmetros,
 * e visualizar o estado do buffer e o log de atividades.
 */
public class ProdutorConsumidorGUI extends JFrame {
    private final JTextField bufferSizeField = new JTextField("5", 5);
    private final JTextField producerSpeedField = new JTextField("500", 5);
    private final JTextField consumerSpeedField = new JTextField("700", 5);
    private final SimulationController simulationController;
    private final JPanel bufferPanel;
    private final List<JLabel> bufferCells = new ArrayList<>();
    private final JTextArea logArea = new JTextArea(1, 20);

    /**
     * Construtor da interface gráfica. Configura os componentes da GUI,
     * incluindo controles, painel de log, painel do buffer e gráfico.
     */
    public ProdutorConsumidorGUI() {
        setTitle("Produtor-Consumidor com Visualização");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de controle com campos de entrada e botões de iniciar/parar
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Tamanho do Buffer:"));
        controlPanel.add(bufferSizeField);
        controlPanel.add(new JLabel("Velocidade do Produtor (ms):"));
        controlPanel.add(producerSpeedField);
        controlPanel.add(new JLabel("Velocidade do Consumidor (ms):"));
        controlPanel.add(consumerSpeedField);

        JButton startButton = new JButton("Iniciar");
        startButton.addActionListener(_ -> startSimulation());

        JButton stopButton = new JButton("Parar");
        stopButton.addActionListener(_ -> stopSimulation());

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.NORTH);

        simulationController = new SimulationController(this);

        // Painel principal com gráfico, log e visualização do buffer
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ChartPanel chartPanel = simulationController.getChartPanel();
        chartPanel.setPreferredSize(new Dimension(800, 200));
        chartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(chartPanel);

        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(800, 25));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scrollPane);

        bufferPanel = new JPanel();
        bufferPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bufferPanel.setPreferredSize(new Dimension(800, 60));
        mainPanel.add(bufferPanel);

        add(mainPanel, BorderLayout.CENTER);

        initializeBufferPanel(5);  // Inicializa o painel do buffer com tamanho padrão de 5
    }

    /**
     * Inicializa o painel do buffer com células para representar o conteúdo do buffer.
     * @param bufferSize Tamanho do buffer a ser exibido.
     */
    private void initializeBufferPanel(int bufferSize) {
        bufferPanel.removeAll();
        bufferPanel.setLayout(new GridLayout(1, bufferSize, 5, 5));
        bufferCells.clear();

        int cellSize = 50;

        // Cria e adiciona células ao painel do buffer
        for (int i = 0; i < bufferSize; i++) {
            JLabel cell = new JLabel("", SwingConstants.CENTER);
            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            cell.setPreferredSize(new Dimension(cellSize, cellSize));
            bufferCells.add(cell);
            bufferPanel.add(cell);
        }

        bufferPanel.revalidate();
        bufferPanel.repaint();
    }

    /**
     * Inicia a simulação com os parâmetros fornecidos pelo usuário.
     * Lê o tamanho do buffer, a velocidade do produtor e do consumidor.
     */
    private void startSimulation() {
        int bufferSize = Integer.parseInt(bufferSizeField.getText());
        int producerSpeed = Integer.parseInt(producerSpeedField.getText());
        int consumerSpeed = Integer.parseInt(consumerSpeedField.getText());

        initializeBufferPanel(bufferSize);  // Atualiza o painel do buffer com o novo tamanho
        simulationController.startSimulation(bufferSize, producerSpeed, consumerSpeed);
    }

    /**
     * Para a simulação chamando o método stopSimulation do controlador.
     */
    private void stopSimulation() {
        simulationController.stopSimulation();
    }

    /**
     * Atualiza a visualização do buffer com os itens atuais.
     * @param bufferContents Lista de itens presentes no buffer.
     */
    public void updateBufferDisplay(List<Integer> bufferContents) {
        for (int i = 0; i < bufferCells.size(); i++) {
            if (i < bufferContents.size()) {
                bufferCells.get(i).setText(bufferContents.get(i).toString());
            } else {
                bufferCells.get(i).setText("");
            }
        }
    }

    /**
     * Adiciona uma mensagem ao log de atividades.
     * @param message Mensagem a ser registrada no log.
     */
    public void logMessage(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     * Método principal para inicializar a GUI.
     * @param args Argumentos da linha de comando.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProdutorConsumidorGUI gui = new ProdutorConsumidorGUI();
            gui.setVisible(true);
        });
    }
}