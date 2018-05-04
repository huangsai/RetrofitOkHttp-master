package cn.hyman.testnetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hyman.testnetwork.R;

import cn.hyman.network.network.INetworkCallback;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login();
    }

    private void login(){
        HttpRequestManager.getInstance().loginGet("test", "123456", MainActivity.this, new INetworkCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                Log.i("test", "loginGet onSuccess");
            }

            @Override
            public void onError(String err_msg) {
                Log.i("test", "loginGet onError");
            }

            @Override
            public void onFailure() {
                Log.i("test", "loginGet onFailure");
            }
        });

        HttpRequestManager.getInstance().loginPost("test", "123456", MainActivity.this, new INetworkCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                Log.i("test", "loginPost onSuccess");
            }

            @Override
            public void onError(String err_msg) {
                Log.i("test", "loginPost onError");
            }

            @Override
            public void onFailure() {
                Log.i("test", "loginPost onFailure");
            }
        });
    }
}
