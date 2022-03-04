package flow.jfxcore.context;

import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public enum GUIState {
    INSTANCE;

    private static Stage stage;

    private GUIState() {
    }

    public static Stage getStage() {
        return stage;
    }

    synchronized public static void setStage(Stage stage) {
        GUIState.stage = stage;
    }
}