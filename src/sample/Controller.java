package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;

public class Controller {

    private String pathToFileToBeAnalyzed;

    @FXML
    private ListView testsList = new ListView();

    @FXML
    private CheckBox testNist = new CheckBox();

    @FXML
    private CheckBox testDieHard = new CheckBox();

    @FXML
    private CheckBox testU01 = new CheckBox();

    @FXML
    private CheckBox nistTestFreq = new CheckBox();

    @FXML
    private CheckBox nistTestFreqBlock = new CheckBox();

    @FXML
    private TextField actionTarget = new TextField();

    @FXML
    private FileChooser fileChooser = new FileChooseConfigurer().configureFileChooser();

    @FXML
    private Button startAnalyze = new Button();

    @FXML
    private TextField elapsedTimeAfterAnalyzeStartTextField = new TextField();

    @FXML
    protected void handleActionOnStartAnalyzeButton(ActionEvent event) {
        if (pathToFileToBeAnalyzed != null) {
            try {
                int exitValue = 0;
                Process process = null;

                String path = System.getProperty("user.dir");

                if (testNist.isSelected()) {
                    process = new ProcessBuilder(path + "\\assess.exe", "1000", pathToFileToBeAnalyzed)
                            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                            .redirectError(ProcessBuilder.Redirect.INHERIT)
                            .start();

                    exitValue = process.waitFor();
                    System.out.println("\nExit value of process: " + exitValue);
                }

                if (testDieHard.isSelected()) {
                    process = new ProcessBuilder(path + "\\diehard.exe", pathToFileToBeAnalyzed)
                            .redirectOutput(new File(path + "\\results_diehard.txt"))
                            .redirectError(ProcessBuilder.Redirect.INHERIT)
                            .start();

                    exitValue = process.waitFor();
                    System.out.println("\nExit value of process: " + exitValue);
                }

                elapsedTimeAfterAnalyzeStartTextField.setText("" + exitValue);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

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
