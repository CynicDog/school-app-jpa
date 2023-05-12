package org.jhta.util;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;
import org.jhta.domain.LoginUser;

public class LoginUserCodec implements MessageCodec<LoginUser, LoginUser> {

    @Override
    public void encodeToWire(Buffer buffer, LoginUser loginUser) {
        String jsonStr = Json.encode(loginUser);
        buffer.appendInt(jsonStr.getBytes().length);
        buffer.appendString(jsonStr);
    }

    @Override
    public LoginUser decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        String jsonStr = buffer.getString(pos + 4, pos + 4 + length);
        return Json.decodeValue(jsonStr, LoginUser.class);
    }

    @Override
    public LoginUser transform(LoginUser loginUser) {
        return loginUser;
    }

    @Override
    public String name() {
        return "LoginUserCodec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
