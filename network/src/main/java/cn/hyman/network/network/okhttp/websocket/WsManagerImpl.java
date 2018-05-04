package cn.hyman.network.network.okhttp.websocket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import cn.hyman.network.utils.StringUtil;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @className: WsManagerImpl
 * @classDescription: jk websocket manager implement
 * @author: hyman
 */
public class WsManagerImpl implements IWsManager {
    // 日志标识
    public final static String TAG = "network@WsManagerImpl";
    // 重连间隔时间
    private final static int RECONNECT_INTERVAL_TIME = 10 * 1000;
    // 重连最大间隔时间
    private final static long RECONNECT_MAX_INTERVAL_TIME = 120 * 1000;
    // 当前连接状态(默认为断开连接)
    private int mCurrentStatus = WsStatus.ConnectStatus.DISCONNECTED;
    // 是否需要重新连接
    private boolean isNeedReconnect = true;
    // 上下文
    private Context mContext;
    // ws链接
    private String wsUrl;
    // websocket instance
    private WebSocket mWebSocket;
    // jk websocket listener
    private WsStatusListener mWsStatusListener;
    // 重连次数
    private int reconnectCount = 1;
    // websocket handler(用于重连websocket)
    private Handler wsHandler = new Handler(Looper.getMainLooper());
    // 重连runnable
    private Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (mWsStatusListener != null)
                mWsStatusListener.onReconnect();
            buildConnect();
        }
    };
    // websocket listener
    private WebSocketListener mWebSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            // assignin websocket
            mWebSocket = webSocket;
            // set currentStatus as connected
            mCurrentStatus = WsStatus.ConnectStatus.CONNECTED;
            // 取消重连websocket
            cancelReconnect();
            // open status callback
            if (mWsStatusListener != null) {
                Log.e(TAG, "#onOpen#response=" + response);
                mWsStatusListener.onOpen(response);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            // message text callback
            if (mWsStatusListener != null) {
                Log.e(TAG, "#onMessage#text=" + text);
                mWsStatusListener.onMessage(text);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            // message bytes callback
            if (mWsStatusListener != null) {
                if (bytes != null) {
                    Log.e(TAG, "#onMessage#bytes=" + bytes.toString());
                } else {
                    Log.e(TAG, "#onMessage#bytes");
                }
                mWsStatusListener.onMessage(bytes);
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            // set currentStatus as disConnected
            mCurrentStatus = WsStatus.ConnectStatus.DISCONNECTED;
            // closed status callback
            if (mWsStatusListener != null) {
                Log.e(TAG, "#onClosed#code=" + code + "#reason=" + reason);
                mWsStatusListener.onClosed(code, reason);
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            // closing status callback
            if (mWsStatusListener != null) {
                Log.e(TAG, "#onClosing#code=" + code + "#reason=" + reason);
                mWsStatusListener.onClosing(code, reason);
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            // set currentStatus as disConnected
            mCurrentStatus = WsStatus.ConnectStatus.DISCONNECTED;
            // failure status callback
            if (mWsStatusListener != null) {
                Log.e(TAG, "#onFailure#t=" + t + "#response=" + response);
                if (t.toString().contains("Socket closed")) {
                    mWsStatusListener.onClosed(WsStatus.StatusCode.NORMAL_CLOSE,
                            WsStatus.StatusTip.NORMAL_CLOSE);
                } else {
                    mWsStatusListener.onFailure(t, response);
                }
            }
        }
    };

    /**
     * 关闭websocket
     *
     * @param code   关闭码
     * @param reason 关闭原因
     */
    public boolean closeWebSocket(int code, String reason) {
        if (mWebSocket != null) {
            return mWebSocket.close(code, reason);
        }
        return false;
    }

    /**
     * 重连websocket
     */
    private void tryReconnect() {
        // 若不需重连或无网则返回
        if (!isNeedReconnect || !isNetworkConnected(mContext)) return;
        // set currentStatus as reconnect
        mCurrentStatus = WsStatus.ConnectStatus.RECONNECT;
        Log.e(TAG, "#tryReconnect#reconnectCount=" + reconnectCount);
        // 重连延迟
        long delay = reconnectCount * RECONNECT_INTERVAL_TIME;
        Log.e(TAG, "#tryReconnect#delay=" + delay);
        // 开始重连
        if (wsHandler != null
                && reconnectRunnable != null) {
            wsHandler.removeCallbacks(reconnectRunnable);
            reconnectCount++;
            wsHandler.postDelayed(reconnectRunnable,
                    delay > RECONNECT_MAX_INTERVAL_TIME ? RECONNECT_INTERVAL_TIME : delay);
            if (delay > RECONNECT_MAX_INTERVAL_TIME) {
                Log.e(TAG, "#tryReconnect#reset reconnectCount");
                reconnectCount = 1;
            }
        }
    }

    /**
     * 取消重连websocket
     */
    public void cancelReconnect() {
        if (wsHandler != null
                && reconnectRunnable != null) {
            wsHandler.removeCallbacks(reconnectRunnable);
            reconnectCount = 1;
        }
    }

    /**
     * set jk websocket listener
     *
     * @param mWsStatusListener jk websocket listener
     */
    public void setJkWsStatusListener(WsStatusListener mWsStatusListener) {
        this.mWsStatusListener = mWsStatusListener;
    }

    /**
     * Constructor
     */
    public WsManagerImpl(Builder builder) {
        mContext = builder.mContext;
        wsUrl = builder.wsUrl;
    }

    /**
     * 检查网络是否连接
     */
    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 创建websocket连接
     */
    private void buildConnect() {
        // 若当前状态为已连接、正在连接中、无网状态则无需创建websocket连接
        if (mCurrentStatus == WsStatus.ConnectStatus.CONNECTED
                | mCurrentStatus == WsStatus.ConnectStatus.CONNECTING)
            return;
        if (!isNetworkConnected(mContext)) {
            if (mWsStatusListener != null) {
                mWsStatusListener.onNoNetWork();
            }
            return;
        }
        // 设置当前状态为正在连接中
        mCurrentStatus = WsStatus.ConnectStatus.CONNECTING;
        // 初始化websocket
        initWebSocket();
    }

    /**
     * 初始化websocket
     */
    private void initWebSocket() {
        if (StringUtil.isNotEmpty(wsUrl)
                && mWebSocketListener != null) {
            OkHttpWebSocketUtils.getInstance().initWsClient(wsUrl, mWebSocketListener);
        }
    }

    /**
     * 发送消息
     *
     * @param msg 消息
     */
    private boolean sendMsg(Object msg) {
        boolean isSend = false;
        if (mWebSocket != null
                && mCurrentStatus == WsStatus.ConnectStatus.CONNECTED) {
            if (msg instanceof String) {
                isSend = mWebSocket.send((String) msg);
            } else if (msg instanceof ByteString) {
                isSend = mWebSocket.send((ByteString) msg);
            }
            // 发送消息失败，尝试重连
            if (!isSend) {
                tryReconnect();
            }
        }
        return isSend;
    }

    /**
     * 断开websocket连接
     */
    private void disconnect() {
        // 若当前状态为断开连接则返回
        if (mCurrentStatus == WsStatus.ConnectStatus.DISCONNECTED)
            return;
        // 取消重连websocket
        cancelReconnect();
        // 取消所有请求
        OkHttpWebSocketUtils.getInstance().cancalAllRequest();
        if (mWebSocket != null) {
            boolean isClosed = closeWebSocket(WsStatus.StatusCode.NORMAL_CLOSE,
                    WsStatus.StatusTip.NORMAL_CLOSE);
            // 非正常关闭连接
            if (!isClosed) {
                // 回调关闭状态
                if (mWsStatusListener != null)
                    mWsStatusListener.onClosed(WsStatus.StatusCode.ABNORMAL_CLOSE,
                            WsStatus.StatusTip.ABNORMAL_CLOSE);
            }
        }
        // set currentStatus as disConnected
        mCurrentStatus = WsStatus.ConnectStatus.DISCONNECTED;
    }

    @Override
    public WebSocket getWebSocket() {
        return mWebSocket;
    }

    @Override
    public void startConnect() {
        isNeedReconnect = true;
        // 创建websocket连接
        buildConnect();
    }

    @Override
    public void stopConnect() {
        isNeedReconnect = false;
        // 断开websocket连接
        disconnect();
    }

    @Override
    public boolean isWsConnected() {
        return mCurrentStatus == WsStatus.ConnectStatus.CONNECTED;
    }

    @Override
    public int getCurrentStatus() {
        return mCurrentStatus;
    }

    @Override
    public boolean sendMessage(String msg) {
        return sendMsg(msg);
    }

    @Override
    public boolean sendMessage(ByteString byteString) {
        return sendMsg(byteString);
    }

    /**
     * @className: Builder
     * @classDescription: 建造者模式构建
     */
    public static final class Builder {
        // 上下文
        private Context mContext;
        // ws链接地址
        private String wsUrl;

        /**
         * ConStructor
         *
         * @param mContext 上下文
         */
        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        /**
         * ws链接地址配置
         *
         * @param wsUrl ws链接地址
         */
        public Builder setWsUrl(String wsUrl) {
            this.wsUrl = wsUrl;
            return this;
        }

        /**
         * 构建WsManagerImpl对象
         */
        public WsManagerImpl build() {
            return new WsManagerImpl(this);
        }
    }
}
