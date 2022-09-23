package javafx.example;

import javafx.handy.annotation.FXController;
import javafx.handy.annotation.FXWindow;
import javafx.handy.starter.FXNotifyController;

@FXController(path = "/second.fxml")
@FXWindow(title = "我的第一个JAVAFX 插件",preWidth = 400, preHeight = 300)
public class SecondController extends FXNotifyController {

}
