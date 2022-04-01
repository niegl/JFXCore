package flow.jfxcore.factory;

import flow.jfxcore.log.IPlusLogger;
import flow.jfxcore.log.PlusLoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jack
 * @version 1.0
 * @date 2019/7/4 11:13
 * @since JavaFX2.0 JDK1.8
 */
public class FXBuilder implements BeanBuilder {
    private final IPlusLogger logger = PlusLoggerFactory.getLogger(FXBuilder.class);

    @Override
    public <T> T getBean(Class<T> type) {
        T object = null;
        try {
            object = type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return object;
    }
}
