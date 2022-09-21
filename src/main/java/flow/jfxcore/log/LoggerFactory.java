package flow.jfxcore.log;

import org.apache.log4j.Logger;

/**
 * log工厂类
 */
public class LoggerFactory {

    static {
        initLog4jBase();
    }

    private LoggerFactory() {
    }

    public static ILogger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz);
        return new LoggerWrapper(logger);
    }

    /**
     * 初始化log4j的路径,如果没有设置那么默认在当前路径下.
     */
    private static void initLog4jBase() {
        if (System.getProperty("log.base") == null) {
            String projectPath = System.getProperty("user.dir");;
            System.setProperty("log.base", projectPath);
        }
    }

}
