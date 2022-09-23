package javafx.handy.proxy;

import javafx.handy.annotation.FXRedirect;
import javafx.handy.annotation.FXSender;
import javafx.handy.starter.FXNotifyController;
import javafx.handy.dispatcher.MessageDispatcher;
import javafx.handy.stage.StageManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 负责拦截FXSender或FXRedirect的方法，并将结果发送给目标对象.<p>
 * This proxy class intercept Methods that has special annotation such as
 * FXSender which is a mark for message queue
 */
public class FXProxyCreator<T> implements MethodInterceptor {

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
     Params:
     * @param obj "this", the enhanced object
     * @param method intercepted Method
     * @param args argument array; primitive types are wrapped
     * @param proxy used to invoke super (non-intercepted method); may be called
     * as many times as needed
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = proxy.invokeSuper(obj, args);  //获取该方法运行后的结果
        Annotation[] annotations = method.getDeclaredAnnotations();

        // 如果没有给任何的 消息发送或跳转参数，则返回
        if (result == null) {
            return null;
        }

        for (Annotation annotation : annotations) {
            if (FXSender.class.equals(annotation.annotationType())) {  // 拦截是否发送消息函数
                FXSender fxSender = (FXSender) annotation;
                String queue = fxSender.name();
                if (queue.isEmpty()) {
                    queue = method.getName();
                }
                MessageDispatcher.sendMessageSync(queue, result);
            } else if (FXRedirect.class.equals((annotation.annotationType()))) {  //拦截是否重定向函数
                FXRedirect fxRedirect = (FXRedirect) annotation;
                //关闭原窗口
                if (fxRedirect.close()) {
                    StageManager.closeStage(target);
                }

                if (fxRedirect.hasOwner()) {
                    FXNotifyController proxyOwner = StageManager.getStage(fxRedirect.owner());
                    if (proxyOwner != null) {
                        StageManager.redirectTo(result, proxyOwner);
                    } else {
                        if (obj instanceof FXNotifyController controller) {
                            StageManager.redirectTo(result, controller);
                        }
                    }
                } else {
                    StageManager.redirectTo(result, null);
                }
            }
        }
        return result;
    }

    /**
     * 代理和非代理对象之间的属性值同步: 将被代理对象的属性值同步设置给代理对象。
     * @param target 被代理对象
     * @param proxy 代理对象
     */
    public static void inject(@NotNull Object target, @NotNull Object proxy) {

        Class<?> clazz = target.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //如：private、static、final等
                int fieldModifiers = field.getModifiers();
                //与某个具体的修饰符进行比较
                if (Modifier.isFinal(fieldModifiers)) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    Object value = field.get(target);
                    field.set(proxy, value);
                } catch (InaccessibleObjectException | IllegalArgumentException|IllegalAccessException|SecurityException e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }

    }
}
