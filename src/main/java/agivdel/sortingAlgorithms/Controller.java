package agivdel.sortingAlgorithms;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;

public class Controller extends View{
    @FXML private ComboBox<Integer> delayComboBox;
    @FXML private ToggleButton pauseButton;

    private Task<Void> task;
    public static int timeDelay = Constants.MEDIUM;


    @FXML
    private void initialize() {
        setArrayLength();
        delayComboBoxInit();
        setTimeDelay();//если не разместить здесь, а оформить через @FXML private, срабатывает не каждый раз
    }

    private void delayComboBoxInit() {
        delayComboBox.getItems().addAll(Constants.SLOW, Constants.MEDIUM, Constants.FAST);
        delayComboBox.getSelectionModel().select(1);//по умолчанию в списке показан элемент под индексом 1
        delayComboBox.setConverter(new IntegerStringConverter());
    }

    private void setTimeDelay() {
        delayComboBox.valueProperty().addListener((obj, oldValue, newValue) -> {
            timeDelay = newValue;
        });
    }

    @FXML
    private void start(ActionEvent actionEvent) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }

        task = createTask();
        Thread thread = new Thread(task);
        thread.setDaemon(true);//поток-демон завершится при завершении основного потока
        thread.start();

        progressLabel.textProperty().bind(task.messageProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        pauseButton.disableProperty().bind(task.runningProperty().not());//кнопка активна до окончания задачи
        setSliderAndComboBoxDisable(true);
    }

    @FXML
    private void cancel() throws InterruptedException {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    draw();
                } catch (Exception ex) {
                    updateMessage(ex.getMessage());
                }
                return null;
            }

            private void draw() throws InterruptedException {
                alg = new Algorithms(arrayLimit, Constants.NUMBER_ORIGIN, Constants.NUMBER_BOUND);
                Double[] array = alg.getArray();

                Platform.runLater(() -> paneShow.getChildren().clear());
                updateMessage("задача запущена");
                for (int i = 0; i < array.length; i++) {
                    while (pauseButton.isSelected()) {
                        Thread.onSpinWait();
                        updateMessage("пауза");
                    }

                    updateMessage("шаг: i = " + i);
                    updateProgress(i, array.length);

                    //в зависимости от выбранной в algorithmsComboBox позиции выбираем тот или иной алгоритм
                    alg.select(algorithmsComboBox.getSelectionModel().getSelectedIndex(), i);

                    Platform.runLater(() -> paneShow.getChildren().clear());
                    for (int k = 0; k < array.length; k++) {
                        Platform.runLater(new DrawShape().param(k, array[k]));//отрисовка столбцов на paneShow
                    }

                    try {
                        Thread.sleep(timeDelay);
                    } catch (InterruptedException interrupted) {
                        if (isCancelled()) {
                            updateMessage("задача прервана");
                            Platform.runLater(() -> paneShow.getChildren().clear());
                            break;
                        }
                    }
                }
            }

            @Override
            protected void succeeded() {
                updateMessage("задача завершена");
                task = null;
                setSliderAndComboBoxDisable(false);
            }

            @Override
            protected void cancelled() {
                pauseButton.setSelected(false);
                setSliderAndComboBoxDisable(false);
            }
        };
    }
}
