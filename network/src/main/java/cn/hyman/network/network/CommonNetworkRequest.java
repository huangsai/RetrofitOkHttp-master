package cn.hyman.network.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @className:CommonNetworkRequest
 * @classDescription: retrofit request
 * @author: hyman
 */
public class CommonNetworkRequest {
    // sington
    private static CommonNetworkRequest instance;

    /**
     * Constructor
     */
    private CommonNetworkRequest() {
    }

    /**
     * sington
     */
    public static CommonNetworkRequest getInstance() {
        if (instance == null) {
            instance = new CommonNetworkRequest();
        }
        return instance;
    }

    /**
     * create Retrofit
     *
     * @param service
     * @param baseUrl
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service, String baseUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new OkHttpInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(CommonNetworkConvertFactory.create())
                .client(client)
                .build();
        return retrofit.create(service);
    }
}
