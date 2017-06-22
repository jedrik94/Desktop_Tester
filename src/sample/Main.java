package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.getIcons().add(new Image("/sample/icon.png"));
        primaryStage.setTitle("Tester");
        primaryStage.setMaxHeight(770);
        primaryStage.setMaxWidth(1380);
        primaryStage.setScene(new Scene(root, 1360, 768));
        primaryStage.getScene().getStylesheets().addAll(this.getClass().getResource("styles.css").toExternalForm());

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
