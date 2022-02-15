package flow.jfxcore.annotation;

import java.lang.annotation.*;

/**
 * @author suisui
 * @version 1.1
 * @description 重定向的注解
 * @date 2019/12/3 12:53
 * @since JavaFX2.0 JDK1.8
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface FXRedirect {
    /**
     * 是否关闭原窗体
     * @return
     */
    boolean close() default true;

    /**
     * 是否设置父窗体，配合FXWindow使用，当窗体Modality=Modality.WINDOW_MODAL时，需设置owner为true才能起作用。
     * @return Modality.WINDOW_MODAL为true,其他为false.
     */
    boolean hasOwner() default false;

    /**
     * 设置父窗体，如果为上一个窗体可以不设置.
     * @return
     */
    String owner() default "";
}
