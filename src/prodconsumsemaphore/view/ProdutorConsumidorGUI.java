package prodconsumsemaphore.view;
import prodconsumsemaphore.controller.SimulationController;
import org.jfree.chart.ChartPanel;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutorConsumidorGUI extends JFrame {
    private final JTextField bufferSizeField = new JTextField("5", 5);
    private final JTextField producerSpeedField = new JTextField("500", 5);
    private final JTextField consumerSpeedField = new JTextField("700", 5);
    private final SimulationController simulationController;
    private final JPanel bufferPanel;
    private final List<JLabel> bufferCells = new ArrayList<>();
    private final JTextArea logArea = new JTextArea(1, 20);

    public ProdutorConsumidorGUI() {
        setTitle("Produtor-Consumidor com Visualização");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

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

        initializeBufferPanel(5);
    }

    private void initializeBufferPanel(int bufferSize) {
        bufferPanel.removeAll();
        bufferPanel.setLayout(new GridLayout(1, bufferSize, 5, 5));
        bufferCells.clear();

        int cellSize = 50;

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

    private void startSimulation() {
        int bufferSize = Integer.parseInt(bufferSizeField.getText());
        int producerSpeed = Integer.parseInt(producerSpeedField.getText());
        int consumerSpeed = Integer.parseInt(consumerSpeedField.getText());

        initializeBufferPanel(bufferSize);
        simulationController.startSimulation(bufferSize, producerSpeed, consumerSpeed);
    }

    private void stopSimulation() {
        simulationController.stopSimulation();
    }

    public void updateBufferDisplay(List<Integer> bufferContents) {
        for (int i = 0; i < bufferCells.size(); i++) {
            if (i < bufferContents.size()) {
                bufferCells.get(i).setText(bufferContents.get(i).toString());
            } else {
                bufferCells.get(i).setText("");
            }
        }
    }

    public void logMessage(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProdutorConsumidorGUI gui = new ProdutorConsumidorGUI();
            gui.setVisible(true);
        });
    }
}
