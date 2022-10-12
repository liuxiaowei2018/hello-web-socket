package com.open.websocket.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuxiaowei
 * @date 2022年10月12日 16:07
 * @Description 用户认证请求
 */
@Data
@Accessors(chain = true)
public class AuthRequest implements Message{

    public static final String TYPE = "AUTH_REQUEST";

    /**
     * 认证 Token
     */
    private String accessToken;

}
