package cn.hyman.testnetwork;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import cn.hyman.network.network.CommonNetworkRequest;
import cn.hyman.network.network.INetworkCallback;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by hyman on 2018/5/4.
 */
public class HttpRequestManager extends BaseManager<Context> {
    private static final String TAG = HttpRequestManager.class.getSimpleName();
    private final Context mContext;
    private static HttpRequestManager sInstance;
    private static boolean isInitialized = false;
    //封装网络请求接口
    private final ApiStore mApiStore;

    public static boolean isInitialized() {
        return isInitialized;
    }

    public static void initialize(Context context) {
        Log.d(TAG, "HttpRequestManager initializing...");
        if (sInstance == null) {
            synchronized (HttpRequestManager.class) {
                if (sInstance == null) {
                    sInstance = new HttpRequestManager(context);
                    isInitialized = true;
                }
            }
        }

        Log.d(TAG, "HttpRequestManager initialized.");
    }

    public static HttpRequestManager getInstance() {
        if (sInstance != null) {
            return sInstance;
        } else {
            throw new IllegalStateException("HttpRequestManager was not initialized.");
        }
    }

    private HttpRequestManager(Context context) {
        this.mContext = context;
        mApiStore = CommonNetworkRequest.getInstance().create(ApiStore.class, Constants.BASE_URL);
    }

    interface ApiStore {
        @GET("app/login")
        Call<LoginResponse> loginGet(@Query("username") String username,
                                     @Query("password") String password);

        @FormUrlEncoded
        @POST("app/login")
        Call<LoginResponse> loginPost(@Field("username") String username,
                                      @Field("password") String password);
    }

    /**
     * 登录GET方式
     *
     * @param username
     * @param password
     * @param activity
     * @param callback
     */
    public void loginGet(String username, String password,
                         Activity activity, INetworkCallback<LoginResponse> callback) {
        Call<LoginResponse> mCall = mApiStore.loginGet(username, password);
        mCall.enqueue(new RetrofitCallBack(callback, activity, LoginResponse.class, true));
    }

    /**
     * 登录POST 方式
     *
     * @param username
     * @param password
     * @param activity
     * @param callback
     */
    public void loginPost(String username, String password,
                          Activity activity, INetworkCallback<LoginResponse> callback) {
        Call<LoginResponse> call = mApiStore.loginPost(username, password);
        call.enqueue(new RetrofitCallBack(callback, activity, LoginResponse.class, true));

    }


}
