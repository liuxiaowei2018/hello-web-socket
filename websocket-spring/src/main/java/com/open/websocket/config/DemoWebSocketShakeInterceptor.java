package com.open.websocket.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * @author liuxiaowei
 * @date 2022年10月12日 16:39
 * @Description
 * 自定义 HttpSessionHandshakeInterceptor 拦截器
 * 因为 WebSocketSession 无法获得 ws 地址上的请求参数，所以只好通过该拦截器，获得 accessToken 请求参数，设置到 attributes 中
 */
public class DemoWebSocketShakeInterceptor extends HttpSessionHandshakeInterceptor {

    /**
     * 拦截 Handshake 事件
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return boolean
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 获得 accessToken
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            attributes.put("accessToken", serverRequest.getServletRequest().getParameter("accessToken"));
        }
        // 调用父方法，继续执行逻辑
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
