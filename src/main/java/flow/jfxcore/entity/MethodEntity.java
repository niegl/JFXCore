package flow.jfxcore.entity;

import flow.jfxcore.core.FXNotifyController;

import java.lang.reflect.Method;

/**
 * This class is base cn.edu.scau.biubiusuisui.entity for queue message(or signal)
 * you mush save the instance and method which means who will run this method
 */
public class MethodEntity<T> {
    /**
     * 所属Controller
     */
    private T notifyController;
    /**
     * 实际方法
     */
    private Method method;

    public MethodEntity(T fxBaseController, Method method) {
        this.notifyController = fxBaseController;
        this.method = method;
    }

    public T getNotifyController() {
        return notifyController;
    }

    public void setNotifyController(T notifyController) {
        this.notifyController = notifyController;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

}
