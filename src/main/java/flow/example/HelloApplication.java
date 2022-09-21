package flow.example;

import flow.jfxcore.annotation.FXScan;
import flow.jfxcore.core.FXStarter;
import flow.jfxcore.stage.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

@FXScan(base = {"flow.example"})
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
        FXStarter.start(HelloApplication.class);
        StageManager.showStage("HelloController#flow.example.HelloController");
    }

    public static void main(String[] args) {
        launch();
    }
}