package flow.jfxcore.core;

import flow.jfxcore.annotation.FXScan;
import flow.jfxcore.annotation.FXWindow;
import flow.jfxcore.factory.BeanBuilder;
import flow.jfxcore.factory.FXBuilder;
import flow.jfxcore.factory.FXControllerFactory;
import flow.jfxcore.log.IPlusLogger;
import flow.jfxcore.log.PlusLoggerFactory;
import flow.jfxcore.utils.ClassUtil;
import flow.jfxcore.utils.FileUtil;
import javafx.application.Platform;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jack
 * @version 1.0
 * @date 2019/6/25 2:54
 * @since JavaFX2.0 JDK1.8
 */
public class FXPlusBootstrap {
    private static final IPlusLogger logger = PlusLoggerFactory.getLogger(FXPlusBootstrap.class);
    private static final BeanBuilder DEFAULT_BEAN_FACTORY = new FXBuilder();

    public static void start(Class clazz, BeanBuilder beanBuilder) {
        logger.info("starting JavaFX-Plus Application");
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
                            loadFXPlusClass(className, beanBuilder);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
        }
    }

    public static void start(Class clazz) {
        start(clazz, DEFAULT_BEAN_FACTORY);
    }

    private static void loadFXPlusClass(String className, BeanBuilder beanBuilder) throws ClassNotFoundException {
        Class clazz = Class.forName(className);
        // ???????????????????????????Stage
        if (clazz.getAnnotation(FXWindow.class) != null) {
            logger.info("loading stage of class: " + className);
            FXControllerFactory.loadStage(clazz, beanBuilder);
        }
    }
}
