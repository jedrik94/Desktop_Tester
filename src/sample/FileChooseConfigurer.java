package sample;

import javafx.stage.FileChooser;

import java.io.File;

class FileChooseConfigurer {
    private FileChooser fileChooser = new FileChooser();

    FileChooser configureFileChooser(String title, String extensionName, String extension) {
        this.fileChooser.setTitle(title);
        this.fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        this.fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(extensionName, extension)
        );
        return this.fileChooser;
    }
}
