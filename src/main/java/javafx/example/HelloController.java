package javafx.example;

import javafx.handy.annotation.FXController;
import javafx.handy.annotation.FXRedirect;
import javafx.handy.annotation.FXWindow;
import javafx.handy.starer.FXNotifyController;
import javafx.handy.entity.FXRedirectParam;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

@FXController(path = "/hello.fxml")
@FXWindow(title = "我的第一个JAVAFX 插件",preWidth = 400, preHeight = 300, mainStage = true)
public class HelloController extends FXNotifyController {
    @FXML
    private Label welcomeText;

    @FXML
    @FXRedirect(close = false, hasOwner = true)
    protected FXRedirectParam onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        return new FXRedirectParam("javafx.example.SecondController");
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onShow() {
        System.out.println("onShow();");
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void onHide() {
        System.out.println("onHide();");
    }

    @Override
    public void onResize(Number oldWidth, Number newWidth, Number oldHeight, Number newHeight) {
        System.out.println(oldWidth + "," +newWidth+ "," +oldHeight+ "," +newHeight);
    }
}