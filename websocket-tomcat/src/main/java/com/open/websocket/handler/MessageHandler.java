package com.open.websocket.handler;

import com.open.websocket.message.Message;

import javax.websocket.Session;

/**
 * @author liuxiaowei
 * @date 2022年10月12日 16:13
 * @Description 消息处理器接口
 */
public interface MessageHandler<T extends Message> {

    /**
     * 执行处理消息
     *
     * @param session 会话
     * @param message 消息
     */
    void execute(Session session, T message);

    /**
     * @return 消息类型，即每个 Message 实现类上的 TYPE 静态字段
     */
    String getType();
}
