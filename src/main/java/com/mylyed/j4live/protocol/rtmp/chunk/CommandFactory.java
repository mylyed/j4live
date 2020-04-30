package com.mylyed.j4live.protocol.rtmp.chunk;

import com.mylyed.j4live.protocol.amf.AMF0Object;
import com.mylyed.j4live.protocol.rtmp.chunk.message.Command;
import com.mylyed.j4live.protocol.rtmp.chunk.message.CommandAMF0;
import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * @author lilei
 * created at 2020/4/27
 */
@UtilityClass
public class CommandFactory {

    /**
     * 连接成功
     *
     * @param transactionId
     * @return
     */
    public static Command connectSuccess(Integer transactionId) {
        Objects.requireNonNull(transactionId, "transactionId");

        AMF0Object object = new AMF0Object()
                .addProperty("fmsVer", "FMS/3,0,1,123")
                .addProperty("capabilities", 31);

        AMF0Object args = new AMF0Object()
                .addProperty("level", "status")
                .addProperty("code", "NetConnection.Connect.Success")
                .addProperty("description", "Connection succeeded")
                .addProperty("objectEncoding", 0);

        Command command = new CommandAMF0("_result", transactionId, object, args);

        return command;
    }

    /**
     * 创建流成功
     *
     * @param transactionId
     * @param streamId      流编号
     * @return
     */
    public static Command createStreamSuccess(int transactionId, int streamId) {
        return new CommandAMF0("_result", transactionId, null, streamId);
    }

    /**
     * 推送开始
     *
     * @param transactionId
     * @return
     */
    public static Command publishStart(Integer transactionId, String streamName) {
        AMF0Object status = new AMF0Object()
                .addProperty("level", "status")
                .addProperty("code", "NetStream.Publish.Start")
                .addProperty("description", "Start publishing")
                .addProperty("objectEncoding", 0)
                .addProperty("details", streamName);

        Command command = new CommandAMF0("onStatus", transactionId, null, status);
        //todo
        command.getHeader().setCsid(8);
        return command;
    }


}
