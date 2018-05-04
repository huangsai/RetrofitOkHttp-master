package cn.hyman.network.network.okhttp.websocket;

/**
 * @className: WsStatus
 * @classDescription: jk websocket status()
 * @author: hyman
 */
public class WsStatus {
    /**
     * @className: ConnectStatus
     * @classDescription: 连接状态
     */
    public static class ConnectStatus {
        // 正在连接中
        public final static int CONNECTING = 0;
        // 已经连接上
        public final static int CONNECTED = 1;
        // 重新连接
        public final static int RECONNECT = 2;
        // 断开连接
        public final static int DISCONNECTED = -1;
    }

    /**
     * @className: StatusCode
     * @classDescription: 状态标识
     */
    public static class StatusCode {
        // 正常关闭标识
        public final static int NORMAL_CLOSE = 1000;
        // 非正常关闭标识
        public final static int ABNORMAL_CLOSE = 1001;
    }

    /**
     * @className: StatusTip
     * @classDescription: 状态提示
     */
    public static class StatusTip {
        // 正常关闭提示
        public final static String NORMAL_CLOSE = "normal close";
        // 非正常关闭提示
        public final static String ABNORMAL_CLOSE = "abnormal close";
    }
}
