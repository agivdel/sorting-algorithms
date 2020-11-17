package agivdel.sortingAlgorithms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * selection sort,
 * insertion sort
 */

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(new Image("/images/alg_sort.png"));
        stage.setTitle("Sorting algorithms2");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, Constants.WIDTH, Constants.HEIGHT);
        stage.setScene(scene);
        stage.setOpacity(0.98);
        scene.getStylesheets().add(getClass().getResource("/darkTheme.css").toExternalForm());
        stage.show();

        View view = loader.getController();
        view.setStage(stage);
    }
}
