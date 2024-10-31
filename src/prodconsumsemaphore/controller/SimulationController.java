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

public class SimulationController {
    private ConsumerProducer consumerProducer;
    private final XYSeries bufferSeries;
    private Timer chartUpdateTimer;
    private long startTime;
    private final ProdutorConsumidorGUI gui;

    public SimulationController(ProdutorConsumidorGUI gui) {
        this.gui = gui;
        bufferSeries = new XYSeries("Itens no Buffer");
    }

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
        }, 0, 500);
    }

    public void stopSimulation() {
        if (consumerProducer != null) {
            consumerProducer.stop();
        }
        if (chartUpdateTimer != null) {
            chartUpdateTimer.cancel();
        }
    }

    public void updateBufferDisplay() {
        int[] bufferContents = consumerProducer.getBufferContents();
        gui.updateBufferDisplay(convertToList(bufferContents));
    }

    public void logMessage(String message) {
        gui.logMessage(message);
    }

    private java.util.List<Integer> convertToList(int[] array) {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        for (int value : array) {
            list.add(value);
        }
        return list;
    }
}
