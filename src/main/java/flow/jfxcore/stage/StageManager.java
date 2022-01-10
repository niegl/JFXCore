package flow.jfxcore.stage;

import flow.jfxcore.core.FXNotifyController;
import flow.jfxcore.core.FXPlusContext;
import flow.jfxcore.entity.FXRedirectParam;
import flow.jfxcore.exception.InvalidURLException;
import flow.jfxcore.log.IPlusLogger;
import flow.jfxcore.log.PlusLoggerFactory;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author suisui
 * @version 1.1
 * @description 舞台管理器
 * @date 2019/12/3 15:43
 * @since JavaFX2.0 JDK1.8
 */
public class StageManager {
    private static final IPlusLogger logger = PlusLoggerFactory.getLogger(StageManager.class);
    private static StageManager stageManager = null;
    private static Map<String, FXNotifyController> initWindows = new ConcurrentHashMap<>();
    private static Map<String, Class> windowClazz = new ConcurrentHashMap<>();
    /**
     * @author yangsuiyu
     * @description 1.2新增属性
     */
    private static ArrayDeque<FXNotifyController> windowsStack = new ArrayDeque<>();

    private StageManager() {

    }

    /**
     * 单例
     *
     * @return
     */
    public static synchronized StageManager getInstance() {
        if (stageManager == null) {
            stageManager = new StageManager();
        }
        return stageManager;
    }

    /**
     * 注册FXWindow注解的Controller
     *
     * @param clazz
     * @param fxBaseControllerProxy
     */
    public void registerWindow(Class clazz, FXNotifyController fxBaseControllerProxy) {
        fxBaseControllerProxy.getClass().getDeclaredAnnotations();
        initWindows.put(fxBaseControllerProxy.getName(), fxBaseControllerProxy);
        windowClazz.put(fxBaseControllerProxy.getName(), clazz);
    }

    /**
     * 关闭窗口
     *
     * @param controllerName
     */
    public void closeStage(String controllerName) {
        if (initWindows.get(controllerName) != null) {
            initWindows.get(controllerName).closeStage();
        }
    }

    /**
     * @param redirectParams
     * @Description 跳转
     */
    public void redirectTo(Object redirectParams) {
        FXRedirectParam fxRedirectParam = null;
        if (redirectParams instanceof String) {
            if (((String) redirectParams).contains("?")) { //有参数，query return "SuccessController?name=ss&psw=111"
                try {
                    fxRedirectParam = getQueryParamsFromURL((String) redirectParams);
                } catch (InvalidURLException e) {
                    e.printStackTrace();
                }
            } else { //无参数  return  "SuccessController"
                fxRedirectParam = new FXRedirectParam((String) redirectParams);
            }
        } else if (redirectParams instanceof FXRedirectParam) { // return FXRedirectParam
            fxRedirectParam = (FXRedirectParam) redirectParams;
        }
        redirectWithParams(fxRedirectParam);
    }

    /**
     * @param fxRedirectParam
     * @Description 携带参数跳转
     */
    private void redirectWithParams(FXRedirectParam fxRedirectParam) {
        if (fxRedirectParam != null) {
            String toControllerStr = fxRedirectParam.getToController();
            FXNotifyController toController = initWindows.get(toControllerStr);
            if (toController != null) {
                List<FXNotifyController> controllers = FXPlusContext.getControllers(toController.getName());
//                if (controllers.size() > 0) {
//                    FXBaseController newController = controllers.get(controllers.size() - 1);
//                    toController = FXControllerFactory.getFXController(newController.getClass(), toControllerStr);
////                    registerWindow(, toController);
//                }
                logger.debug("redirecting to " + toController.getName());
                toController.setParam(fxRedirectParam.getParams());
                toController.setQuery(fxRedirectParam.getQueryMap());
                toController.showStage();
            }
        }
    }

    public void showStage(String controllerName) {
        FXNotifyController controllerProxy = initWindows.get(controllerName);
        controllerProxy.showStage();
    }

    /**
     * RedirectController?num=10&name=suisui -> Map:{"num","10"},{"name","suisui"}
     *
     * @param url
     * @return
     */
    private FXRedirectParam getQueryParamsFromURL(String url) throws InvalidURLException {
        String[] items = url.split("\\?");
        if (items.length != 2) {
            throw new InvalidURLException();
        }
        String leftBase = items[0];
        String paramsStr = items[1];
        String[] paramsKV = paramsStr.split("&");

        FXRedirectParam fxRedirectParam = new FXRedirectParam(leftBase);
        for (int i = 0; i < paramsKV.length; i++) {
            String params[] = paramsKV[i].split("=");
            if (params.length != 2) {
                throw new InvalidURLException();
            } else {
                fxRedirectParam.addQuery(params[0], params[1]);
            }
        }
        return fxRedirectParam;
    }
}
