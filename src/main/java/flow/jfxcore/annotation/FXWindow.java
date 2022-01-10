package flow.jfxcore.annotation;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.lang.annotation.*;

/**
 * @author jack
 * @author suisui
 * @version 1.0
 * @date 2019/6/25 1:36
 * @since JavaFX2.0 JDK1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface FXWindow {
    double preWidth() default 0.0;

    double preHeight() default 0.0;

    boolean resizable() default false;

    boolean mainStage() default false;

    StageStyle style() default StageStyle.DECORATED;

    String title();

    /**
     * 设置对话框的模态
     * @return
     */
    Modality modality() default Modality.NONE;

    String fill() default "WHITE";
    /**
     * @description 图标URL
     * @version 1.2
     */
    String icon() default "";

}
