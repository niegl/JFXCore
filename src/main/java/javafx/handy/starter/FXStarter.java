package javafx.handy.starter;

import javafx.handy.annotation.FXReceiver;
import javafx.handy.annotation.FXScan;
import javafx.handy.dispatcher.MessageDispatcher;
import javafx.handy.factory.BeanBuilder;
import javafx.handy.factory.FXBuilder;
import javafx.handy.factory.FXControllerFactory;
import javafx.handy.utils.ClassUtil;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JFX启动器
 */
@Slf4j
public class FXStarter {

    private static final BeanBuilder DEFAULT_BEAN_FACTORY = new FXBuilder();

    public static void start(Class<?> clazz) {
        start(clazz, DEFAULT_BEAN_FACTORY);
    }

    private static void start(Class<?> clazz, BeanBuilder beanBuilder) {
        log.info("FXStarter begin...");

        String[] basePackage = null;
        FXScan scanAnnotation = clazz.getDeclaredAnnotation(FXScan.class);
        if (scanAnnotation != null) {
            basePackage = scanAnnotation.base();
        } else {
            basePackage = new String[]{clazz.getPackageName()};
        }

        if (basePackage == null) {
            return;
        }

        ClassUtil classUtil = new ClassUtil();
        for (String package0 : basePackage) {
            List<String> allClassName = classUtil.scanAllClassName(package0);
            if (allClassName == null) {
                continue;
            }
            allClassName.forEach( className -> Platform.runLater(() -> loadFXClass(className, beanBuilder)));
        }

        log.info("FXStarter end...");
    }
    /**
     * 加载指定包下面的class类
     * @param className 类名
     * @param beanBuilder 类对象生产者
     */
    private static void loadFXClass(String className, BeanBuilder beanBuilder) {
        try {
            Class<?> clazz = Class.forName(className);
            // 是窗口，需要初始化Stage
            if (FXNotifyController.class.isAssignableFrom(clazz)) {
                log.info("loading stage of class: " + className);
                FXControllerFactory.loadStage((Class<FXNotifyController>) clazz, beanBuilder);
            } else if (clazz.getDeclaredAnnotation(FXReceiver.class) != null) {
                MessageDispatcher.registerConsumer(clazz);
            }
        } catch (ClassNotFoundException e) {
            log.error("loading stage exception, class: " + className);
        }

    }
}
