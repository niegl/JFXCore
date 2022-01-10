package flow.jfxcore.core;

import flow.jfxcore.core.FXNotifyController;
import flow.jfxcore.proxy.FXEntityProxy;
import javafx.beans.property.Property;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context is use for storing Controller
 * In addition,you can store an instance into Session to use it everywhere
 *
 * @author jack
 * @version 1.0
 * @date 2019/6/26 12:28
 * @since JavaFX2.0 JDK1.8
 */
public class FXPlusContext {

    private FXPlusContext() {
    }

    /**
     * FXController控制器注册表
     */
    private static final Map<String, List<FXNotifyController>> controllerContext = new ConcurrentHashMap<>();

    private static Map<Object, FXEntityProxy> beanMap = new ConcurrentHashMap(); // Object注册为FXEntityObject


    public static void registerController(FXNotifyController fxBaseController) {
        List<FXNotifyController> controllers = controllerContext.get(fxBaseController.getName());
        if (controllers == null) {
            controllers = new LinkedList<>();
        }
        controllers.add(fxBaseController);
        // @since 1.2.1 fix: 没有将controller真正注册到context的异常
        controllerContext.put(fxBaseController.getName(), controllers);
    }


    public static FXEntityProxy getProxyByBeanObject(Object object) {
        return beanMap.get(object);
    }

    public static void setProxyByBeanObject(Object object, FXEntityProxy fxEntityProxy) {
        beanMap.put(object, fxEntityProxy);
    }

    /**
     * @param key = fxBaseController.getName()
     * @return
     */
    public static List<FXNotifyController> getControllers(String key) {
        return controllerContext.get(key);
    }

    public static Property getEntityPropertyByName(Object object, String fieldName) {
        return getProxyByBeanObject(object).getPropertyByFieldName(fieldName);
    }
}
