package javafx.example;

import javafx.handy.starter.FXStarter;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

//@FXScan(base = {"flow.example"})
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXStarter.start(HelloApplication.class);
    }

    public static void main(String[] args) {
        launch();
    }
}