package cn.hyman.network.network;

import android.annotation.SuppressLint;
import android.util.Log;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @className: CommonNetworkConvertFactory
 * @classDescription: this converter decode the response.
 */
public class CommonNetworkConvertFactory extends Converter.Factory{
    // 日志标识
    private final static String TAG = "network@CommonNetworkConvertFactory";

    public static CommonNetworkConvertFactory create() {
        return create(new Gson());
    }

    public static CommonNetworkConvertFactory create(Gson gson) {
        return new CommonNetworkConvertFactory(gson);
    }

    private CommonNetworkConvertFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new GsonResponseBodyConverter<>();
    }

    final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

        @SuppressLint("LongLogTag")
        @Override public T convert(ResponseBody value) throws IOException {
            String reString;
            try {
                reString = value.string();
                Log.e(TAG, "#body=" + reString);
                return (T) reString;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
