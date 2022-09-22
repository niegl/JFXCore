package javafx.handy.annotation;

import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.lang.annotation.*;

/**
 * 窗体属性相关设置
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface FXWindow {
    double preWidth() default 0.0;

    double preHeight() default 0.0;

    boolean resizable() default true;

    boolean mainStage() default false;

    StageStyle style() default StageStyle.DECORATED;

    String title();

    /**
     * 是否全屏显示
     */
    boolean maximize() default false;

    /**
     * 设置对话框的模态
     * @return
     */
    Modality modality() default Modality.NONE;

    /**
     * 设置窗体填充
     * @return
     */
    String fill() default "WHITE";
    /**
     * @description 图标URL
     * @version 1.2
     */
    String icon() default "";

}
