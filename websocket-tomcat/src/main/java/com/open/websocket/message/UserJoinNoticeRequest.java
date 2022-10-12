package com.open.websocket.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuxiaowei
 * @date 2022年10月12日 16:09
 * @Description 用户加入群聊的通知 Message
 */
@Data
@Accessors(chain = true)
public class UserJoinNoticeRequest implements Message{

    public static final String TYPE = "USER_JOIN_NOTICE_REQUEST";

    /**
     * 昵称
     */
    private String nickname;
}
