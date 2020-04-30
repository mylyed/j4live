package com.mylyed.j4live.protocol.rtmp.server;

import com.mylyed.j4live.protocol.amf.AMF0Object;
import com.mylyed.j4live.protocol.rtmp.chunk.CommandFactory;
import com.mylyed.j4live.protocol.rtmp.chunk.RtmpConstants;
import com.mylyed.j4live.protocol.rtmp.chunk.UserControlMessageFactory;
import com.mylyed.j4live.protocol.rtmp.chunk.message.Command;
import com.mylyed.j4live.protocol.rtmp.chunk.message.pcm.SetChunkSize;
import com.mylyed.j4live.protocol.rtmp.chunk.message.pcm.SetPeerBw;
import com.mylyed.j4live.protocol.rtmp.chunk.message.pcm.WindowAckSize;
import com.mylyed.j4live.stream.StreamHandler;
import com.mylyed.j4live.stream.StreamHandlerManager;
import com.mylyed.j4live.stream.StreamHandlerName;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 命令处理器
 * <p>
 * 专门用户处理命令
 * <pre>
 *          +-------------+                                     +----------+
 *          |  Client     |                  |                  |  Server  |
 *          +-------------+                  |                  +----------+
 *                 |          |      Handshaking  Done    |          |
 *                 |                         |                       |
 *                 |                         |                       |
 *        ---+---- |---------   Command Message(connect)   --------->|
 *           |     |                                                 |
 *        Connect  |<---------- Window Acknowledge Size -------------|
 *           |     |                                                 |
 *           |     |<------------- Set Peer BandWidth ---------------|
 *           |     |                                                 |
 *           |     |----------- Window Acknowledge Size ------------>|
 *           |     |                                                 |
 *           |     |<--------- User Control(StreamBegin) ------------|
 *           |     |                                                 |
 *        ---+---- |--------------- Command Message ---------------->|
 *                 |          (_result- connect response)            |
 *                 |                                                 |
 *        ---+---- |---------Command Message(createStream) --------->|
 *        Create   |                                                 |
 *        Stream   |                                                 |
 *        ---+---- |<-------------- Command Message -----------------|
 *                 |       (_result- createStream response)          |
 *                 |                                                 |
 *        ---+---- |--------- Command Message (publish) ------------>|
 *           |     |                                                 |
 *        publish  |<-------- UserControl (StreamBegin) -------------|
 *           |     |                                                 |
 *           |     |---------- Data Message (Metadata) ------------->|
 *           |     |                                                 |
 *           |     |------------------ Audio Message---------------->|
 *           |     |                                                 |
 *           |     |----------------- SetChunkSize ----------------->|
 *           |     |                                                 |
 *           |     |<--------------- Command Message ----------------|
 *           |     |            (_result- publish result)            |
 *           |     |------------------ Video Message---------------->|
 *                                          |
 *                                          |
 *                            Until the stream is complete
 * </pre>
 *
 * @author lilei
 * created at 2020/4/26
 */
@Slf4j

public class CommandHandler extends RtmpMessageHandler<Command> {


    private int bytesReadWindow = 2500000;
    private long bytesRead;
    private long bytesReadLastSent;

    private long bytesWritten;
    private int bytesWrittenWindow = 2500000;
    private int bytesWrittenLastReceived;

    public CommandHandler(StreamHandlerManager streamManager) {
        super(streamManager);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        log.debug("command: {}", command);
        String commandName = command.getCommandName();
        switch (commandName) {
            case "connect":
                handleConnect(ctx, command);
                break;
            case "createStream":
                handleCreateStream(ctx, command);
                break;
            case "publish":
                handlePublish(ctx, command);
                break;
//            case "play":
//                handlePlay(ctx, msg);
//                break;
            case "deleteStream":
            case "closeStream":
                handleCloseStream(ctx, command);
                break;
//            default:
//                break;
        }
    }

    private void handleCloseStream(ChannelHandlerContext ctx, Command command) {
        log.debug("handleCloseStream:{}", command);
        StreamHandler streamHandler = tryGetStreamHandler(ctx);
        streamManager.removeStreamHandler(streamHandler);
    }

    /**
     * 处理连接
     * <pre>
     *          ---+---- |---------   Command Message(connect)   --------->|
     *             |     |                                                 |
     *          Connect  |<---------- Window Acknowledge Size -------------|
     *             |     |                                                 |
     *             |     |<------------- Set Peer BandWidth ---------------|
     *             |     |                                                 |
     *             |     |----------- Window Acknowledge Size ------------>|
     *             |     |                                                 |
     *             |     |<--------- User Control(StreamBegin) ------------|
     *             |     |                                                 |
     *          ---+---- |<--------------- Command Message ----------------|
     *             |     |           (_result- connect response)           |
     *      *  </pre>
     *
     * @param ctx
     * @param command 例：
     *                CommandAMF0(super=Command(
     *                commandName=connect,
     *                transactionId=1,
     *                object=
     *                {
     *                app=live,
     *                type=nonprivate,
     *                flashVer=FMLE/3.0 (compatible; FMSc/1.0),
     *                swfUrl=rtmp://127.0.0.1/live,
     *                tcUrl=rtmp://127.0.0.1/live
     *                },
     *                args=[]))
     */
    private void handleConnect(ChannelHandlerContext ctx, Command command) {
        log.debug("handleConnect");
        AMF0Object amf0Object = command.getObject();
        if (amf0Object.containsKey("objectEncoding")) {
            Number objectEncoding = Double.valueOf(amf0Object.get("objectEncoding").toString());
            if (objectEncoding.intValue() == 3) {
                log.debug("不支持AMF3格式");
                ctx.close();
                return;
            }
        }
        //Window Acknowledge Size 确认窗口大小
        ctx.writeAndFlush(new WindowAckSize(bytesWrittenWindow));

        //Set Peer BandWidth 设置对等端带宽
        ctx.writeAndFlush(SetPeerBw.soft(bytesReadWindow));
        SetChunkSize setChunkSize = new SetChunkSize(5000);
        ctx.writeAndFlush(setChunkSize);
        //User Control(StreamBegin)
        ctx.writeAndFlush(UserControlMessageFactory.streamBegin(command.getHeader().getCsid()));
        //_result- connect response
        final Command result = CommandFactory.connectSuccess(command.getTransactionId());
        ctx.writeAndFlush(result);

        StreamHandlerName streamHandlerName = new StreamHandlerName(amf0Object.get("app").toString());
        setStreamHandlerName(ctx, streamHandlerName);
    }

    /**
     * 创建流
     * <pre>
     * ---+---- |---------Command Message(createStream) --------->|
     * Create   |                                                 |
     * Stream   |                                                 |
     * ---+---- |<-------------- Command Message -----------------|
     *          |       (_result- createStream response)          |
     * </pre>
     *
     * @param ctx
     * @param command CommandAMF0(super=Command(commandName=createStream, transactionId=4, object=null, args=[]))
     */
    private void handleCreateStream(ChannelHandlerContext ctx, Command command) {
        log.debug("handleCreateStream");
        final Command commandAMF0 = CommandFactory.createStreamSuccess(command.getTransactionId(), RtmpConstants.DEFAULT_STREAM_ID);
        ctx.writeAndFlush(commandAMF0);

    }

    /**
     * 推送
     * <pre>
     *  ---+---- |--------- Command Message (publish) ------------>|
     *  |     |                                                 |
     * publish|<-------- UserControl (StreamBegin) -------------|
     *  |     |                                                 |
     *  |     |---------- Data Message (Metadata) ------------->|
     *  |     |                                                 |
     *  |     |------------------ Audio Message---------------->|
     *  |     |                                                 |
     *  |     |----------------- SetChunkSize ----------------->|
     *  |     |                                                 |
     *  |     |<--------------- Command Message ----------------|
     *  |     |            (_result- publish result)            |
     *  |     |------------------ Video Message---------------->|
     *                                  |
     *                                  |
     *                  Until the stream is complete
     * </pre>
     *
     * @param ctx
     * @param command CommandAMF0(super=Command(commandName=publish, transactionId=5, object=null, args=[first, live]))
     */
    private void handlePublish(ChannelHandlerContext ctx, Command command) {
        log.debug("handlePublish");

        final String streamName = (String) command.getArgs()[0];
        final String publishTypeString = (String) command.getArgs()[1];

        log.debug("streamName:{}", streamName);
        log.debug("type:{}", publishTypeString);

        tryGetStreamHandlerName(ctx).setName(streamName);
        createStreamHandler(ctx);

        ctx.writeAndFlush(CommandFactory.publishStart(command.getTransactionId(), streamName.toString()));
    }

    private void createStreamHandler(ChannelHandlerContext ctx) {
        StreamHandlerName streamHandlerName = tryGetStreamHandlerName(ctx);
        log.debug("createStreamHandler:{}", streamHandlerName);
        StreamHandler streamHandler = new StreamHandler(streamHandlerName);
        streamManager.addStreamHandler(streamHandler);

    }


}
