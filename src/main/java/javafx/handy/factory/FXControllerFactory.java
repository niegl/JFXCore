package javafx.handy.factory;

import javafx.handy.annotation.FXController;
import javafx.handy.annotation.FXData;
import javafx.handy.annotation.FXWindow;
import javafx.handy.starer.FXNotifyController;
import javafx.handy.starer.FXWindowParser;
import javafx.handy.loader.FXMLLoaderExt;
import javafx.handy.log.ILogger;
import javafx.handy.log.LoggerFactory;
import javafx.handy.proxy.FXProxyCreator;
import javafx.handy.stage.StageManager;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author jack
 * @version 1.0
 * @date 2019/6/25 8:12
 * @since JavaFX2.0 JDK1.8
 */
public class FXControllerFactory {
    private static final ILogger logger = LoggerFactory.getLogger(FXControllerFactory.class);
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
    private static <T extends FXNotifyController> T getFxBaseController0(Class<T> clazz, String controllerName, BeanBuilder beanBuilder) {

        FXController fxController = null;
        T bean;
        T proxy = null;

        fxController = clazz.getDeclaredAnnotation(FXController.class);
        if (fxController == null) return null;

        FXMLLoaderExt fxmlLoader;
        logger.info("loading the FXML file of " + clazz.getName());
        String fxmlPathName = fxController.path();
        fxmlLoader = new FXMLLoaderExt(clazz.getResource(fxmlPathName));

        bean = beanBuilder.getBean(clazz); //获取controller实例
        if (bean == null) {
            return null;
        }

        if (controllerName == null || controllerName.isEmpty()) controllerName = clazz.getName();
        bean.setName(controllerName);

        FXProxyCreator<T> proxyCreator = new FXProxyCreator<>();
        proxy = proxyCreator.getInstance(bean);
        //产生代理从而实现赋能
        fxmlLoader.setController(proxy);

        try {
            if (proxy != null) {
                proxy.onLoad(); //页面加载
                fxmlLoader.load();
                proxy.setRoot(fxmlLoader.getRoot());
                StageManager.registerStage(proxy);
            }
        } catch (IOException e) {
            logger.error("FXMLLoader load failed! ");
            e.printStackTrace();
            return null;
        }

        return proxy;
    }

    /**
     * @param fxWindow
     * @param clazz
     * @return
     * @Description 为有FXWindow注解的类创建Stage
     */
    private static Stage createWindow(FXWindow fxWindow, Class<FXNotifyController> clazz, FXNotifyController proxy) {
        if (fxWindow == null) {
            logger.error("creating window  without FXWindow Annotation:" + clazz.getName());
            return null;
        }

//        logger.info("creating window.....");
        Stage stage = new Stage();
        proxy.setStage(stage);

        Pane root = proxy.getRoot();
        double preWidth = fxWindow.preWidth() == 0 ? root.getPrefWidth() : fxWindow.preWidth();
        double preHeight = fxWindow.preHeight() == 0 ? root.getPrefHeight() : fxWindow.preHeight();
        Scene scene = new Scene(root, preWidth, preHeight);

        stage.setScene(scene);
        fxWindowAnnotationParser.parse(stage, proxy, fxWindow);

        // 此处设置生命周期中的onShow,onHide,onClose
        proxy.initLifeCycle();

        if (fxWindow.mainStage()) {  //当是主舞台时，先show为敬
            proxy.showStage();
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
    public static void loadStage(Class<FXNotifyController> clazz, BeanBuilder beanBuilder) {
        FXWindow windowAnnotation = clazz.getDeclaredAnnotation(FXWindow.class);
        FXController controllerAnnotation = clazz.getDeclaredAnnotation(FXController.class);

        if (controllerAnnotation == null || windowAnnotation == null) {
            logger.error("FXWindow Annotation without FXController or FXWindow:" + clazz.getName());
            return;
        }

        FXNotifyController proxy = getFXController(clazz, beanBuilder);
        if (proxy != null) {
            createWindow(windowAnnotation, clazz, proxy);
        }
    }

    /**
     * 加载FXXML并赋代理
     * @param clazz
     * @param beanBuilder
     * @return 代理对象
     */
    private static FXNotifyController getFXController(Class<FXNotifyController> clazz, BeanBuilder beanBuilder) {
        return getFxBaseController0(clazz, null, beanBuilder);
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

    private static boolean isFXWindow(Class<?> clazz) {
        if (clazz.getDeclaredAnnotation(FXWindow.class) != null) {
            return true;
        } else {
            return false;
        }
    }
}
