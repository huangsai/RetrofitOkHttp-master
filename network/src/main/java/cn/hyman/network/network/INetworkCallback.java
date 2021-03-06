package cn.hyman.network.network;

/**
 * @className: INetworkCallback
 * @classDescription: Api回调
 * @author: hyman
 */
public interface INetworkCallback<T> {
    // 请求数据成功
    void onSuccess(T response);

    // 请求数据错误
    void onError(String err_msg);

    // 网络请求失败
    void onFailure();
}
