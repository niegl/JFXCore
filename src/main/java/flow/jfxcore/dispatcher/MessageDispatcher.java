package flow.jfxcore.dispatcher;

import flow.jfxcore.annotation.FXReceiver;
import flow.jfxcore.core.FXNotifyController;
import flow.jfxcore.entity.MethodEntity;
import flow.jfxcore.log.IPlusLogger;
import flow.jfxcore.log.PlusLoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息注册、消息分发处理类。
 */
public class MessageDispatcher {
    private static final IPlusLogger logger = PlusLoggerFactory.getLogger(MessageDispatcher.class);

    /**
     * Map<主题，订阅了主题的所有方法>
     */
    private static final Map<String, List<MethodEntity<FXNotifyController>>> receivers = new ConcurrentHashMap<>();

    private static MessageDispatcher messageQueue = null;

    private MessageDispatcher() {
    }

    /**
     * 获取mq单例
     *
     * @return MessageQueue
     */
    public static synchronized MessageDispatcher getInstance() {
        if (messageQueue == null) {
            messageQueue = new MessageDispatcher();
        }
        return messageQueue;
    }

    /**
     * @param notifyController  基础controller
     * @param controllerProxy 基础controller代理
     * @description 注册消费者，即FXReceiver注解的method
     */
    public void registerConsumer(FXNotifyController notifyController, FXNotifyController controllerProxy) {
        Class clazz = notifyController.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (FXReceiver.class.equals(annotation.annotationType())) {
                    logger.info("registering consumer: " + controllerProxy.getName());
                    FXReceiver consumer = (FXReceiver) annotation;
                    MethodEntity<FXNotifyController> fxMethodEntity = new MethodEntity<>(controllerProxy, method);
                    List<MethodEntity<FXNotifyController>> fxMethodEntities = receivers.get(consumer.name());
                    if (fxMethodEntities == null) {
                        fxMethodEntities = new ArrayList<>();
                    }
                    fxMethodEntities.add(fxMethodEntity);
                    receivers.put(consumer.name(), fxMethodEntities);
                }
            }
        }
    }

    /**
     * @param topic  消息topic
     * @param msg 消息内容
     * @description 处理消息发送
     */
    public void sendMessage(String topic, Object msg) {
        List<MethodEntity<FXNotifyController>> lists = receivers.get(topic);
        if (lists == null || lists.isEmpty()) return;

        for (var fxMethodEntity : lists) {
            Method method = fxMethodEntity.getMethod();
            try {
                method.setAccessible(true);
                FXNotifyController notifyController = fxMethodEntity.getNotifyController();
                if (method.getParameterCount() == 0) {
                    method.invoke(notifyController);
                } else {
                    // 调起FXReceiver注解的方法
                    method.invoke(notifyController, msg);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
