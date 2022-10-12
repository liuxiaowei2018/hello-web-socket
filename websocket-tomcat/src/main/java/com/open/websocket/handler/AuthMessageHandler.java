package com.open.websocket.handler;

import com.open.websocket.message.AuthRequest;
import com.open.websocket.message.AuthResponse;
import com.open.websocket.message.UserJoinNoticeRequest;
import com.open.websocket.util.WebSocketUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.Session;

/**
 * @author liuxiaowei
 * @date 2022年10月12日 16:12
 * @Description
 */
@Component
public class AuthMessageHandler implements MessageHandler<AuthRequest> {

    @Override
    public void execute(Session session, AuthRequest message) {
        // 如果未传递 accessToken
        if (StringUtils.isEmpty(message.getAccessToken())) {
            WebSocketUtil.send(session, AuthResponse.TYPE,
                    new AuthResponse().setCode(1).setMessage("认证 accessToken 未传入"));
            return;
        }
        // 添加到 WebSocketUtil 中
        // 考虑到代码简化，先直接使用 accessToken 作为 User
        WebSocketUtil.addSession(session, message.getAccessToken());

        // 判断是否认证成功。这里，假装直接成功
        WebSocketUtil.send(session, AuthResponse.TYPE, new AuthResponse().setCode(0));

        // 通知所有人，某个人加入了。这个是可选逻辑，仅仅是为了演示
        WebSocketUtil.broadcast(UserJoinNoticeRequest.TYPE,
                new UserJoinNoticeRequest().setNickname(message.getAccessToken()));
    }

    @Override
    public String getType() {
        return AuthRequest.TYPE;
    }
}
