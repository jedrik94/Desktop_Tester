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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller {

    private String pathToFileToBeAnalyzedTestU01 = "";
    private String pathToFileToBeAnalyzedNistDieHard = "";
    private String pathToBashExe = "";

    private boolean isAnalyzing = false;

    private static final String path = System.getProperty("user.dir");

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
    private FileChooser fileChooserTestU01 = new FileChooseConfigurer().configureFileChooser("Choose file to analyze", "TXT float values [0,1)", "*");

    @FXML
    private Button fileChooserButtonTestU01 = new Button();

    @FXML
    private Button fileChooserButtonNistDieHard = new Button();

    @FXML
    private FileChooser fileChooserNistDieHard = new FileChooseConfigurer().configureFileChooser("Choose file to analyze", "BINARY", "*");

    @FXML
    private FileChooser destinyChooserZipFileSave = new FileChooseConfigurer().configureFileChooser("Destiny of zip file", "ZIP", "*.zip");

    @FXML
    private FileChooser bashChooserPath = new FileChooseConfigurer().configureFileChooser("Executable bash.exe", "EXE", "*.exe");

    @FXML
    private Button bashChooserButton = new Button();

    @FXML
    private Button startAnalyze = new Button();

    @FXML
    private Button zipAllFiles = new Button();

    @FXML
    private Button zipNistFiles = new Button();

    @FXML
    private Button zipTestU01Files = new Button();

    @FXML
    private Button zipDieHardFiles = new Button();

    @FXML
    private TextField pathBash = new TextField();

    @FXML
    private TextField sequenceLengthNist = new TextField();

    @FXML
    private TextField elapsedTimeAfterAnalyzeStartTextField = new TextField();

    @FXML
    private VBox downloadsPart = new VBox();

    @FXML
    protected void handleActionOnStartAnalyzeButton(ActionEvent event) {
        NanoTimer timer = new NanoTimer();

        List<String> filesPathsList = new ArrayList<>();
        filesPathsList.addAll(Arrays.asList(pathToFileToBeAnalyzedNistDieHard, pathToFileToBeAnalyzedTestU01));

        List<CheckBox> selectorsList = new ArrayList<>();
        selectorsList.addAll(Arrays.asList(testU01, testNist, testDieHard));

        List<Node> disableElementsList = new ArrayList<>();
        disableElementsList.addAll(Arrays.asList(startAnalyze, testDieHard, testNist, testU01, sequenceLengthNist, bashChooserButton));

        List<Node> disableZipButtonsList = new ArrayList<>();
        disableZipButtonsList.addAll(Arrays.asList(zipAllFiles, zipDieHardFiles, zipNistFiles, zipTestU01Files));

        HashMap<CheckBox, Button> testsSelectorsZipButtonsMap = new HashMap<>(3);
        HashMap<CheckBox, String> testsSelectorsFilesPathsMap = new HashMap<>(3);

        testsSelectorsFilesPathsMap.put(testU01, pathToFileToBeAnalyzedTestU01);
        testsSelectorsFilesPathsMap.put(testDieHard, pathToFileToBeAnalyzedNistDieHard);
        testsSelectorsFilesPathsMap.put(testNist, pathToFileToBeAnalyzedNistDieHard);

        testsSelectorsZipButtonsMap.put(testU01, zipTestU01Files);
        testsSelectorsZipButtonsMap.put(testDieHard, zipDieHardFiles);
        testsSelectorsZipButtonsMap.put(testNist, zipNistFiles);

        Disabler elementsDisabler = new Disabler(disableElementsList);
        Disabler zipButtonsDisabler = new Disabler(disableZipButtonsList);

        if (selectorsList.stream().anyMatch(CheckBox::isSelected) && filesPathsList.stream().anyMatch(s -> !s.isEmpty())) {

            isAnalyzing = true;
            elementsDisabler.setDisable(true);
            zipButtonsDisabler.setDisable(true);

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
                    processesList.add(new ProcessBuilder(pathToBashExe, "-c", LinuxPathConverter.getPath(path, true) + "testU01bat.exe " + LinuxPathConverter.getPath(pathToFileToBeAnalyzedTestU01, false))
                            .redirectOutput(new File(path + "\\results_testU01.log"))
                            .redirectError(new File(path + "\\error_testU01.log")).start());
                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                Checker processesChecker = new Checker(processesList);
                processesChecker.start();

                SelectedChecker selectedChecker = new SelectedChecker(testsSelectorsZipButtonsMap, testsSelectorsFilesPathsMap, zipAllFiles);

                new Thread(new Task<Void>() {
                    @Override
                    public Void call() {
                        while (isAnalyzing) {
                            if (processesChecker.isAlive()) {

                            } else {
                                elapsedTimeAfterAnalyzeStartTextField.setText(timer.getElapsedTimeSec() + "sec");

                                elementsDisabler.setDisable(false);
                                selectedChecker.check();
                                isAnalyzing = false;

                                downloadsPart.setVisible(true);
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
                    .darkStyle()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.TOP_RIGHT);
            notificationBuilder.showWarning();
        }
    }

    @FXML
    protected void handleActionOnBashPathChooser(ActionEvent event) {
        Node source = (Node) event.getSource();
        Window stage = source.getScene().getWindow();

        if (!isAnalyzing) {
                File file = bashChooserPath.showOpenDialog(stage);
                if (file != null) {
                    pathToBashExe = file.getAbsolutePath();
                }
                pathBash.setText(pathToBashExe);
        }
    }

    @FXML
    protected void handleMouseOverSequenceField(ActionEvent event) {
        if (event.getSource() == sequenceLengthNist) {
            Node textField = (Node) event.getSource();
            Tooltip.install(textField, new Tooltip("Gets ONLY numbers!"));
        }
    }

    @FXML
    protected void handleActionOnZipButtons(ActionEvent event) {
        Node source = (Node) event.getSource();
        Window stage = source.getScene().getWindow();

        net.lingala.zip4j.core.ZipFile zipFile = null;

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(new Date());

        try {
            if (event.getSource() == zipAllFiles) {
                zipFile = new net.lingala.zip4j.core.ZipFile(path + "\\" + dateString + "_allFiles.zip");

                zipFile.addFile(new File(path + "\\results_diehard.log"), zipParameters);
                zipFile.addFile(new File(path + "\\results_testU01.log"), zipParameters);
                zipFile.addFile(new File(path + "\\error_testU01.log"), zipParameters);

                zipFile.addFolder(path + "\\experiments", zipParameters);
            } else if (event.getSource() == zipDieHardFiles) {
                zipFile = new net.lingala.zip4j.core.ZipFile(path + "\\" + dateString + "_dieHardFiles.zip");

                zipFile.addFile(new File(path + "\\results_diehard.log"), zipParameters);
            } else if (event.getSource() == zipNistFiles) {
                zipFile = new net.lingala.zip4j.core.ZipFile(path + "\\" + dateString + "_nistFiles.zip");

                zipFile.addFolder(path + "\\experiments", zipParameters);
            } else if (event.getSource() == zipTestU01Files) {
                zipFile = new net.lingala.zip4j.core.ZipFile(path + "\\" + dateString + "_testU01Files.zip");

                zipFile.addFile(new File(path + "\\results_testU01.log"), zipParameters);
                zipFile.addFile(new File(path + "\\error_testU01.log"), zipParameters);
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } finally {
            destinyChooserZipFileSave.setInitialFileName("");
            File dest = destinyChooserZipFileSave.showSaveDialog(stage);
            if (dest != null && zipFile != null) {
                try {
                    Files.copy(zipFile.getFile().toPath(), dest.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zipFile != null) {
                try {
                    Files.delete(zipFile.getFile().toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
