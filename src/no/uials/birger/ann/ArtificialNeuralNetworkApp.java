package no.uials.birger.ann;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.DoubleFunction;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ArtificialNeuralNetworkApp {

    public static int attempts = 0;

    public static void findFunction() {

        DoubleFunction<Double> f = (double x) -> Math.tanh(x);
        DoubleFunction<Double> fD = (double x) -> 1 - Math.pow(f.apply(x), 2);

        final Network network = Network.getRandom(f, fD, 2, 6, 1);
        network.setBiasInput(-1);
        network.setTrainBias(false);

        final XYSeries trainingSeries = new XYSeries("training");
        final XYSeries testingSeries = new XYSeries("testing");

        XYSeries accepted = new XYSeries("Accepted");
        XYSeries rejected = new XYSeries("Rejected");

        XYSeriesCollection errorData = new XYSeriesCollection();
        errorData.addSeries(trainingSeries);
        errorData.addSeries(testingSeries);

        JFreeChart errorChart = ChartFactory.createScatterPlot(null, null,
                null, errorData);
        errorChart.getXYPlot().setRenderer(new XYSplineRenderer());
        errorChart.setAntiAlias(true);
        errorChart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
        errorChart.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);
        ChartFrame errorFrame = new ChartFrame("Error rate", errorChart);

        int nTrain = 500;
        int nTest = 250;

        final double[][] trainingSet = new double[nTrain][];
        for (int i = 0; i < nTrain; i++) {
            trainingSet[i] = new double[2];
            trainingSet[i][0] = (Math.random() * 2 - 1);
            trainingSet[i][1] = (Math.random() * 2 - 1);
        }

        final double[][] testingSet = new double[nTest][];
        for (int i = 0; i < nTest; i++) {
            testingSet[i] = new double[2];
            testingSet[i][0] = (Math.random() * 2 - 1);
            testingSet[i][1] = (Math.random() * 2 - 1);
        }

        KeyListener keyListener = new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                    case KeyEvent.VK_SPACE:
                        accepted.clear();
                        rejected.clear();
                        for (double y = -2; y <= 2; y += 0.5) {
                            for (double x = -2; x <= 2; x += 0.5) {

                                double[] input2 = {x, y};
                                double[] output2 = network.recall(input2);

                                if (output2[0] > 0.0) {
                                    accepted.add(x, y);
                                } else {
                                    rejected.add(x, y);
                                }

                            }

                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        int trainErrors = 0;
                        int testErrors = 0;
                        double[] input = null;
                        double[] output = null;
                        double[] target = new double[1];
                        double sin;
                        for (int i = 0; i < nTrain; i++) {

                            input = trainingSet[i];
                            output = network.recall(input);

                            sin = Math.sin(input[0] * Math.PI);

                            if (input[1] < sin) { // 0

                                target[0] = -1;
                                if (output[0] > 0) {
                                    trainErrors++;
                                }

                            } else { // 1

                                target[0] = 1;
                                if (output[0] <= 0) {
                                    trainErrors++;
                                }

                            }

                            network.train(input, target, 0.05);

                        }

                        for (int i = 0; i < nTest; i++) {

                        }

                        double trainingError = (double) trainErrors / (double) nTrain;
                        double testingError = (double) testErrors / (double) nTest;
                        int epochs = trainingSeries.getItemCount();
                        trainingSeries.add(epochs, trainingError);
                        testingSeries.add(epochs, testingError);
                        break;
                    default:
                        break;
                }

            }
        };

        errorFrame.addKeyListener(keyListener);
        errorFrame.pack();
        errorFrame.setVisible(true);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(accepted);
        dataset.addSeries(rejected);

        XYSeries sine = new XYSeries("Sine");
        double y;
        for (double x = -2; x <= 2; x += 0.2) {
            y = Math.sin(x * Math.PI);
            sine.add(x, y);
        }
        dataset.addSeries(sine);

        JFreeChart chart = ChartFactory.createScatterPlot(null, null, null,
                dataset);
        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        chart.setAntiAlias(true);
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);
        chart.getXYPlot().getRenderer().setSeriesPaint(2, Color.BLACK);
        ChartFrame frame = new ChartFrame("2D Network", chart);
        frame.addKeyListener(keyListener);
        frame.pack();
        frame.setVisible(true);

    }

    public static void interactiveNetwork() {

        DoubleFunction<Double> f = (double x) -> Math.tanh(x);
        DoubleFunction<Double> fD = (double x) -> 1 - Math.pow(Math.tanh(x), 2);

        Network network = Network.getRandom(f, fD, 2, 2, 1);
        network.setBiasInput(-1);
        network.setTrainBias(true);

        double[][][] w = network.getWeights();
        XYSeries[][][] wSeries = new XYSeries[w.length][][];

        System.out.print("[ ");
        for (int l = 0; l < w.length; l++) {

            System.out.print("[ ");
            wSeries[l] = new XYSeries[w[l].length][];
            for (int j = 0; j < w[l].length; j++) {

                System.out.print("[ ");
                wSeries[l][j] = new XYSeries[w[l][j].length];
                for (int i = 0; i < w[l][j].length; i++) {
                    System.out.print(w[l][j][i] + " ");
                    wSeries[l][j][i] = new XYSeries("w" + (i + 1));
                }
                System.out.print("] ");

            }
            System.out.print("] ");

        }
        System.out.println("]");

        XYSeries accepted = new XYSeries("Accepted");
        XYSeries rejected = new XYSeries("Rejected");

        KeyListener keyListener = new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

                    System.exit(0);

                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    boolean pass = true;
                    for (int a = -1; a < 2; a += 2) {

                        for (int b = -1; b < 2; b += 2) {

                            double[] x = {a, b};
                            double[] y = network.recall(x);
                            double[] ideal = {(a + b == 0 ? 1 : -1)};
                            double result = y[0] > 0 ? 1 : -1;

                            if (result != ideal[0]) {

                                pass = false;
                                network.train(x, ideal, 0.1);

                            }

                        }

                    }

                    if (pass) {
                        System.err.println("Network training completed");
                        return;
                    }

                    accepted.clear();
                    rejected.clear();

                    for (double y = -1; y <= 1; y += 0.1) {
                        for (double x = -1; x <= 1; x += 0.1) {

                            double[] input = {x, y};
                            double[] output = network.recall(input);

                            if (output[0] > 0.0) {
                                accepted.add(x, y);
                            } else {
                                rejected.add(x, y);
                            }

                        }
                    }

                    record(attempts++, network.getWeights(), wSeries);
                }

            }
        };

        for (int l = 0; l < wSeries.length; l++) {
            for (int j = 0; j < wSeries[l].length; j++) {

                XYSeriesCollection dataset = new XYSeriesCollection();

                for (int i = 0; i < wSeries[l][j].length; i++) {
                    dataset.addSeries(wSeries[l][j][i]);
                }

                JFreeChart chart = ChartFactory.createScatterPlot(null, null,
                        null, dataset);
                chart.getXYPlot().setRenderer(new XYSplineRenderer());
                chart.setAntiAlias(true);
                chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
                chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);
                ChartFrame frame = new ChartFrame(
                        "Layer: " + (l + 1) + ", Neuron: " + (j + 1), chart);
                frame.addKeyListener(keyListener);
                frame.pack();
                frame.setVisible(true);

            }
        }

        System.out.println(w[1][0][2]);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(accepted);
        dataset.addSeries(rejected);

        record(attempts++, network.getWeights(), wSeries);

        JFreeChart chart = ChartFactory.createScatterPlot(null, null, null,
                dataset);
        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        chart.setAntiAlias(true);
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);
        ChartFrame frame = new ChartFrame("2D Network", chart);
        frame.addKeyListener(keyListener);
        frame.pack();
        frame.setVisible(true);

    }

    private static String arrayToString(double[] array) {

        StringBuilder sb = new StringBuilder();
        sb.append("[ " + array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", " + array[i]);
        }
        sb.append(" ]");
        return sb.toString();

    }

    public static void backpropagationExampleTest() {

        double[][][] w = {{{.15, .2, .35}, {.25, .3, .35}},
        {{.4, .45, .6}, {.5, .55, .6}}};
        double[] input = {.05, .1};
        double[] ideal = {.01, .99};

        DoubleFunction<Double> f = (double x) -> 1 / (1 + Math.exp(-x));
        DoubleFunction<Double> fD = (double x) -> f.apply(x) * (1 - f.apply(x));

        Network network = new Network(f, fD, w, -1, true);

        network.train(input, ideal, 0.5);

    }

    public static void main(String[] args) {

        new ArtificialNeuralNetworkApp();

    }

    private static void printDoubleArray(double[] array) {

        System.out.print("[ " + array[0]);

        for (int index = 1; index < array.length; index++) {
            System.out.print(", " + array[index]);
        }

        System.out.println(" ]");

    }

    public static void show2DNetwork(Network network) {

        XYSeries accepted = new XYSeries("Accepted");
        XYSeries rejected = new XYSeries("Rejected");

        for (double y = -1; y <= 1; y += 0.025) {
            for (double x = -1; x <= 1; x += 0.025) {

                double[] input = {x, y};
                double[] output = network.recall(input);

                if (output[0] > 0.0) {
                    accepted.add(x, y);
                } else {
                    rejected.add(x, y);
                }

            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(accepted);
        dataset.addSeries(rejected);

        JFreeChart chart = ChartFactory.createScatterPlot(null, null, null,
                dataset);
        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        chart.setAntiAlias(true);
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);
        ChartFrame frame = new ChartFrame("2D Network", chart);
        frame.pack();
        frame.setVisible(true);

    }

    public static void record(double x, double[][][] w,
            XYSeries[][][] wSeries) {

        for (int l = 0; l < w.length; l++) {
            for (int j = 0; j < w[l].length; j++) {
                for (int i = 0; i < w[l][j].length; i++) {
                    wSeries[l][j][i].add(x, w[l][j][i]);
                }
            }
        }

    }

    public static void trainingAlgorithmExample() {

        DoubleFunction<Double> f = (double x) -> Math.tanh(x);
        DoubleFunction<Double> fD = (double x) -> 1 - Math.pow(Math.tanh(x), 2);

        Network network = Network.getRandom(f, fD, 2, 2, 1);
        network.setBiasInput(-1);
        network.setTrainBias(false);

        double[][][] w = network.getWeights();
        XYSeries[][][] wSeries = new XYSeries[w.length][][];

        for (int l = 0; l < w.length; l++) {

            wSeries[l] = new XYSeries[w[l].length][];
            for (int j = 0; j < w[l].length; j++) {

                wSeries[l][j] = new XYSeries[w[l][j].length];
                for (int i = 0; i < w[l][j].length; i++) {
                    wSeries[l][j][i] = new XYSeries("w" + (i + 1));
                }

            }

        }

        System.out.println(w[1][0][2]);

        record(0, w, wSeries);

        int workouts = 0;
        int maxWorkouts = 500;

        boolean pass = false;
        while (!pass && workouts <= maxWorkouts) {

            pass = true;

            for (int a = -1; a < 2; a += 2) {

                for (int b = -1; b < 2; b += 2) {

                    double[] x = {a, b};
                    double[] y = network.recall(x);
                    double[] ideal = {(a + b == 0 ? 1 : -1)};
                    double result = y[0] > 0 ? 1 : -1;

                    if (result != ideal[0]) {

                        pass = false;
                        network.train(x, ideal, 0.1);

                        workouts++;
                        record(workouts, w, wSeries);

                    }

                }

            }

        }

        record(workouts + 1, w, wSeries);

        if (pass) {
            System.out.println(
                    "It took " + workouts + " workouts to train an XOR");
        } else {
            System.err.println("Gave up training");
        }

        for (int l = 0; l < wSeries.length; l++) {
            for (int j = 0; j < wSeries[l].length; j++) {

                XYSeriesCollection dataset = new XYSeriesCollection();

                for (int i = 0; i < wSeries[l][j].length; i++) {
                    dataset.addSeries(wSeries[l][j][i]);
                }

                JFreeChart chart = ChartFactory.createScatterPlot(null, null,
                        null, dataset);
                chart.getXYPlot().setRenderer(new XYSplineRenderer());
                chart.setAntiAlias(true);
                chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
                chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);
                ChartFrame frame = new ChartFrame(
                        "Layer: " + (l + 1) + ", Neuron: " + (j + 1), chart);
                frame.pack();
                frame.setVisible(true);

            }
        }

        System.out.println(w[1][0][2]);
        show2DNetwork(network);

    }

    public static void visualizeFunction(DoubleFunction<Double> f, double min,
            double max) {

        XYSeries w0 = new XYSeries("y");

        for (double i = min; i <= max; i += 0.01) {
            double result = f.apply(i);
            w0.add(i, result);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(w0);

        JFreeChart chart = ChartFactory.createXYLineChart(null, null, null,
                dataset, PlotOrientation.VERTICAL, true, true, false);
        ChartFrame frame = new ChartFrame("Function", chart);
        frame.pack();
        frame.setVisible(true);

    }

    public static void customNetworkTest() {

        double[][][] w = {
            {{0.023128565894638936, 0.26438585799789094,
                0.40578767173669794},
            {-0.40215594432345825, 0.8512585283052834,
                0.40578767173669794}},
            {{-0.5421590355340142, 0.0112696760952758,
                0.20173599310633158}}};

        DoubleFunction<Double> f = (double x) -> Math.tanh(x);
        DoubleFunction<Double> fD = (double x) -> 1 - Math.pow(Math.tanh(x), 2);

        Network network = new Network(f, fD, w, -1, true);
        show2DNetwork(network);

    }

    public ArtificialNeuralNetworkApp() {

        // trainingAlgorithmExample();
        // backpropagationExampleTest();
        // customNetworkTest();
        // interactiveNetwork();
        findFunction();

    }

}
