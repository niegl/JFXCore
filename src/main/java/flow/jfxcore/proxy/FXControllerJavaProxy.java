package flow.jfxcore.proxy;

import flow.jfxcore.annotation.FXRedirect;
import flow.jfxcore.annotation.FXSender;
import flow.jfxcore.core.FXNotifyController;
import flow.jfxcore.dispatcher.MessageDispatcher;
import flow.jfxcore.stage.StageManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * java的代理只能代理方法，不能代理属性
 */

public class FXControllerJavaProxy<T> implements InvocationHandler {

    T target;

    @SuppressWarnings("unchecked")
    public T getProxy(T target) {
        this.target = target;
        T proxy = (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
//        inject(target, proxy);

        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object obj = method.invoke(target,args);

        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (FXSender.class.equals(annotation.annotationType())) {  // 拦截是否发送消息函数
                FXSender fxSender = (FXSender) annotation;
                String name = ((FXNotifyController)target).getName() + ":";
                if ("".equals(fxSender.name())) {
                    name += method.getName();
                } else {
                    name += fxSender.name();
                }
                MessageDispatcher.getInstance().sendMessage(name, obj);
            }
            if (FXRedirect.class.equals((annotation.annotationType()))) {  //拦截是否重定向函数
                FXRedirect fxRedirect = (FXRedirect) annotation;
                if (fxRedirect.close()) {  //关闭原窗口
                    StageManager.getInstance().closeStage(((FXNotifyController)target).getName());
                }
                StageManager.getInstance().redirectTo(obj, null);
            }
        }
        return obj;
    }

    /**
     * 代理和非代理对象之间的属性值同步: 将被代理对象的属性值同步设置给代理对象。
     * @param target 被代理对象
     * @param proxy 代理对象
     */
    private void inject(Object target, Object proxy) {
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(target);
                field.set(proxy, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
