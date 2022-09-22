package javafx.handy.starer;

import javafx.handy.annotation.FXScan;
import javafx.handy.annotation.FXWindow;
import javafx.handy.factory.BeanBuilder;
import javafx.handy.factory.FXBuilder;
import javafx.handy.factory.FXControllerFactory;
import javafx.handy.log.ILogger;
import javafx.handy.log.LoggerFactory;
import javafx.handy.utils.ClassUtil;
import javafx.handy.utils.FileUtil;
import javafx.application.Platform;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * JFX启动器
 */
public class FXStarter {
    private static final ILogger logger = LoggerFactory.getLogger(FXStarter.class);
    private static final BeanBuilder DEFAULT_BEAN_FACTORY = new FXBuilder();

    public static void start(Class<?> clazz) {
        start(clazz, DEFAULT_BEAN_FACTORY);
    }

    private static void start(Class<?> clazz, BeanBuilder beanBuilder) {
        logger.info("FXStarter begin...");

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

        logger.info("FXStarter end...");
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
                logger.info("loading stage of class: " + className);
                FXControllerFactory.loadStage((Class<FXNotifyController>) clazz, beanBuilder);
            }
        } catch (ClassNotFoundException e) {
            logger.error("loading stage exception, class: " + className);
        }

    }
}
