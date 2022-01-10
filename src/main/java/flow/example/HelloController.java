package flow.example;

import flow.jfxcore.annotation.FXController;
import flow.jfxcore.annotation.FXWindow;
import flow.jfxcore.core.FXNotifyController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

@FXController(path = "/flow/jfxcore/hello-view.fxml")
@FXWindow(title = "我的第一个JAVAFX 插件",preWidth = 400, preHeight = 300, icon = "/icon/flow.png")
public class HelloController extends FXNotifyController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
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
}