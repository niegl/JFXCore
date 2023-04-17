package javafx.handy.utils;

import javafx.handy.locale.FXLanguageLocale;
import lombok.extern.slf4j.Slf4j;


import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author suisui
 * @version 1.2
 * @description 语言国际化工具类
 * @date 2020/5/1 11:15
 * @since JavaFX2.0 JDK1.8
 */
@Slf4j
public class ResourceBundleUtil {

    /**
     * @param baseName
     * @param fxPlusLocale
     * @return
     * @description 获取Java的ResourceBundle
     */
    public static ResourceBundle getResourceBundle(String baseName, FXLanguageLocale fxPlusLocale) {
        baseName = StringUtil.trimExtension(baseName);
        baseName = StringUtil.splashToDot(baseName);
        Locale locale = ResourceBundleUtil.getLocale(fxPlusLocale);

//        logger.info(baseName);
        if (locale != null) {
            return ResourceBundle.getBundle(baseName, locale);
        }
        return null;
    }

    /**
     * @param fxPlusLocale
     * @return
     * @description 通过FXPlusLocale枚举类型获取Locale
     */
    private static Locale getLocale(FXLanguageLocale fxPlusLocale) {
        switch (fxPlusLocale) {
            case SIMPLIFIED_CHINESE:
                return Locale.SIMPLIFIED_CHINESE;
            case FRANCE:
                return Locale.FRANCE;
            case KOREAN:
                return Locale.KOREAN;
            case ENGLISH:
                return Locale.UK;
            case GERMANY:
                return Locale.GERMANY;
            case ITALIAN:
                return Locale.ITALIAN;
            case AMERICAN:
                return Locale.US;
            case JAPANESE:
                return Locale.JAPAN;
            case TRADITIONAL_CHINESE:
                return Locale.TRADITIONAL_CHINESE;
            default:
                return null;
        }
    }

    /**
     * @param key
     * @return
     * @description 通过key获取String类型的value值，失败或不存在返回空字符串
     */
    public static String getStringValue(ResourceBundle resource, String key) {
        if ("".equals(key) || null == key) {
            return "";
        }
        try {
            return resource.getString(key);
        } catch (MissingResourceException | ClassCastException e) {
            log.error(e.getMessage());
//            e.printStackTrace();
            return "";
        }
    }

    /**
     * @param key
     * @return
     * @description 通过key获取Integer类型的value值，失败或不存在返回-1
     */
    public static Integer getIntegerValue(ResourceBundle resource, String key) {
        if ("".equals(key) || null == key) {
            return -1;
        }
        try {
            return Integer.valueOf(resource.getString(key));
        } catch (MissingResourceException | NumberFormatException e) {
            log.error(e.getMessage());
//            e.printStackTrace();
            return -1;
        }
    }
}
