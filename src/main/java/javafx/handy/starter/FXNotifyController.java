package javafx.handy.starter;

import javafx.handy.annotation.FXWindow;
import javafx.handy.context.GUIState;
import javafx.handy.exception.ProtocolNotSupport;
import javafx.handy.utils.FileUtil;
import javafx.handy.utils.StringUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;


/*
 * In JavaFX-Plus Framework Controller
 * We use MVC model
 * V means view which stand for fxml
 * C means controller which stand for FXBaseController instance
 * M means model  which is base cn.edu.scau.biubiusuisui.entity in your program
 * Every BaseController has a name which is used for identifying different  <strong>instance</strong>
 */

/**
 * 窗体事件消息通知的基类。如果这个类的方法需要通过Cglib代理，那么方法不能设置成final方法。
 */
@Slf4j
public abstract class FXNotifyController {

    protected String name = "";
    private Stage stage;
    private boolean isWindow = false;

    private Parent root = null;
    /**
     * <p>description 用于携带信息数据</p>
     *
     * @version 1.2
     */
    private Map<String, Object> query = new HashMap<>();
    private Map<String, Object> param = new HashMap<>();

    public FXNotifyController(String name) {
        this.name = name;
    }

    public FXNotifyController() {
        Annotation[] annotations = getClass().getAnnotations();
        // Find FXController cn.edu.scau.biubiusuisui.annotation
        for (Annotation annotation : annotations) {
            // 添加赋予是否为窗口的逻辑
            if (annotation.annotationType().equals(FXWindow.class)) {
                this.isWindow = true;
            }
        }
    }

    /**
     * @description 相当于onReady, 页面渲染完后的操作
     * @version 1.2
     */
    public void initialize() {
    }

    /**
     * @description 初始化onShow, onHide, onClose的生命周期
     * @version 1.2
     */
    public final void initLifeCycle() {

        this.stage.setOnShowing(event -> {
            try {
                onShow();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        });
        this.stage.setOnHidden(windowEvent -> {
            stage.close();
        });

        this.stage.setOnCloseRequest(event -> {
            try {
                onClose();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        });

        // 当窗体为当前工作窗体时
        this.stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (t1) {
                    GUIState.setStage(stage);
                }
            }
        });

        // resize 事件监听
        this.stage.heightProperty().addListener((observableValue, number, t1) -> {
            onResize(stage.getWidth(), stage.getWidth(), number, t1);
        });
        this.stage.widthProperty().addListener((observableValue, number, t1) -> {
            onResize(number, t1, stage.getHeight(), stage.getHeight());
        });
        // 监听最小化窗口
        this.stage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue) { //最小化
                    onHide();
                } else {
                    onShow(); //取消最小化
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        });

    }

    /**
     * @description 在fxmlloader.load()之后的操作
     * @version 1.2
     */
    public void onLoad() {}

    /**
     * @description 在显示页面之前的操作
     * @version 1.2
     */
    public void onShow() {}

    /**
     * @description 在关闭窗口之前的操作
     * @version 1.2
     */
    public void onClose() {}

    /**
     * @description 在隐藏窗口之前的操作
     * @version 1.2
     */
    public void onHide() {}

    public void onResize(Number oldWidth, Number newWidth, Number oldHeight, Number newHeight) {}
    /**
     * 唤起舞台
     */
    public void showStage() {
        if (this.isWindow) {
            this.stage.show();
        }
    }

    /**
     * 显示并等待
     */
    public void showAndWait() {
        if (this.isWindow) {
            this.stage.showAndWait();
        }
    }

    /**
     * 关闭舞台
     */
    public void closeStage() {
        if (this.isWindow) {
            this.stage.close();
        }
    }

    /**
     * @description 最小化
     * @version 1.2
     */
    public void minimizeStage() {
        if (this.isWindow) {
            this.stage.setIconified(true);
        }
    }

    public void hideStage() {
        if (this.isWindow) {
            this.stage.hide();
        }
    }

    /**
     * <p>description:  开放设置窗口标题 </p>
     *
     * @param title 标题
     * @return true--修改标题成功 false--修改失败
     * @version 1.3
     */
    public void setWindowTitle(String title) {
        if (this.isWindow) {
            this.stage.setTitle(title);
            log.info("setting title of window");
        } else {
            log.warn("the controller is not window");
        }
    }

    /**
     * <p>description: 开放设置窗口图标</p>
     *
     * @param icon String 图标URL地址，需要放在resources文件下或项目根目录下
     */
    public void setIcon(String icon) {
        if (this.isWindow) {
            if (!"".equals(icon)) {
                try {
                    InputStream iconUrl = new FileUtil().getFilePathFromResources(icon);
                    if (iconUrl != null) {
                        this.stage.getIcons().clear();
                        this.stage.getIcons().add(new Image(iconUrl));
                    } else {
                        log.warn("the icon file has not existed");
                    }
                } catch (ProtocolNotSupport exception) {
                    log.error(exception.getMessage(), exception);
                }
            }
        }
    }

    public void setModality(Modality modality) {
        this.stage.initModality(modality);
    }

    public void setOwner(Stage parentStage) {
        this.stage.initOwner(parentStage);
    }
    /**
     * 设置窗体是否能够改变大小
     * @param bResizable
     * @return 返回原窗体属性
     */
    public boolean setResizable(boolean bResizable) {
        boolean bOld = this.stage.isResizable();
        this.stage.setResizable(bResizable);
        return bOld;
    }

    @SuppressWarnings("unchecked")
    public <T> T getRoot() {
        return (T) root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    public void maximize(boolean fullScreen) { stage.setMaximized(fullScreen);}

    /**
     * 获取Controller名字
     *
     * @return name
     */
    public String getName() {
        if ("".equals(name) || name == null) { // 原本无“name == null”判断条件，会出错
            return StringUtil.getBaseClassName(getClass().getName());
        } else {
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWindow() {
        return this.isWindow;
    }

    public void setWindow(boolean window) {
        this.isWindow = window;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
