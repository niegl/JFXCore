package javafx.handy.starter;

import javafx.handy.annotation.FXWindow;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FXWindowParser {

    public void parse(Stage stage, FXNotifyController controllerProxy, FXWindow fxWindow) {
//        logger.info("parsing @FXWindow of class: " + controllerProxy.getName());

        // 处理 title
        controllerProxy.setWindowTitle(fxWindow.title());

        // 处理 icon
        controllerProxy.setIcon(fxWindow.icon());

        // 处理模态对话框设置
        controllerProxy.setModality(fxWindow.modality());
        // 是否能够改变大小
        controllerProxy.setResizable(fxWindow.resizable());

        if (fxWindow.maximize()) {
            controllerProxy.maximize(true);
        }
        // 处理窗体填充颜色
        Scene scene = stage.getScene();
        scene.setFill(Paint.valueOf(fxWindow.fill()));

        // 处理style
        stage.initStyle(fxWindow.style());
    }
}
