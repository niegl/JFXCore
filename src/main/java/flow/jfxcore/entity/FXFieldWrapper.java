package flow.jfxcore.entity;

import flow.jfxcore.annotation.FXField;
import javafx.beans.property.Property;

/**
 * 将Controller中的JavaFX的field包装成FXFieldWrapper
 *
 * @author jack
 * @version 1.0
 * @date 2019/6/28 10:03
 * @since JavaFX2.0 JDK1.8
 */
public class FXFieldWrapper<T> {

    private FXField fxField;
    private Class<T> type;

    private Property<T> property;

    public FXFieldWrapper() {
    }

    public FXFieldWrapper(FXField fxField, Class<T> type) {
        this.fxField = fxField;
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public FXField getFxField() {
        return fxField;
    }

    public void setFxField(FXField fxField) {
        this.fxField = fxField;
    }

    public Property<T> getProperty() {
        return property;
    }

    public void setProperty(Property<T> property) {
        this.property = property;
    }

}
