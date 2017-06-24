package sample;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Komputer on 6/24/2017.
 */
public class Disabler {
    private List<Node> elementsList;

    public Disabler(List<Node> elementsList) {
        this.elementsList = elementsList;
    }

    public void setDisable(boolean state) {
        for (Node node : elementsList) {
            node.setDisable(state);
        }
    }
}
