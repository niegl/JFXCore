package flow.example;

import flow.jfxcore.annotation.FXScan;
import flow.jfxcore.core.FXNotifyController;
import flow.jfxcore.core.FXPlusBootstrap;
import flow.jfxcore.core.FXPlusContext;
import flow.jfxcore.stage.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

@FXScan(base = {"flow.example"})
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
        FXPlusBootstrap.start(HelloApplication.class);
        StageManager.getInstance().showStage("HelloController#flow.example.HelloController");
    }

    public static void main(String[] args) {
        launch();
    }
}