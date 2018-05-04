package cn.hyman.network.network.response;

import java.io.Serializable;

/**
 * @className: BaseResponse
 * @classDescription: 网络请求返回对象公共抽象类
 * @author: hyman
 */
public class BaseResponse implements Serializable {
    // 序列化UID 用于反序列化
    private static final long serialVersionUID = 234513596098152098L;
    // 是否成功
    private boolean isSuccess;
    //状态码
    public String code;
    // 数据
    public String info;
    // 响应信息
    public String msg;

    /**
     * ------------------------------------get and set---------------------------------------------
     * -----------------------------------------------------------------------------------
     */

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
