package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Controller {

    private String pathToFileToBeAnalyzed = "";
    private boolean isAnalyzing = false;

    @FXML
    private CheckBox testNist = new CheckBox();

    @FXML
    private CheckBox testDieHard = new CheckBox();

    @FXML
    private CheckBox testU01 = new CheckBox();

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
        if (!pathToFileToBeAnalyzed.isEmpty()) {

            String path = System.getProperty("user.dir");
            NanoTimer timer = new NanoTimer();
            Checker processesChecker = null;

            {
                isAnalyzing = true;
                startAnalyze.setDisable(true);
                testNist.setDisable(true);
                testDieHard.setDisable(true);
                testU01.setDisable(true);
            }

            int exitValue = 0;
            Map<String, Process> processesList = new HashMap<>(3);

            try {
                if (testNist.isSelected()) {

                    processesList.put("Nist", new ProcessBuilder(path + "\\assess.exe", "1000", pathToFileToBeAnalyzed)
//                    processesList.put("Nist", new ProcessBuilder("D:\\sts-2.1.2\\sts-2.1.2\\assess.exe", "1000", pathToFileToBeAnalyzed)
                            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                            .redirectError(ProcessBuilder.Redirect.INHERIT).start());
                    /*Process processNist = processesList.get("Nist");

                    exitValue = processNist.waitFor();
                    System.out.println("\nExit value of process: " + exitValue);*/
                }

                if (testDieHard.isSelected()) {
                    processesList.put("DieHard", new ProcessBuilder(path + "\\diehard.exe", pathToFileToBeAnalyzed)
//                    processesList.put("DieHard", new ProcessBuilder("D:\\diehard-master\\diehard.exe", pathToFileToBeAnalyzed)
                            .redirectOutput(new File(path + "\\results_diehard.log"))
                            .redirectError(ProcessBuilder.Redirect.INHERIT).start());
                    /*Process processDieHard = processesList.get("DieHard");

                    exitValue = processDieHard.waitFor();
                    System.out.println("\nExit value of process: " + exitValue);*/
                }

                if (testU01.isSelected()) {
                    String pathTemp = "/" + Character.toLowerCase(path.charAt(0)) + path.substring(1).replace(":", "").replace("\\", "/") + "/";
                    String pathToFileToBeAnalyzedTemp = "/" + Character.toLowerCase(pathToFileToBeAnalyzed.charAt(0)) + pathToFileToBeAnalyzed.substring(1).replace(":", "").replace("\\", "/");

                    System.out.println(pathToFileToBeAnalyzedTemp);
                    System.out.println(pathTemp);
                    processesList.put("U01", new ProcessBuilder("D:\\Programs\\Git\\bin\\bash.exe", "-c", pathTemp + "testU01bat.exe " + pathToFileToBeAnalyzedTemp)
                            .redirectOutput(new File(path + "\\results_testU01.log"))
                            .redirectError(new File(path + "\\error_testU01.log")).start());
                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                processesChecker = new Checker(processesList);
                processesChecker.start();

                while (isAnalyzing) {
                    if (processesChecker.isAlive()) {

                        System.out.println(processesList.get("U01").isAlive() + "");
                    } else {
                        elapsedTimeAfterAnalyzeStartTextField.setText(timer.getElapsedTimeSec() + "sec");

                        isAnalyzing = false;
                        startAnalyze.setDisable(false);
                        testNist.setDisable(false);
                        testU01.setDisable(false);
                        testDieHard.setDisable(false);
                    }
                }
            }
        }
    }

    @FXML
    protected void handleActionOnFileChoosingButton(ActionEvent event) {
        Node source = (Node) event.getSource();
        Window stage = source.getScene().getWindow();

        if (!isAnalyzing) {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                pathToFileToBeAnalyzed = file.getAbsolutePath();
            }

            actionTarget.setText(pathToFileToBeAnalyzed);
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


}
