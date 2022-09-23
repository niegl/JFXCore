package javafx.handy.annotation;

import java.lang.annotation.*;

/**
 * @author jack
 * @version 1.0
 * @date 2019/6/25 13:06
 * @since JavaFX2.0 JDK1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface FXReceiver {
    String name() default "";
}
