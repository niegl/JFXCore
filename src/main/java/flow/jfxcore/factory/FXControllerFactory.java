package flow.jfxcore.factory;

import flow.jfxcore.annotation.FXController;
import flow.jfxcore.annotation.FXData;
import flow.jfxcore.annotation.FXWindow;
import flow.jfxcore.core.FXNotifyController;
import flow.jfxcore.core.FXPlusContext;
import flow.jfxcore.core.FXWindowParser;
import flow.jfxcore.dispatcher.MessageDispatcher;
import flow.jfxcore.fxextend.FXMLLoaderExt;
import flow.jfxcore.log.IPlusLogger;
import flow.jfxcore.log.PlusLoggerFactory;
import flow.jfxcore.proxy.FXControllerProxy;
import flow.jfxcore.stage.StageManager;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author jack
 * @version 1.0
 * @date 2019/6/25 8:12
 * @since JavaFX2.0 JDK1.8
 */
public class FXControllerFactory {
    private static IPlusLogger logger = PlusLoggerFactory.getLogger(FXControllerFactory.class);

    private static final BeanBuilder BEAN_BUILDER = new FXBuilder();
    private static final FXWindowParser fxWindowAnnotationParser = new FXWindowParser();


    /**
     * 控制类的注入流程
     * 这是一个即将被创建的控制类
     * <pre>
     *   <code>
     *    class MainController{
     *          @Autowired
     *          Student stu; //普通属性
     *          @FXML
     *          Button btn; //FX属性
     *    }
     *   </code>
     *   </pre>
     * 1. 实现对普通属性的注入
     * 如果使用了Spring那么这类会自动注入那些@Autowired的属性，请不要将这个方法用在控制器属性中
     * <pre>
     *   <code>
     *    class MainController{
     *          @Autowired
     *          Student stu ;  //初始化完成
     *          @FXML
     *          Button btn;  // null
     *    }
     *   </code>
     *   </pre>
     * 2. 通过loadFXML实现对FX属性的注入
     * <pre>
     *   <code>
     *    class MainController{
     *          @Autowired
     *          Student stu ;  //初始化完成
     *          @FXML
     *          Button btn;  // 初始化完成
     *    }
     *   </code>
     *   </pre>
     * <p>
     * 3.  完成对FXBind的注解的解析
     * <p>
     * <p>
     * <p>
     * 4.  完成注册
     *
     * @param clazz          instance that extends by FXBaseController
     * @param controllerName
     * @return
     */
    private static FXNotifyController getFxBaseController(Class clazz, String controllerName, BeanBuilder beanBuilder) {
        return getFxBaseController0(clazz, controllerName, beanBuilder);
    }

    private static FXNotifyController getFxBaseController0(Class clazz, String controllerName, BeanBuilder beanBuilder) {

        FXController fxController = null;        //reflect and get FXController cn.edu.scau.biubiusuisui.annotation
        fxController = (FXController) clazz.getDeclaredAnnotation(FXController.class);
        FXNotifyController fxBaseController = null;
        FXNotifyController fxControllerProxy = null;

        if (fxController == null) return null;

        FXMLLoaderExt fxmlLoader;
        logger.info("loading the FXML file of " + clazz.getName());
        String fxmlPathName = fxController.path();
        fxmlLoader = new FXMLLoaderExt(clazz.getResource(fxmlPathName));

        fxBaseController = (FXNotifyController) beanBuilder.getBean(clazz); //获取controller实例
        if (fxBaseController != null) {
            if (controllerName == null || controllerName.isEmpty()) controllerName = clazz.getName();
            fxBaseController.setName(controllerName);

            FXControllerProxy<FXNotifyController> controllerProxy = new FXControllerProxy<>();
            fxControllerProxy = controllerProxy.getInstance(fxBaseController);
            //产生代理从而实现赋能
            fxmlLoader.setController(fxControllerProxy);
        }

        if (fxControllerProxy != null) {
            fxControllerProxy.onLoad(); //页面加载
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                logger.info("FXMLLoader get Resource failed! ");
                e.printStackTrace();
                return null;
            }
            fxControllerProxy.setRoot(fxmlLoader.getRoot());
            fxBaseController.setRoot(fxmlLoader.getRoot());

            register(clazz, fxBaseController, fxControllerProxy);
        }

        return fxControllerProxy;
    }


    /**
     * 将代理对象和目标对象注册
     *
     * @param fxBaseController      目标对象
     * @param fxBaseControllerProxy 代理对象
     */
    private static void register(Class clazz, FXNotifyController fxBaseController, FXNotifyController fxBaseControllerProxy) {
        FXPlusContext.registerController(fxBaseController); //保存原生
        StageManager.getInstance().registerWindow(clazz, fxBaseControllerProxy);  //注册舞台、代理
        MessageDispatcher.getInstance().registerConsumer(fxBaseController, fxBaseControllerProxy); // 添加进入消息队列 信号功能
    }

    /**
     * @param fxWindow
     * @param clazz
     * @return
     * @Description 为有FXWindow注解的类创建Stage
     */
    private static Stage createWindow(FXWindow fxWindow, Class clazz, FXNotifyController fxControllerProxy) {

        if (fxControllerProxy == null) return null;

        logger.info("creating window.....");
        Stage stage = new Stage();
        fxControllerProxy.setStage(stage);

        Pane root = fxControllerProxy.getRoot();
        double preWidth = fxWindow.preWidth() == 0 ? root.getPrefWidth() : fxWindow.preWidth();
        double preHeight = fxWindow.preHeight() == 0 ? root.getPrefHeight() : fxWindow.preHeight();
        Scene scene = new Scene(root, preWidth, preHeight);

        stage.setScene(scene);
        fxWindowAnnotationParser.parse(stage, fxControllerProxy, fxWindow);

        // 此处设置生命周期中的onShow,onHide,onClose
        fxControllerProxy.initLifeCycle();

        if (fxWindow.mainStage()) {  //当是主舞台时，先show为敬
            fxControllerProxy.showStage();
        }
        return stage;
    }

    private FXControllerFactory() {

    }

    /**
     * 加载舞台
     * 原函数名为loadMainStage(Class clazz, BeanBuilder beanBuilder)
     *
     * @param clazz
     * @param beanBuilder
     */
    public static void loadStage(Class clazz, BeanBuilder beanBuilder) {
        FXWindow declaredAnnotation = (FXWindow) clazz.getDeclaredAnnotation(FXWindow.class);
        //只有当用了FXWindow注解，才会注册Stage
        if (declaredAnnotation != null) {
            getFXWindow(clazz, null, beanBuilder);
        }
    }

    public static FXNotifyController getFXController(Class clazz) {
        return getFXController(clazz, BEAN_BUILDER);
    }

    public static FXNotifyController getFXController(Class clazz, BeanBuilder beanBuilder) {
        FXNotifyController fxBaseController = getFXController(clazz, null, beanBuilder);
        return fxBaseController;
    }

    public static FXNotifyController getFXController(Class clazz, String controllerName) {
        return getFXController(clazz, controllerName, BEAN_BUILDER);
    }

    public static FXNotifyController getFXController(Class clazz, String controllerName, BeanBuilder beanBuilder) {
        FXNotifyController fxBaseController = getFxBaseController(clazz, controllerName, beanBuilder);
        return fxBaseController;
    }


    public static Stage getFXWindow(Class clazz) {
        FXWindow fxWindow = (FXWindow) clazz.getDeclaredAnnotation(FXWindow.class);
        if (fxWindow != null) {
            FXNotifyController fxController = getFXController(clazz);
            return createWindow(fxWindow, clazz, fxController);
        } else {
            return null;
        }
    }

    public static Stage getFXWindow(Class clazz, BeanBuilder beanBuilder) {
        FXWindow fxWindow = (FXWindow) clazz.getDeclaredAnnotation(FXWindow.class);
        if (fxWindow != null) {
            FXNotifyController fxController = getFXController(clazz, beanBuilder);
            return createWindow(fxWindow, clazz, fxController);
        } else {
            return null;
        }
    }

    public static Stage getFXWindow(Class clazz, String controllerName) {
        FXWindow fxWindow = (FXWindow) clazz.getDeclaredAnnotation(FXWindow.class);
        if (fxWindow != null) {
            FXNotifyController fxController = getFXController(clazz, controllerName);
            return createWindow(fxWindow, clazz, fxController);
        } else {
            return null;
        }
    }

    public static Stage getFXWindow(Class clazz, String controllerName, BeanBuilder beanBuilder) {
        FXWindow fxWindow = (FXWindow) clazz.getDeclaredAnnotation(FXWindow.class);
        if (fxWindow != null) {
            FXNotifyController fxControllerProxy = getFXController(clazz, controllerName);
            Stage stage = createWindow(fxWindow, clazz, fxControllerProxy);
            return stage;
        } else {
            return null;
        }
    }

    /**
     * 代理对象与被代理对象数据同步
     * @param fxControllerProxy 代理对象
     */
    private static void sync(FXNotifyController fxControllerProxy) {
        String name = fxControllerProxy.getName();
        if (name.isEmpty()) {
            return;
        }

        List<FXNotifyController> controllerList = FXPlusContext.getControllers(name);
        if (controllerList != null) {
            for (FXNotifyController controller :
                    controllerList) {
                FXControllerProxy.inject(fxControllerProxy,controller);
            }
        }

    }

    private static void parseData(Object fxControllerObject) {
        Class clazz = fxControllerObject.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            FXData annotation = field.getAnnotation(FXData.class);
            if (annotation != null) {
                field.setAccessible(true);
                //建立代理
                try {
                    Object fieldValue = field.get(fxControllerObject);
                    Object fieldValueProxy = FXEntityFactory.wrapFXBean(fieldValue);
                    field.set(fxControllerObject, fieldValueProxy);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private static void addDataInNameSpace(ObservableMap namespace, Object object) {
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            FXData annotation = field.getAnnotation(FXData.class);
            if (annotation != null) {
                field.setAccessible(true);
                try {
                    String fx_id;
                    field.setAccessible(true);
                    if ("".equals(annotation.fx_id())) {
                        fx_id = field.getName();
                    } else {
                        fx_id = annotation.fx_id();
                    }
                    Object fieldValue = field.get(object);
                    namespace.put(fx_id, fieldValue);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private static void scanBind(ObservableMap namespace, Object object) {
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            parseBind(namespace, object, field);
        }
    }

    private static void parseBind(ObservableMap namespace, Object object, Field field) {
//        FXBind fxBind = field.getAnnotation(FXBind.class);
//        field.setAccessible(true);
//        ExpressionParser expressionParser = new ExpressionParser(namespace, object);
//        if (fxBind != null) {
//            String[] expressions = fxBind.value();
//            try {
//                Object objectValue = field.get(object);
//                for (String e : expressions) {
//                    expressionParser.parse(objectValue, e);
//                }
//            } catch (IllegalAccessException e) {
//                logger.error(e.getMessage());
//                e.printStackTrace();
//            } catch (NoSuchChangeMethod noSuchChangeMethod) {
//                logger.error(noSuchChangeMethod.getMessage());
//                noSuchChangeMethod.printStackTrace();
//            }
//        }
    }

    private static boolean isFXWindow(Class clazz) {
        if (clazz.getDeclaredAnnotation(FXWindow.class) != null) {
            return true;
        } else {
            return false;
        }
    }
}
