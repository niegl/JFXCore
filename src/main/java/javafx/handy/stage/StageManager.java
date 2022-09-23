package javafx.handy.stage;

import javafx.handy.starter.FXNotifyController;
import javafx.handy.dispatcher.MessageDispatcher;
import javafx.handy.entity.FXRedirectParam;
import javafx.handy.exception.InvalidURLException;
import javafx.handy.log.ILogger;
import javafx.handy.log.LoggerFactory;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理FXNotifyController代理
 */
public class StageManager {
    private static final ILogger logger = LoggerFactory.getLogger(StageManager.class);
    private static final Map<String, FXNotifyController> stages = new ConcurrentHashMap<>();

    private StageManager() {
    }

    /**
     * 注册FXWindow注解的Controller
     * @param fxBaseControllerProxy
     */
    public static void registerStage(FXNotifyController fxBaseControllerProxy) {
        stages.put(fxBaseControllerProxy.getName(), fxBaseControllerProxy);
        MessageDispatcher.registerConsumer(fxBaseControllerProxy); // 添加进入消息队列 信号功能
    }

    /**
     * 关闭窗口
     *
     * @param controllerName
     */
    public static void closeStage(@NotNull String controllerName) {
        FXNotifyController controller = stages.get(controllerName);
        if (controller != null) {
            controller.closeStage();
        }
    }

    /**
     * 关闭窗口
     */
    public static void closeStage(Object stage) {
        if (stage instanceof FXNotifyController controller) {
            FXNotifyController proxy = stages.get(controller.getName());
            if (proxy != null) {
                proxy.closeStage();
            }
        }
    }

    /**
     * 携带参数跳转
     * @param redirectParams
     */
    public static void redirectTo(Object redirectParams, FXNotifyController ownerController) {
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
        redirectWithParams(fxRedirectParam, ownerController);
    }

    /**
     * 携带参数跳转
     * @param fxRedirectParam 参数
     * @param ownerController  传递参数的窗体，在没有显示传递父窗体的情况下为默认的父窗体
     */
    private static void redirectWithParams(FXRedirectParam fxRedirectParam, FXNotifyController ownerController) {
        if (fxRedirectParam != null) {
            String toControllerStr = fxRedirectParam.getToController();
            if (toControllerStr == null) {
                return;
            }
            FXNotifyController toController = stages.get(toControllerStr);
            if (toController != null) {
                logger.debug("redirecting to " + toController.getName());
                toController.setParam(fxRedirectParam.getParams());
                toController.setQuery(fxRedirectParam.getQueryMap());
                Stage toControllerStage = toController.getStage();
                if (toControllerStage != null) {
                    if (null == toControllerStage.getOwner()) {
                        if (ownerController != null) {
                            toController.setOwner(ownerController.getStage());
                        }
                    }
                }

                toController.showStage();
            }
        }
    }

    public static void showStage(@NotNull String controllerName) {
        FXNotifyController controllerProxy = stages.get(controllerName);
        if (controllerProxy != null) {
            controllerProxy.showStage();
        }
    }

    /**
     * 获取controller实例
     * @param controllerName
     * @param <T>
     * @return
     */
    public static <T> T getStage(String controllerName) {
        return (T) stages.get(controllerName);
    }

    /**
     * RedirectController?num=10&name=suisui -> Map:{"num","10"},{"name","suisui"}
     *
     * @param url
     * @return
     */
    private static FXRedirectParam getQueryParamsFromURL(String url) throws InvalidURLException {
        String[] items = url.split("\\?");
        if (items.length != 2) {
            throw new InvalidURLException();
        }
        String leftBase = items[0];
        String paramsStr = items[1];
        String[] paramsKV = paramsStr.split("&");

        FXRedirectParam fxRedirectParam = new FXRedirectParam(leftBase);
        for (String s : paramsKV) {
            String[] params = s.split("=");
            if (params.length != 2) {
                throw new InvalidURLException();
            } else {
                fxRedirectParam.addQuery(params[0], params[1]);
            }
        }
        return fxRedirectParam;
    }
}
