package com.open.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author liuxiaowei
 * @date 2022年10月12日 16:01
 * @Description
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Bean
    public DemoWebSocketHandler webSocketHandler() {
        return new DemoWebSocketHandler();
    }

    @Bean
    public DemoWebSocketShakeInterceptor webSocketShakeInterceptor() {
        return new DemoWebSocketShakeInterceptor();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 配置处理器
        registry.addHandler(this.webSocketHandler(), "/")
                // 配置拦截器
                .addInterceptors(new DemoWebSocketShakeInterceptor())
                // 解决跨域问题
                .setAllowedOrigins("*");
    }
}
