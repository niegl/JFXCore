package flow.jfxcore.proxy;

import flow.jfxcore.annotation.FXRedirect;
import flow.jfxcore.annotation.FXSender;
import flow.jfxcore.core.FXNotifyController;
import flow.jfxcore.dispatcher.MessageDispatcher;
import flow.jfxcore.stage.StageManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 负责拦截FXSender或FXRedirect的方法，并将结果发送给目标对象.
 * This proxy class intercept Methods that has special annotation such as
 * FXSender which is a mark for message queue
 */
public class FXControllerProxy<T extends FXNotifyController> implements MethodInterceptor {

    T target;

    @SuppressWarnings("unchecked")
    public T getInstance(T target) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);
        Object proxy = enhancer.create();
        // target.* -> proxy.*
        inject(target, proxy);
        return (T) proxy;
    }

    /**
     * 运行FXSender的方法，并将结果发送给 FXSender指定的对象。
     * @param o 为由CGLib动态生成的代理类实例
     * @param method Method为上文中实体类所调用的被代理的方法引用
     * @param objects Object[]为参数值列表
     * @param methodProxy MethodProxy为生成的代理类对方法的代理引用
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object o1 = methodProxy.invokeSuper(o, objects);  //获取该方法运行后的结果
        Annotation[] annotations = method.getDeclaredAnnotations();

        for (Annotation annotation : annotations) {
            if (FXSender.class.equals(annotation.annotationType())) {  // 拦截是否发送消息函数
                FXSender fxSender = (FXSender) annotation;
                String name = target.getName() + ":";
                if ("".equals(fxSender.name())) {
                    name += method.getName();
                } else {
                    name += fxSender.name();
                }
                MessageDispatcher.getInstance().sendMessage(name, o1);
            }
            if (FXRedirect.class.equals((annotation.annotationType()))) {  //拦截是否重定向函数
                FXRedirect fxRedirect = (FXRedirect) annotation;
                if (fxRedirect.close()) {  //关闭原窗口
                    StageManager.getInstance().closeStage(target.getName());
                }
                StageManager.getInstance().redirectTo(o1);
            }
        }
        return o1;
    }

    /**
     * 代理和非代理对象之间的属性值同步: 将被代理对象的属性值同步设置给代理对象。
     * @param target 被代理对象
     * @param proxy 代理对象
     */
    private void inject(Object target, Object proxy) {
        Class clazz = target.getClass();
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
