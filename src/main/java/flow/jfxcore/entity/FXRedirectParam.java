package flow.jfxcore.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author suisui
 * @version 1.2
 * @description 跳转窗口携带的参数
 * @date 2020/4/6 18:06
 * @since JavaFX2.0 JDK1.8
 */
public class FXRedirectParam {
    /**
     * 跳转的目标Controller
     */
    private String toController;
    /**
     * query方式的参数, like: helloController?name=JavaFx-Plus&msg=helloWorld
     * the map will store: { name -> JavaFx-Plus, msg -> helloWorld}
     */
    private Map<String, Object> query = new HashMap<>();

    /**
     * param方式的参数，会以map方式传递给目标Controller
     */
    private Map<String, Object> params = new HashMap<>();

    public FXRedirectParam(String toController) {
        this.toController = toController;
    }

    public String getToController() {
        return toController;
    }

    public void setToController(String toController) {
        this.toController = toController;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Map<String, Object> getQueryMap() {
        return query;
    }


    public FXRedirectParam addParam(String key, Object param) {
        this.params.put(key, param);
        return this;
    }

    public Object getParam(String key) {
        return this.params.get(key);
    }

    public FXRedirectParam addQuery(String key, Object param) {
        this.query.put(key, param);
        return this;
    }

    public Object getOneQuery(String key) {
        return this.query.get(key);
    }
}
