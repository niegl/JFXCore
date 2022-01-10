package flow.jfxcore.core;

import flow.jfxcore.annotation.FXWindow;
import flow.jfxcore.log.IPlusLogger;
import flow.jfxcore.log.PlusLoggerFactory;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 */
public class FXWindowParser {
    private static final IPlusLogger logger = PlusLoggerFactory.getLogger(FXWindowParser.class);

    public void parse(Stage stage, FXNotifyController fxControllerProxy, FXWindow fxWindow) {
        logger.info("parsing @FXWindow of class: " + fxControllerProxy.getName());

        // 处理 title
        fxControllerProxy.setWindowTitle(fxWindow.title());

        // 处理 icon
        fxControllerProxy.setIcon(fxWindow.icon());

        // 处理模态对话框设置
        fxControllerProxy.setModality(fxWindow.modality());

        // 处理窗体填充颜色
        Scene scene = stage.getScene();
        scene.setFill(Paint.valueOf(fxWindow.fill()));

        // 处理style
        stage.initStyle(fxWindow.style());
    }
}
