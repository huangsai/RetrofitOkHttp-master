package cn.hyman.network.network;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @className: OkHttpInterceptor
 * @classDescription: Http拦截器
 * @author: hyman
 */
public class OkHttpInterceptor implements Interceptor {
    // 日志标识
    private final static String TAG = "network@OkHttpInterceptor";
    // utf8
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @SuppressLint("LongLogTag")
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        // 获得Connection，内部有route、socket、handshake、protocol方法
        Connection connection = chain.connection();
        // 如果Connection为null，返回HTTP_1_1，否则返回connection.protocol()
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        // 比如: --> POST http://121.40.227.8:8088/api http/1.1
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        Log.e(TAG, "#requestStartMessage=" + requestStartMessage);
        // 打印 Response
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            throw e;
        }
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        if (bodyEncoded(response.headers())) {
            Log.e(TAG, "#bodyEncoded");
        } else {
            BufferedSource source = responseBody.source();
            // Buffer the entire body.
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            if (contentLength != 0) {
                // 获取Response的body的字符串 并打印
//                Log.e(TAG,"#body=" + buffer.readString(charset));
            }
        }
        return response;
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}
