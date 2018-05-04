package cn.hyman.network.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import cn.hyman.network.network.response.BaseResponse;
import cn.hyman.network.module.AppManager;
import cn.hyman.network.utils.StringUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @className: CommonRetrofitCallback
 * @classDescription: 统一Api回调
 */
public abstract class CommonRetrofitCallback<T> implements Callback <T>{
    // 日志标识
    private final static String TAG = "network@CommonRetrofitCallback";
    // 页面弱引用为空
    private final static String ACTIVITY_WEAK_REF_IS_NULL = "activity weak ref is null";
    // 回调更新页面非当前页面
    private final static String UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE = "update ui page is not request current page";
    // 添加json标签名称
    public final static String JK_JSON_NAME = "hyman_json_name";

    // 数据格式{code：0，reason：信息，result：result}
    // 数据解析标识--code
    public final static String DATA_ANALYSIS_IDENTIFY_CODE = "code";
    // 数据解析标识--reason
    public final static String DATA_ANALYSIS_IDENTIFY_REASON = "reason";
    // 数据解析标识--result
    public final static String DATA_ANALYSIS_IDENTIFY_RESULT = "result";

    // 状态标识--0
    public final static String STATUS_IDENTIFY_ZORE = "0";
    // 状态标识--1
    public final static String STATUS_IDENTIFY_ONE = "1";
    //是否解析数据
    private final boolean isParseData;
    // 回调
    private INetworkCallback<T> mCallback;
    // 页面弱引用
    private WeakReference<Activity> activityWeakRef;
    // type cls
    public Class<T> typeCls;
    // ui thread handler
    public Handler mHandler;
    // 异常
    public final static String EXCEPTION = "exception";
    // 空数据
    public final static String NULL_DATA  = "nodata";

    /**
     * Constructor
     * @param mCallback 回调
     * @param activity 页面实例
     * @param typeCls 数据类型
     * @param isParseData 是否解析数据
     * @return
     */
    public CommonRetrofitCallback(INetworkCallback<T> mCallback, Activity activity,
                                  Class<T> typeCls, boolean isParseData){
        this.mCallback = mCallback;
        activityWeakRef = new WeakReference<>(activity);
        this.typeCls = typeCls;
        mHandler = new Handler(Looper.getMainLooper());
        this.isParseData = isParseData;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (activityWeakRef == null
                || activityWeakRef.get() == null) {
            Log.e(TAG, "#" + ACTIVITY_WEAK_REF_IS_NULL);
            return;
        }
        // 处理是否当前页，如果非当前页则无需回调更新UI
        if (!AppManager.getInstance().isCurrent(activityWeakRef.get())){
            Log.e(TAG, "#" + UPDATE_UI_PAGE_IS_NOT_CURRENT_PAGE);
            return;
        }
        // 若数据为空则回调返回
        if (response == null || response.body() == null) {
            mCallback.onError(NULL_DATA);
            Log.e(TAG, "#" + NULL_DATA);
            return;
        }
        String body = (String) response.body();
        // 无数据回调处理
        if (StringUtil.isEmpty(body)){
            mCallback.onError(NULL_DATA);
            Log.e(TAG, "#" + NULL_DATA);
            return;
        }
        if(isParseData){
            //需要解析数据
            requestParseDeal(body);
        }else {
            //直接返回数据
            requestNotParseDeal(body);
        }
        Log.e(TAG, "#are you compatible data ?");
        // 兼容数据
        compatibleData();
    }

    // 兼容数据
    public abstract void compatibleData();

    /**
     * 数据格式二处理
     * @param body 数据字符串
     */
    private void requestNotParseDeal(String body) {
        if (StringUtil.isNotEmpty(body)){
            successCallBack(body);
        }else {
            errorCallBack(NULL_DATA);
        }
    }

    /**
     * 数据格式一处理
     * @param body 数据字符串
     * @return
     */
    private void requestParseDeal(String body) {
        try {
            JSONObject mJson = new JSONObject(body);
            // 响应码
            String code = mJson.optString(DATA_ANALYSIS_IDENTIFY_CODE);
            // 消息
            String reason = mJson.optString(DATA_ANALYSIS_IDENTIFY_REASON);
            // 结果信息
            String result = mJson.optString(DATA_ANALYSIS_IDENTIFY_RESULT);
            // 改造后json字符串
            String remakeStr = remakeJsonData(body);
            // 获取需解析数据
            JSONObject remakeJsonObject = new JSONObject(remakeStr);
            String data = remakeJsonObject.optString(JK_JSON_NAME);
            // 请求成功
            if (STATUS_IDENTIFY_ZORE.equals(code)
                    && StringUtil.isNotEmpty(result)){
                // Gson解析数据，回调数据
                BaseResponse baseResponse;
                baseResponse = (BaseResponse) new Gson().fromJson(data, typeCls);
                baseResponse.setSuccess(true);
                baseResponse.setMsg(reason);
                baseResponse.setInfo(data);
                successCallBack(baseResponse);
            }else {
                // 请求错误（回调错误信息）
                errorCallBack(reason);
            }
        } catch (JSONException e) {
            // 异常
            errorCallBack(EXCEPTION);
            e.printStackTrace();
        }
    }

    /**
     * 改造json数据，增加一层标签
     * @param jsonStr 改造前json字符串
     * @return jsonObject.toString 改造后json字符串
     */
    public static String remakeJsonData(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JK_JSON_NAME, new JSONObject(jsonStr));
        return jsonObject.toString();
    }

    /**
     * 成功回调
     * @param object
     * @return
     */
    private void successCallBack(final Object object){
        if (mCallback != null && mHandler != null){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (object != null){
                        mCallback.onSuccess((T) object);
                    }else {
                        mCallback.onSuccess(null);
                    }
                }
            });
        }
    }

    /**
     * 请求错误回调
     * @param msg 错误消息
     * @return
     */
    private void errorCallBack(final String msg){
        if (mCallback != null && mHandler != null){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError(msg);
                }
            });
        }
    }

    /**
     * 请求失败回调
     * @param
     * @return
     */
    private void failCallBack(){
        if (mCallback != null && mHandler != null){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onFailure();
                }
            });
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (activityWeakRef == null
                || activityWeakRef.get() == null) {
            Log.e(TAG, "#onFailure#" + ACTIVITY_WEAK_REF_IS_NULL);
            return;
        }
        // 失败回调
        failCallBack();
    }
}
