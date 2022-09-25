import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


public class Main extends Application {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final HashMap<Integer, Double> CHART = new HashMap<>();
    private static int k = 40;
    private static final HashMap<String, Integer> FLOWER_TYPES = new HashMap<>();
    private static ArrayList<Flower> testArray;
    private static ArrayList<Flower> trainArray;

    public static void main(String[] args) {
        testArray = parse("data/test-set.csv");
        trainArray = parse("data/train-set.csv");
        System.out.println("Accuracy for K = " + k + ": " + matchTestSet(k, false, testArray, trainArray) + "\n\n");
        for (int i = 1; i <= trainArray.size(); i += 1) {
            k = i;
            double accuracy = matchTestSet(k, false, testArray, trainArray);
            CHART.put(i, accuracy);
            System.out.println("k = " + i + " : " + accuracy);
        }
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setAnimated(false);
        yAxis.setLabel("Accuracy");
        xAxis.setLabel("K");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(1.2);
        yAxis.setTickUnit(0.2);

        XYChart.Series series = new XYChart.Series();
        series.setName("Accuracy change with higher K");


        for (Map.Entry<Integer, Double> entry : CHART.entrySet()) {
            boolean add = series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }


        lineChart.getData().add(series);
        Button button = new Button("add new flower");
        button.setMaxWidth(500);
        button.setMaxHeight(200);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                addNewFlower();
                stage.close();
                stage.show();
            }
        });
        VBox box = new VBox(lineChart, button);
        GridPane gridPane = new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(800));
        gridPane.add(lineChart, 0, 0);
        gridPane.add(button, 0, 1);
        button.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gridPane, 800, 300);
        stage.setScene(scene);
        stage.show();
    }
    public static void addNewFlower() {
        while (true) {
            System.out.println("Enter: x1,x2,x3,x4,k or 'exit': ");
            String line = SCANNER.nextLine();
            if (line.equals("exit"))
                break;


            String[] attrsString = line.split(",");
            double[] attributes = new double[attrsString.length - 1];

            try {
                for (int i = 0; i < attributes.length; i++)
                    attributes[i] = Double.parseDouble(attrsString[i].trim());
                k = (Integer.parseInt(attrsString[attrsString.length - 1].trim()));
            } catch (Exception exception) {
                System.out.println("Entered data is not valid!");
                System.out.println("=====================================");
                addNewFlower();
                break;
            }

            ArrayList<Flower> observations = new ArrayList<>();
            observations.add(new Flower(null, attributes));

            testArray = observations;
            matchTestSet(k, true, testArray, trainArray);


        }
    }

        public static double getDistance(Flower o1, Flower o2) {
            double distance = 0;
            Flower flower;
            //we want to operate on a flower with smaller length
            if (o1.getValues().length > o2.getValues().length) flower = o2;
            else flower = o1;
            for (int i = 0; i < flower.getValues().length; i++)
                distance += Math.abs(Math.pow(o1.getValues()[i] - o2.getValues()[i], 2));

            return Math.sqrt(distance);
        }

    private static String matchTrainSet(int k, ArrayList<Flower> trainArrayList) {
        if (k > trainArrayList.size())
            k = (trainArrayList.size());

        for (Map.Entry<String, Integer> entry : FLOWER_TYPES.entrySet())
            entry.setValue(0);

        for (int i = 0; i < k; i++)
            for (Map.Entry<String, Integer> entry : FLOWER_TYPES.entrySet())
                if (trainArrayList.get(i).getNAME().equals(entry.getKey()))
                    entry.setValue(entry.getValue() + 1);

        return Collections.max(FLOWER_TYPES.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    public static double matchTestSet(int k, boolean addNewFlower, ArrayList<Flower> testArrayList, ArrayList<Flower> trainArrayList) {
        double total = 0;
        double found = 0;

        for (Flower testFlower : testArrayList) {
            for (Flower trainFlower : trainArrayList)
                trainFlower.setDistance(getDistance(testFlower, trainFlower));

            Collections.sort(trainArrayList);
            String name = matchTrainSet(k, trainArrayList);

            if (name.equals(testFlower.getNAME())) found++;
            if (addNewFlower)
                System.out.println("Expected name: " + testFlower.getNAME() + " " + Arrays.toString(testFlower.getValues()) + " -> Actual name: " + name);
            total++;
        }
        return found / total;
    }
    private static ArrayList<Flower> parse(String path) {

        ArrayList<Flower> flowerArrayList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while (null != (line = reader.readLine())) {
                String[] strings = line.split(",");
                double[] values = new double[strings.length - 1];
                for (int i = 0; i < values.length; i++)
                    values[i] = Double.parseDouble(strings[i].trim());
                FLOWER_TYPES.put(strings[strings.length - 1], 0);
                flowerArrayList.add(new Flower(strings[strings.length - 1], values));
            }
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
        //System.out.println(FLOWER_TYPES.toString());
        return flowerArrayList;

    }


}