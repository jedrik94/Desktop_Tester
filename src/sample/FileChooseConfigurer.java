package sample;

import javafx.stage.FileChooser;

import java.io.File;

class FileChooseConfigurer {
    private FileChooser fileChooser = new FileChooser();

    FileChooser configureFileChooser() {
        this.fileChooser.setTitle("Choose file to analyze");
        this.fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        this.fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("BINARY", "*")
        );
        return this.fileChooser;
    };
}
