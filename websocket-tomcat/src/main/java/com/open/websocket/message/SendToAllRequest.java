package com.open.websocket.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuxiaowei
 * @date 2022年10月12日 16:10
 * @Description 发送给所有人的群聊消息的 Message
 */
@Data
@Accessors(chain = true)
public class SendToAllRequest implements Message{

    public static final String TYPE = "SEND_TO_ALL_REQUEST";

    /**
     * 消息编号
     */
    private String msgId;
    /**
     * 内容
     */
    private String content;
}
