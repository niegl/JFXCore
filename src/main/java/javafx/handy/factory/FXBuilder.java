package javafx.handy.factory;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jack
 * @version 1.0
 * @date 2019/7/4 11:13
 * @since JavaFX2.0 JDK1.8
 */
@Slf4j
public class FXBuilder implements BeanBuilder {

    @Override
    public <T> T getBean(Class<T> type) {
        T object = null;
        try {
            object = type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return object;
    }
}
