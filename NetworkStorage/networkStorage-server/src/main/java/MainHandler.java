import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import io.netty.util.ReferenceCountUtil;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainHandler extends ChannelInboundHandlerAdapter {
    private String username;
    List<String> fileList;

    public MainHandler() {
        this.username =  AuthHandler.getUsername();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            if (msg instanceof FileRequest) {
                FileRequest freq = (FileRequest) msg;
                if (Files.exists(Paths.get("server_storage/" + freq.getFileName()))) {
                    FileMessage fmsg = new FileMessage(Paths.get("server_storage/" + freq.getFileName()));
                    ctx.writeAndFlush(fmsg);
                    System.out.println("FileRequest is success " + fmsg);
                }
            }
            if (msg instanceof FileMessage) {
                FileMessage fmsg = (FileMessage) msg;
                Files.write(Paths.get("server_storage/" + fmsg.getFileName()),
                        fmsg.getData(), StandardOpenOption.CREATE);
                refreshServerFileList();
                sendFileList(ctx);
                System.out.println("FileMessage is success " + fmsg);
            }
            if (msg instanceof FileCommand) {
                String cmd = ((FileCommand) msg).getCommand();
                String cmdDel = ((FileCommand) msg).getFileName();

                if (cmd.equals("getServerList")){
                    refreshServerFileList();
                    System.out.println("List length = " + fileList.size());
                    System.out.println("get ServerList is success");
                }
                if (cmd.equals("del")){
                    if (cmdDel != null) {
                        System.out.println("del try = " + cmdDel);
                        File file = new File("server_storage/" + cmdDel);
                        if( file.delete()){
                            System.out.println("server_storage/" + cmdDel + " файл удален");
                        } else {
                            System.out.println("Файл" +  cmdDel + " не обнаружен");
                        }
                        refreshServerFileList();
                    }
                    System.out.println("Delete List is success ");
                }
                sendFileList(ctx);
            }

            if (msg == null) {
                return;
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void refreshServerFileList() {
        File dir = new File("server_storage/");
        String[] arrFiles = dir.list();
        fileList = Arrays.asList(arrFiles);
        System.out.println("List length= " + fileList.size());
        for (String file : fileList) {
            System.out.println(file);
        }
    }

    public void sendFileList(ChannelHandlerContext ctx){
        try {
            FileList flist = new FileList(fileList);
            ctx.writeAndFlush(flist);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Transfer list is success");
    }


    public static void updateUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }
}
