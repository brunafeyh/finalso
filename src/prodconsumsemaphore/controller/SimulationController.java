package prodconsumsemaphore.controller;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import prodconsumsemaphore.view.ChartCustomizer;
import prodconsumsemaphore.view.ProdutorConsumidorGUI;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controlador da simulação que gerencia a execução das threads de produção e consumo,
 * além de atualizar o gráfico e a interface gráfica.
 */
public class SimulationController {
    private ConsumerProducer consumerProducer;
    private final XYSeries bufferSeries;
    private Timer chartUpdateTimer;
    private long startTime;
    private final ProdutorConsumidorGUI gui;

    /**
     * Construtor da classe SimulationController.
     * @param gui Interface gráfica para a simulação.
     */
    public SimulationController(ProdutorConsumidorGUI gui) {
        this.gui = gui;
        bufferSeries = new XYSeries("Itens no Buffer");
    }

    /**
     * Cria e retorna um painel de gráfico para exibir a quantidade de itens no buffer ao longo do tempo.
     * Pré-condição: A série de dados bufferSeries deve estar inicializada.
     * @return Painel de gráfico do buffer.
     */
    public ChartPanel getChartPanel() {
        XYSeriesCollection dataset = new XYSeriesCollection(bufferSeries);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Itens no Buffer ao Longo do Tempo",
                "Tempo (s)",
                "Quantidade de Itens",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartCustomizer.customizeChart(chart);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 200));

        return chartPanel;
    }

    /**
     * Inicia a simulação com os parâmetros especificados.
     * Cria as threads do produtor e consumidor e inicia a atualização do gráfico.
     * @param bufferSize Tamanho do buffer.
     * @param producerSpeed Velocidade de produção (tempo de espera entre produções).
     * @param consumerSpeed Velocidade de consumo (tempo de espera entre consumos).
     */
    public void startSimulation(int bufferSize, int producerSpeed, int consumerSpeed) {
        consumerProducer = new ConsumerProducer(bufferSize, producerSpeed, consumerSpeed, this);
        consumerProducer.start();

        startTime = System.currentTimeMillis();

        chartUpdateTimer = new Timer();
        chartUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    long elapsedMillis = System.currentTimeMillis() - startTime;
                    double elapsedSeconds = elapsedMillis / 1000.0;
                    bufferSeries.add(elapsedSeconds, consumerProducer.getBufferSize());
                    updateBufferDisplay();
                });
            }
        }, 0, 500); // Atualiza o gráfico a cada 500 ms
    }

    /**
     * Para a simulação interrompendo as threads de produção e consumo e cancelando o temporizador.
     */
    public void stopSimulation() {
        if (consumerProducer != null) {
            consumerProducer.stop();
        }
        if (chartUpdateTimer != null) {
            chartUpdateTimer.cancel();
        }
    }

    /**
     * Atualiza a exibição do buffer na interface gráfica.
     * Pré-condição: A simulação deve estar em execução.
     * Pós-condição: O display do buffer na GUI é atualizado.
     */
    public void updateBufferDisplay() {
        int[] bufferContents = consumerProducer.getBufferContents();
        gui.updateBufferDisplay(convertToList(bufferContents));
    }

    /**
     * Exibe uma mensagem de log na interface gráfica.
     * @param message Mensagem a ser exibida.
     */
    public void logMessage(String message) {
        gui.logMessage(message);
    }

    /**
     * Converte um array de inteiros para uma lista de inteiros.
     * @param array Array de inteiros a ser convertido.
     * @return Lista de inteiros correspondente.
     */
    private java.util.List<Integer> convertToList(int[] array) {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        for (int value : array) {
            list.add(value);
        }
        return list;
    }
}
