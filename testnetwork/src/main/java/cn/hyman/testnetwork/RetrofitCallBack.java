package cn.hyman.testnetwork;

import android.app.Activity;

import cn.hyman.network.network.CommonRetrofitCallback;
import cn.hyman.network.network.INetworkCallback;

/**
 * Created by hyman on 2018/5/4.
 */
public class RetrofitCallBack extends CommonRetrofitCallback {

    /**
     * Constructor
     *
     * @param mCallback   回调
     * @param activity    页面实例
     * @param typeCls     数据类型
     * @param isParseData 是否解析数据
     * @return
     */
    public RetrofitCallBack(INetworkCallback mCallback, Activity activity, Class typeCls, boolean isParseData) {
        super(mCallback, activity, typeCls, isParseData);
    }

    @Override
    public void compatibleData() {

    }
}
