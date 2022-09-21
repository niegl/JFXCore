package flow.example;

import flow.jfxcore.annotation.FXController;
import flow.jfxcore.annotation.FXWindow;
import flow.jfxcore.core.FXNotifyController;

@FXController(path = "/second.fxml")
@FXWindow(title = "我的第一个JAVAFX 插件",preWidth = 400, preHeight = 300)
public class SecondController extends FXNotifyController {

}
