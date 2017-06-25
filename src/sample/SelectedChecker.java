package sample;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.util.HashMap;

/**
 * Created by Komputer on 6/25/2017.
 */
public class SelectedChecker {
    private HashMap<CheckBox, Button> testsSelectorsZipButtonsMap;
    private HashMap<CheckBox, String> testsSelectorsFilesPathsMap;
    private Button allTestZipButton;

    public SelectedChecker(HashMap<CheckBox, Button> testsSelectorsZipButtonsMap, HashMap<CheckBox, String> testsSelectorsFilesPathsMap, Button allTestZipButton) {
        this.testsSelectorsZipButtonsMap = testsSelectorsZipButtonsMap;
        this.testsSelectorsFilesPathsMap = testsSelectorsFilesPathsMap;
        this.allTestZipButton = allTestZipButton;
    }

    public void check() {
        boolean allSelected = true;

        for (CheckBox checkBox : testsSelectorsZipButtonsMap.keySet()) {
            if (checkBox.isSelected() && !testsSelectorsFilesPathsMap.get(checkBox).isEmpty()) {
                    testsSelectorsZipButtonsMap.get(checkBox).setDisable(false);
                }
            allSelected &= checkBox.isSelected() && !testsSelectorsFilesPathsMap.get(checkBox).isEmpty();
        }

        allTestZipButton.setDisable(!allSelected);
    }
}
