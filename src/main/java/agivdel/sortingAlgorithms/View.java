package agivdel.sortingAlgorithms;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class View {
    @FXML
    public Pane paneShow;
    @FXML
    public Label arrayLengthLabel;
    @FXML
    public Slider arrayLengthSlider;
    @FXML
    public Label progressLabel;
    @FXML
    public ProgressBar progressBar;

    static Stage stage;
    static Scene scene;
    static Algorithms alg;
    static int arrayLimit = 100;

    private static int factor = 10;
    private static double stroke_width = 8.0;

    void setStage(Stage stage) {
        View.stage = stage;
        scene = stage.getScene();
    }

    void setArrayLength() {
        arrayLengthSlider.valueProperty().addListener((obj, oldValue, newValue) -> {
            if ((double) newValue == 1 || (double) newValue == 2 || (double) newValue == 3) {
                arrayLimit = (int) Math.pow(10, (Double) newValue);
                arrayLengthLabel.setText(String.valueOf(arrayLimit));
                markAxisX();
            }
        });
    }

    void markAxisX() {
        factor = (int)paneShow.getWidth() / arrayLimit;
        stroke_width = factor * 0.8;
    }


    /**
     * отрисовка столбцов на главной панели paneShow.
     * Проблемы при 1000 длине массива:
     * 1) при задержке 1 мс поток "зависает";
     * 2) очистка панели не успевает выполниться за время задержки между итерациями
     * (видно, что начало массива уже отрисовывается вновь, а в конце массива мелькает "провал" очистки)
     * 3) привязка линий к окну, а надо - к панели!
     */
    class DrawShape implements Runnable {
        int k;
        double arrayValue;

        @Override
        public void run() {
            int x = k * factor + factor / 2;
            double zeroY = scene.getHeight() - 40;
            Line line = new Line(x, zeroY, x, zeroY - arrayValue);
            line.setStroke(Color.web(Constants.LINE_COLOR));
            line.setStrokeWidth(stroke_width);
            paneShow.getChildren().add(line);
        }

        Runnable param(int k, double arrayValue) {
            this.k = k;
            this.arrayValue = arrayValue;
            return this;
        }
    }
}
