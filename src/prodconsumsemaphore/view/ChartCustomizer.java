package prodconsumsemaphore.view;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Classe utilitária para personalizar a aparência do gráfico, ajustando cores, formatação dos eixos
 * e visibilidade das linhas de grade para melhor visualização.
 */
public class ChartCustomizer {

    /**
     * Personaliza o gráfico definindo o fundo, a formatação dos eixos e as linhas de grade.
     * Ajusta a cor e estilo dos dados exibidos no gráfico.
     * @param chart O gráfico a ser customizado.
     */
    public static void customizeChart(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.WHITE);

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setAutoRange(true);
        yAxis.setNumberFormatOverride(new DecimalFormat("#.##"));

        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setAutoRange(true);
        xAxis.setTickUnit(new NumberTickUnit(1));

        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlineStroke(new BasicStroke(0.5f));

        plot.getRenderer().setSeriesPaint(0, Color.GREEN);
    }
}
