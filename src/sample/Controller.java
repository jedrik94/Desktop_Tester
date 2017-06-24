package sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Controller {

    private String pathToFileToBeAnalyzedTestU01 = "";
    private String pathToFileToBeAnalyzedNistDieHard = "";

    private boolean isAnalyzing = false;

    @FXML
    private CheckBox testNist = new CheckBox();

    @FXML
    private CheckBox testDieHard = new CheckBox();

    @FXML
    private CheckBox testU01 = new CheckBox();

    @FXML
    private TextField pathTestU01 = new TextField();

    @FXML
    private TextField pathTestNistDieHard = new TextField();

    @FXML
    private FileChooser fileChooserTestU01 = new FileChooseConfigurer().configureFileChooser();

    @FXML
    private Button fileChooserButtonTestU01 = new Button();

    @FXML
    private Button fileChooserButtonNistDieHard = new Button();

    @FXML
    private FileChooser fileChooserNistDieHard = new FileChooseConfigurer().configureFileChooser();

    @FXML
    private Button startAnalyze = new Button();

    @FXML
    private TextField sequenceLengthNist = new TextField();

    @FXML
    private TextField elapsedTimeAfterAnalyzeStartTextField = new TextField();

    @FXML
    protected void handleActionOnStartAnalyzeButton(ActionEvent event) {
        String path = System.getProperty("user.dir");
        NanoTimer timer = new NanoTimer();


        List<String> filesPathsList = new ArrayList<>();
        filesPathsList.addAll(Arrays.asList(pathToFileToBeAnalyzedNistDieHard, pathToFileToBeAnalyzedTestU01));

        List<CheckBox> selectorsList = new ArrayList<>();
        selectorsList.addAll(Arrays.asList(testU01, testNist, testDieHard));

        List<Node> disableElementsList = new ArrayList<>();
        disableElementsList.addAll(Arrays.asList(startAnalyze, testDieHard, testNist, testU01, fileChooserButtonNistDieHard, fileChooserButtonTestU01));

        Disabler elementsDisabler = new Disabler(disableElementsList);

        if (selectorsList.stream().anyMatch(CheckBox::isSelected) && filesPathsList.stream().anyMatch(s -> !s.isEmpty())) {

            isAnalyzing = true;
            elementsDisabler.setDisable(true);

            List<Process> processesList = new ArrayList<>(3);

            try {
                if (testNist.isSelected() && !pathToFileToBeAnalyzedNistDieHard.isEmpty()) {


                    processesList.add(new ProcessBuilder(path + "\\assess.exe", sequenceLengthNist.getText().isEmpty() ? "10000" : sequenceLengthNist.getText().replaceAll("[^0-9]", ""), pathToFileToBeAnalyzedNistDieHard)
                            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                            .redirectError(ProcessBuilder.Redirect.INHERIT).start());
                }

                if (testDieHard.isSelected() && !pathToFileToBeAnalyzedNistDieHard.isEmpty()) {
                    processesList.add(new ProcessBuilder(path + "\\diehard.exe", pathToFileToBeAnalyzedNistDieHard)
                            .redirectOutput(new File(path + "\\results_diehard.log"))
                            .redirectError(ProcessBuilder.Redirect.INHERIT).start());
                }

                if (testU01.isSelected() && !pathToFileToBeAnalyzedTestU01.isEmpty()) {
                    processesList.add(new ProcessBuilder("D:\\Programs\\Git\\bin\\bash.exe", "-c", LinuxPathConverter.getPath(path, true) + "testU01bat.exe " + LinuxPathConverter.getPath(pathToFileToBeAnalyzedTestU01, false))
                            .redirectOutput(new File(path + "\\results_testU01.log"))
                            .redirectError(new File(path + "\\error_testU01.log")).start());
                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                Checker processesChecker = new Checker(processesList);
                processesChecker.start();

                new Thread(new Task<Void>() {
                    @Override
                    public Void call() {
                        while (isAnalyzing) {
                            if (processesChecker.isAlive()) {

                            } else {
                                elapsedTimeAfterAnalyzeStartTextField.setText(timer.getElapsedTimeSec() + "sec");

                                isAnalyzing = false;
                                elementsDisabler.setDisable(false);
                            }
                        }
                        return null;
                    }
                }).start();
            }
        }

    }

    @FXML
    protected void handleActionOnFileChoosingButton(ActionEvent event) {
        Node source = (Node) event.getSource();
        Window stage = source.getScene().getWindow();

        if (!isAnalyzing) {
            if (event.getSource() == fileChooserButtonNistDieHard) {
                File file = fileChooserNistDieHard.showOpenDialog(stage);
                if (file != null) {
                    pathToFileToBeAnalyzedNistDieHard = file.getAbsolutePath();
                }
                pathTestNistDieHard.setText(pathToFileToBeAnalyzedNistDieHard);
            } else if (event.getSource() == fileChooserButtonTestU01) {
                File file = fileChooserTestU01.showOpenDialog(stage);
                if (file != null) {
                    pathToFileToBeAnalyzedTestU01 = file.getAbsolutePath();
                }
                pathTestU01.setText(pathToFileToBeAnalyzedTestU01);
            }
        } else {
            Notifications notificationBuilder = Notifications.create()
                    .title("Info")
                    .text("Analysis in progress...")
                    .graphic(null)
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.TOP_RIGHT);
            notificationBuilder.showWarning();
        }
    }

    @FXML
    protected void handleMouseOverSequenceField(ActionEvent event) {
        if (event.getSource() == sequenceLengthNist) {
            Node textField = (Node) event.getSource();
            Tooltip.install(textField, new Tooltip("Gets ONLY numbers!"));
        }
    }
}
