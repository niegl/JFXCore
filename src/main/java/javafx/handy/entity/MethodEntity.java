package javafx.handy.entity;

import java.lang.reflect.Method;

/**
 * This class is base cn.edu.scau.biubiusuisui.entity for queue message(or signal)
 * you mush save the instance and method which means who will run this method
 */
public class MethodEntity<T> {
    /**
     * 所属Controller
     */
    private T receiver;
    /**
     * 实际方法
     */
    private Method method;

    public MethodEntity(T fxBaseController, Method method) {
        this.receiver = fxBaseController;
        this.method = method;
    }

    public T getReceiver() {
        return receiver;
    }

    public void setReceiver(T receiver) {
        this.receiver = receiver;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

}
