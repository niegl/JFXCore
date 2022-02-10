package flow.jfxcore.core;

import flow.jfxcore.core.FXNotifyController;
import flow.jfxcore.proxy.FXEntityProxy;
import javafx.beans.property.Property;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 原生FXNotifyController上下文管理
 */
public class FXPlusContext {

    private FXPlusContext() {
    }

    /**
     * FXController控制器注册表
     */
    private static final Map<String, List<FXNotifyController>> controllerContext = new ConcurrentHashMap<>();

    private static Map<Object, FXEntityProxy> beanMap = new ConcurrentHashMap(); // Object注册为FXEntityObject


    public static void registerController(FXNotifyController fxControllerProxy) {
        List<FXNotifyController> controllers = controllerContext.get(fxControllerProxy.getName());
        if (controllers == null) {
            controllers = new LinkedList<>();
        }
        controllers.add(fxControllerProxy);
        // @since 1.2.1 fix: 没有将controller真正注册到context的异常
        controllerContext.put(fxControllerProxy.getName(), controllers);
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
