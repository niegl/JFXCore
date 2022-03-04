package flow.jfxcore.loader;

import com.sun.javafx.util.Utils;

/**
 * Used to access internal FXMLLoader methods.
 */
public class FXMLLoaderHelperExt {
    private static FXMLLoaderHelperExt.FXMLLoaderAccessor fxmlLoaderAccessor;

    static {
        Utils.forceInit(FXMLLoaderExt.class);
    }

    private FXMLLoaderHelperExt() {
    }

    public static void setStaticLoad(FXMLLoaderExt fxmlLoader, boolean staticLoad) {
        fxmlLoaderAccessor.setStaticLoad(fxmlLoader, staticLoad);
    }

    public static void setFXMLLoaderAccessor(final FXMLLoaderHelperExt.FXMLLoaderAccessor newAccessor) {
        if (fxmlLoaderAccessor != null) {
            throw new IllegalStateException();
        }

        fxmlLoaderAccessor = newAccessor;
    }

    public interface FXMLLoaderAccessor {
        void setStaticLoad(FXMLLoaderExt fxmlLoader, boolean staticLoad);
    }
}
