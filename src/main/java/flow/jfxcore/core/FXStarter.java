package flow.jfxcore.core;

import flow.jfxcore.annotation.FXScan;
import flow.jfxcore.annotation.FXWindow;
import flow.jfxcore.factory.BeanBuilder;
import flow.jfxcore.factory.FXBuilder;
import flow.jfxcore.factory.FXControllerFactory;
import flow.jfxcore.log.ILogger;
import flow.jfxcore.log.LoggerFactory;
import flow.jfxcore.utils.ClassUtil;
import flow.jfxcore.utils.FileUtil;
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

    public static void start(Class clazz) {
        start(clazz, DEFAULT_BEAN_FACTORY);
    }

    private static void start(Class clazz, BeanBuilder beanBuilder) {
        logger.info("FXStarter begin...");
        try {
            logger.info("\n" + FileUtil.readFileFromResources("banner.txt"));
        } catch (UnsupportedEncodingException e) {
            logger.error("\n read classpath:banner.txt error, you can ignore it");
        }

        Annotation[] annotations = clazz.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (!FXScan.class.equals(annotation.annotationType())) {
                continue;
            }
            String[] dirs = ((FXScan) annotation).base();
            for (String dir : dirs) {
                ClassUtil classUtil = new ClassUtil();
                List<String> temps = classUtil.scanAllClassName(dir);
                temps.forEach( className -> {
                    logger.info("loading class: " + className);
                    Platform.runLater(() -> {
                        try {
                            loadFXClass(className, beanBuilder);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
        }
    }

    /**
     * 加载指定包下面的class类
     * @param className 类名
     * @param beanBuilder 类对象生产者
     * @throws ClassNotFoundException
     */
    private static void loadFXClass(String className, BeanBuilder beanBuilder) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        // 是窗口，需要初始化Stage
        if (FXNotifyController.class.isAssignableFrom(clazz)) {
            logger.info("loading stage of class: " + className);
            FXControllerFactory.loadStage((Class<FXNotifyController>) clazz, beanBuilder);
        }
    }
}
