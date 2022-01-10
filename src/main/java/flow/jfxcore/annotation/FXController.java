package flow.jfxcore.annotation;

import flow.jfxcore.locale.FXLanguageLocale;

import java.lang.annotation.*;

/**
 * This is use for marking A controller as FX-Plus Controller
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface FXController {
    /**
     * fxml资源文件的路径
     */
    String path();

    /**
     * @return
     * @description 程序语言，默认不设置
     * @version 1.2
     */
    FXLanguageLocale locale() default FXLanguageLocale.NONE;
}
