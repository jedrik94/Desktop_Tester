package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class Controller {

    private String pathToFileToBeAnalyzed;

    @FXML
    private TextField actionTarget = new TextField();

    @FXML
    private FileChooser fileChooser = new FileChooseConfigurer().configureFileChooser();

    @FXML
    protected void handleActionOnFileChoosingButton(ActionEvent event) {
        Node source = (Node) event.getSource();
        Window stage = source.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            pathToFileToBeAnalyzed = file.getAbsolutePath();
        }

        actionTarget.setText(pathToFileToBeAnalyzed);
    }
}
