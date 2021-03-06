package flow.jfxcore.utils;

import flow.jfxcore.core.FXPlusContext;
import flow.jfxcore.proxy.FXEntityProxy;
import javafx.beans.property.Property;

/**
 * @author jack
 * @version 1.0
 * @Date:2019/7/28 1:52
 * @Description:
 * @since JavaFX2.0 JDK1.8
 */
public class BeanUtil {
    public static Property getPropertyByName(Object entity, String fieldName) {
        FXEntityProxy fxEntityProxy = FXPlusContext.getProxyByBeanObject(entity);
        if (fxEntityProxy == null) {
            return null;
        }
        return fxEntityProxy.getFXFieldWrapperMap().get(fieldName).getProperty();
    }
}
