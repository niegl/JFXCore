package flow.jfxcore.core;

import flow.jfxcore.annotation.FXWindow;
import flow.jfxcore.exception.ProtocolNotSupport;
import flow.jfxcore.log.IPlusLogger;
import flow.jfxcore.log.PlusLoggerFactory;
import flow.jfxcore.utils.FileUtil;
import flow.jfxcore.utils.StringUtil;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
public abstract class FXNotifyController {
    private static final IPlusLogger logger = PlusLoggerFactory.getLogger(FXNotifyController.class);

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
        logger.info("init the life cycle of " + this.getName());
        this.stage.setOnShowing(event -> {
            try {
                onShow();
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        });
        this.stage.setOnCloseRequest(event -> {
            try {
                onClose();
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
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
                logger.error(e.getMessage());
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
            logger.info("setting title of window");
        } else {
            logger.warn("the controller is not window");
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
                        logger.warn("the icon file has not existed");
                    }
                } catch (ProtocolNotSupport exception) {
                    logger.error(exception.getMessage(), exception);
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

    /**
     * 获取Controller名字
     *
     * @return name
     */
    public String getName() {
        if ("".equals(name) || name == null) { // 原本无“name == null”判断条件，会出错
            return StringUtil.getBaseClassName(getClass().getSimpleName());
        } else {
            return StringUtil.getBaseClassName(getClass().getSimpleName()) + "#" + name;
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
