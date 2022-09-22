package javafx.handy.dispatcher;

import javafx.handy.annotation.FXReceiver;
import javafx.handy.entity.MethodEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息注册、消息分发处理类。
 * 消息能够发送成功的关键在于事件的发起者是是代理类，这样在方法调用时就可以进行拦截，从而实现消息的发送.
 */
public class MessageDispatcher {

    /**
     * Map<主题，订阅了主题的所有方法>
     */
    private static final Map<String, List<MethodEntity<Object>>> receivers = new ConcurrentHashMap<>();

    private MessageDispatcher() {
    }

    /**
     * @description 注册消费者，即FXReceiver注解的method
     * @param consumer  消费者
     */
    public static void registerConsumer(Object consumer) {
        Class<?> clazz = consumer.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (FXReceiver.class.equals(annotation.annotationType())) {
                    FXReceiver fxReceiver = (FXReceiver) annotation;
                    List<MethodEntity<Object>> fxMethodEntities = receivers.computeIfAbsent(fxReceiver.name(), k-> new ArrayList<>());

                    MethodEntity<Object> fxMethodEntity = new MethodEntity<>(consumer, method);
                    fxMethodEntities.add(fxMethodEntity);
                }
            }
        }
    }

    /**
     * description 处理消息发送
     * @param topic  消息topic
     * @param msg 消息内容

     */
    public static void sendMessageSync(String topic, Object... msg) {
        List<MethodEntity<Object>> entities = receivers.get(topic);
        if (entities == null || entities.isEmpty()) {
            return;
        }

        for (var entity : entities) {
            Object receiver = entity.getReceiver();
            Method method = entity.getMethod();
            try {
                if (!method.canAccess(receiver)) {
                    continue;
                }
                if (method.getParameterCount() == 0) {
                    method.invoke(receiver);
                } else {
                    // 调起FXReceiver注解的方法
                    method.invoke(receiver, msg);
                }
            } catch (IllegalAccessException | InvocationTargetException |IllegalArgumentException |NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
