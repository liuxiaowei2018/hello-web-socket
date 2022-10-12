package com.open.websocket.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuxiaowei
 * @date 2022年10月12日 16:08
 * @Description 用户认证响应
 */
@Data
@Accessors(chain = true)
public class AuthResponse implements Message{

    public static final String TYPE = "AUTH_RESPONSE";

    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应提示
     */
    private String message;
}
