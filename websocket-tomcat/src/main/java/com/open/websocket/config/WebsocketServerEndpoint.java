package com.open.websocket.config;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.open.websocket.handler.MessageHandler;
import com.open.websocket.message.AuthRequest;
import com.open.websocket.message.Message;
import com.open.websocket.util.WebSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author liuxiaowei
 * @date 2022年10月12日 16:00
 * @Description
 */
@Slf4j
@Controller
@ServerEndpoint("/")
public class WebsocketServerEndpoint implements InitializingBean {

    /**
     * 消息类型与 MessageHandler 的映射
     * <p>
     * 注意，这里设置成静态变量。虽然说 WebsocketServerEndpoint 是单例，但是 Spring Boot 还是会为每个 WebSocket 创建一个 WebsocketServerEndpoint Bean 。
     */
    private static final Map<String, MessageHandler> HANDLERS = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        log.info("[onOpen][session({}) 接入]", session);
        // 解析 accessToken
        List<String> accessTokenValues = session.getRequestParameterMap().get("accessToken");
        String accessToken = !CollectionUtils.isEmpty(accessTokenValues) ? accessTokenValues.get(0) : null;
        // 创建 AuthRequest 消息类型
        AuthRequest authRequest = new AuthRequest().setAccessToken(accessToken);
        // 获得消息处理器
        MessageHandler<AuthRequest> messageHandler = HANDLERS.get(AuthRequest.TYPE);
        if (messageHandler == null) {
            log.error("[onOpen][认证消息类型，不存在消息处理器]");
            return;
        }
        messageHandler.execute(session, authRequest);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        // 生产环境下，请设置成 debug 级别
        log.info("[onOpen][session({}) 接收到一条消息({})]", session, message);
        try {
            // 获得消息类型
            JSONObject jsonMessage = JSONUtil.toBean(message,JSONObject.class);
            String messageType = jsonMessage.getStr("type");
            // 获得消息处理器
            MessageHandler messageHandler = HANDLERS.get(messageType);
            if (messageHandler == null) {
                log.error("[onMessage][消息类型({}) 不存在消息处理器]", messageType);
                return;
            }
            // 解析消息
            Class<? extends Message> messageClass = this.getMessageClass(messageHandler);
            // 处理消息
            Message messageObj = JSONUtil.toBean(jsonMessage.getStr("body"), messageClass);
            messageHandler.execute(session, messageObj);
        } catch (Throwable throwable) {
            log.info("[onMessage][session({}) message({}) 发生异常]", session, throwable);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.info("[onClose][session({}) 连接关闭。关闭原因是({})}]", session, closeReason);
        WebSocketUtil.removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("[onClose][session({}) 发生异常]", session, throwable);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 通过 ApplicationContext 获得所有 MessageHandler Bean
        // 获得所有 MessageHandler Bean
        applicationContext.getBeansOfType(MessageHandler.class).values()
                // 添加到 handlers 中
                .forEach(messageHandler -> HANDLERS.put(messageHandler.getType(), messageHandler));
        log.info("[afterPropertiesSet][消息处理器数量：{}]", HANDLERS.size());
    }

    private Class<? extends Message> getMessageClass(MessageHandler handler) {
        // 获得 Bean 对应的 Class 类名。因为有可能被 AOP 代理过。
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(handler);
        // 获得接口的 Type 数组
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superclass = targetClass.getSuperclass();
        // 此处，是以父类的接口为准
        while ((Objects.isNull(interfaces) || 0 == interfaces.length) && Objects.nonNull(superclass)) {
            interfaces = superclass.getGenericInterfaces();
            superclass = targetClass.getSuperclass();
        }
        if (Objects.nonNull(interfaces)) {
            // 遍历 interfaces 数组
            for (Type type : interfaces) {
                // 要求 type 是泛型参数
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    // 要求是 MessageHandler 接口
                    if (Objects.equals(parameterizedType.getRawType(), MessageHandler.class)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        // 取首个元素
                        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                            return (Class<Message>) actualTypeArguments[0];
                        } else {
                            throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
                        }
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
    }
}
