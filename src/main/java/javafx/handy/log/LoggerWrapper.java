package javafx.handy.log;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 日志类。对log4j进行了二次封装
 */
public class LoggerWrapper implements ILogger {
    private final Logger logger;
    private final String FQCN;

    public LoggerWrapper(Logger logger) {
        this.FQCN = LoggerWrapper.class.getName();
        this.logger = logger;
    }

    public LoggerWrapper(String fqcn, Logger logger) {
        this.FQCN = fqcn;
        this.logger = logger;
    }

    @Override
    public void debug(Object message) {
        logger.log(FQCN, Level.DEBUG, message, null);
    }

    @Override
    public void debug(Object message, Throwable t) {
        logger.log(FQCN, Level.DEBUG, message, t);
    }

    @Override
    public void info(Object message) {
        logger.log(FQCN, Level.INFO, message, null);
    }

    @Override
    public void info(Object message, Throwable t) {
        logger.log(FQCN, Level.INFO, message, t);
    }

    @Override
    public void warn(Object message) {
        logger.log(FQCN, Level.WARN, message, null);
    }

    @Override
    public void warn(Object message, Throwable t) {
        logger.log(FQCN, Level.WARN, message, t);
    }

    @Override
    public void error(Object message) {
        logger.log(FQCN, Level.ERROR, message, null);
    }

    @Override
    public void error(Object message, Throwable t) {
        logger.log(FQCN, Level.ERROR, message, t);
    }
}
