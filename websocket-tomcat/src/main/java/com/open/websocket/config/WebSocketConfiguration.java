package com.open.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author liuxiaowei
 * @date 2022年10月12日 16:01
 * @Description
 */
@Configuration
// @EnableWebSocket 无需添加该注解，因为并不是使用 Spring WebSocket
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        // 创建 ServerEndpointExporter Bean
        // 该 Bean 的作用，是扫描添加有 @ServerEndpoint 注解的 Bean
        return new ServerEndpointExporter();
    }
}
