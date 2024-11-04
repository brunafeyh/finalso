package prodconsum;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Queue;

/**
 * Classe MessageVisualization fornece a interface gráfica para visualizar o problema Produtor-Consumidor.
 * Exibe gráficos de linha e de barras, o estado do buffer e um log de atividades.
 */
public class MessageVisualization extends JFrame {
    private final XYSeries producerSeries;
    private final XYSeries consumerSeries;
    private final DefaultCategoryDataset messageProcessedDataset;
    private final JPanel bufferPanel;
    private final JTextArea logArea;
    private int producerCounter = 0;
    private int consumerCounter = 0;
    private int processedMessageCounter = 0;
    private Producer producer;
    private Consumer consumer;
    private Thread producerThread;
    private Timer updateTimer;
    private final BlockingQueueBuffer buffer;

    public MessageVisualization(BlockingQueueBuffer buffer, int bufferCapacity) {
        this.buffer = buffer;  // Armazena o buffer como um campo da classe para poder acessá-lo no stopProducerConsumer

        setTitle("Message Exchange Visualization");
        setLayout(new BorderLayout());
        setSize(1000, 700);

        // Configuração dos gráficos de linha e barras
        producerSeries = new XYSeries("Producer Messages");
        consumerSeries = new XYSeries("Consumer Messages");
        XYSeriesCollection lineDataset = new XYSeriesCollection();
        lineDataset.addSeries(producerSeries);
        lineDataset.addSeries(consumerSeries);
        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Message Exchange Over Time",
                "Time (ms)",
                "Message Count",
                lineDataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        ChartPanel lineChartPanel = new ChartPanel(lineChart);

        messageProcessedDataset = new DefaultCategoryDataset();
        JFreeChart barChart = ChartFactory.createBarChart(
                "Messages Processed Per Second",
                "Time (seconds)",
                "Cumulative Message Count",
                messageProcessedDataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        ChartPanel barChartPanel = new ChartPanel(barChart);

        // Painel de visualização do buffer
        bufferPanel = new JPanel(new GridLayout(1, bufferCapacity, 5, 5));
        for (int i = 0; i < bufferCapacity; i++) {
            JPanel square = new JPanel();
            square.setPreferredSize(new Dimension(50, 50));
            square.setBackground(Color.LIGHT_GRAY);
            square.setLayout(new BorderLayout());
            JLabel label = new JLabel("", SwingConstants.CENTER);
            square.add(label, BorderLayout.CENTER);
            bufferPanel.add(square);
        }

        // Área de log para exibir mensagens de atividade
        logArea = new JTextArea(10, 20);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        JPanel chartPanel = new JPanel(new GridLayout(2, 1));
        chartPanel.add(lineChartPanel);
        chartPanel.add(barChartPanel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(logScrollPane, BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);

        add(bufferPanel, BorderLayout.SOUTH);

        // Painel de controle com botões de iniciar/parar
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Iniciar");
        JButton stopButton = new JButton("Parar");

        startButton.addActionListener(_ -> startProducerConsumer(buffer, bufferCapacity));
        stopButton.addActionListener(_ -> stopProducerConsumer());

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.NORTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void startProducerConsumer(BlockingQueueBuffer buffer, int bufferCapacity) {
        if (producerThread == null || !producerThread.isAlive()) {
            // Reinicia os gráficos e contadores
            producerSeries.clear();
            consumerSeries.clear();
            producerCounter = 0;
            consumerCounter = 0;
            processedMessageCounter = 0;
            messageProcessedDataset.clear();

            producer = new Producer(buffer);
            consumer = new Consumer(buffer, bufferCapacity);
            producerThread = new Thread(producer);
            Thread consumerThread = new Thread(consumer);
            producerThread.start();
            consumerThread.start();
            logArea.append("Producer and Consumer started.\n");

            // Inicia o Timer quando o produtor e o consumidor começam
            if (updateTimer == null) {
                updateTimer = new Timer(1000, _ -> updateVisualization(buffer, bufferCapacity));
                updateTimer.start();
            }
        } else {
            logArea.append("Producer and Consumer are already running.\n");
        }
    }

    private void stopProducerConsumer() {
        if (producer != null && consumer != null) {
            producer.stop();
            consumer.stop();
            logArea.append("Producer and Consumer stopped.\n");

            // Para o Timer quando o produtor e o consumidor param
            if (updateTimer != null) {
                updateTimer.stop();
                updateTimer = null;
            }

            // Limpa o buffer
            buffer.clearBuffer();
            logArea.append("Buffer cleared.\n");

            // Atualiza a interface para refletir o buffer vazio
            updateVisualization(buffer, buffer.getCapacity());
        }
    }

    private void updateVisualization(BlockingQueueBuffer buffer, int bufferCapacity) {
        // Atualiza gráfico de mensagens do produtor e consumidor
        producerSeries.add(producerCounter++, buffer.getProducerMessageCount());
        consumerSeries.add(consumerCounter++, buffer.getConsumerMessageCount());

        int processedMessages = buffer.getConsumerMessageCount();
        processedMessageCounter += processedMessages;
        messageProcessedDataset.addValue(processedMessageCounter, "Messages", String.valueOf(consumerCounter));

        // Atualiza visualização do estado do buffer
        Queue<String> messageQueue = buffer.getMessageQueue();
        Component[] squares = bufferPanel.getComponents();
        int i = 0;
        for (String message : messageQueue) {
            if (i < bufferCapacity) {
                JPanel square = (JPanel) squares[i];
                JLabel label = (JLabel) square.getComponent(0);
                label.setText(message);
                square.setBackground(Color.GREEN);
                i++;
            } else break;
        }

        for (; i < bufferCapacity; i++) {
            JPanel square = (JPanel) squares[i];
            JLabel label = (JLabel) square.getComponent(0);
            label.setText("");
            square.setBackground(Color.LIGHT_GRAY);
        }

        // Adiciona mensagens ao log
        String logMessages = buffer.getAllLogs();
        if (!logMessages.isEmpty()) {
            logArea.append(logMessages + "\n");
        }
    }

    /**
     * Método principal para inicializar a interface gráfica.
     * Solicita a entrada do usuário para definir o tamanho do buffer.
     * @param args Argumentos da linha de comando.
     */
    public static void main(String[] args) {
        String input = JOptionPane.showInputDialog("Enter buffer size:");
        int bufferCapacity;
        try {
            bufferCapacity = Integer.parseInt(input);
            if (bufferCapacity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid buffer size. Using default size of 5.");
            bufferCapacity = 5;
        }

        BlockingQueueBuffer buffer = new BlockingQueueBuffer(bufferCapacity);
        new MessageVisualization(buffer, bufferCapacity);
    }
}
