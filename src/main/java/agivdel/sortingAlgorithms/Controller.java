package agivdel.sortingAlgorithms;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;

public class Controller extends View{
    @FXML private ComboBox<String> algorithmsComboBox;
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
    private void task_start(ActionEvent actionEvent) {
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
        arrayLengthSlider.setDisable(true);//слайдер неактивен до окончания задачи
        algorithmsComboBox.setDisable(true);//список неактивен до окончания задачи
    }

    @FXML
    private void cancelTask() throws InterruptedException {
        if (task != null) {
            task.cancel();
            task = null;
            pauseButton.setSelected(false);//если метод был вызван при нажатой кнопке паузы, после отмены задачи делаем кнопку паузы отжатой
            paneShow.getChildren().clear();//не всегда очищает панель
            //успевает ли панель очиститься за паузу или нет, зависит от размера массива (для 100 хватало 10мс, для 1000 - даже 80мс может не хватить)
            //может, причина в другом?
            Thread.sleep(200);//видимо, иногда после очистки панели проскакивает цикл работы/отрисовки по алгоритму
            paneShow.getChildren().clear();//повторное очищение через паузу срабатывает всегда
            arrayLengthSlider.setDisable(false);//слайдер неактивен до окончания задачи - дубль!
            algorithmsComboBox.setDisable(false);//список неактивен до окончания задачи - дубль!
        }
    }

    private Task<Void> createTask() {
        return new Task<Void>() {//здесь начинается новый поток Thread
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
                    if (isCancelled()) {
                        updateMessage("задача прервана");
                        break;
                    }
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
                        if (isCancelled()) {//
                            updateMessage("задача прервана");
                            break;
                        }
                    }
                }
            }

            @Override
            protected void succeeded() {
                updateMessage("задача завершена");
                arrayLengthSlider.setDisable(false);//слайдер неактивен до окончания задачи - дубль!
                algorithmsComboBox.setDisable(false);//список неактивен до окончания задачи - дубль!
            }
        };
    }
}
