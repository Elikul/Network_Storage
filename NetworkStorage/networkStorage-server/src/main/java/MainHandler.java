
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class MainHandler extends ChannelInboundHandlerAdapter {

    private String nick;
    List<String> filesList;
    File dir;

    public MainHandler(String username) {
        this.nick =  username;
        dir = null;
        System.out.println("MainHandler start" + this.nick);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected...");
        sendFileList(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("nick= " + nick);
        dir = new File("server/" + nick);
        if(!dir.exists()) {
            if(dir.mkdir()) {
                System.out.println("directory " + dir.getAbsolutePath() + " created.");
            } else {
                System.out.println("directory " + dir.getAbsolutePath() + " not created.");
            }
        } else {
            System.out.println("directory " + dir.getAbsolutePath() + " exists.");
        }

        try {
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                if (Files.exists(Paths.get("server/" + nick + "/" + fr.getFileName()))) {
                    FileMessage fm = new FileMessage(Paths.get("server/" + nick + "/" + fr.getFileName()));
                    ctx.writeAndFlush(fm);
                    System.out.println("tranfer Obj OK " + fm);
                }
            }
            if (msg instanceof FileMessage) {//
                FileMessage fm = (FileMessage) msg;
                Files.write(Paths.get("server/" + nick + "/" + fm.getFileName()),
                        fm.getData(), StandardOpenOption.CREATE);
                sendFileList(ctx);
                System.out.println("Obj OK " + fm);
            }
            if (msg instanceof FileCommand) {
                String cmd = ((FileCommand) msg).getCommand();
                String cmdDel = ((FileCommand) msg).getFileName();
                String[] tokens = cmd.split(" ");
                String key = tokens[0];
                if (key.equals("/auth")){
                    if (!tokens[1].equals(nick)) {
                        ctx.writeAndFlush(new FileCommand("/needDiscon",""));
                    }
                }
                if (key.equals("/end_login")||key.equals("/exit")) {
                    final ChannelFuture f = ctx.writeAndFlush(new FileCommand("/exit",""));
                    ctx.fireChannelRead(msg);
                    System.out.println("===Client disconectMH!=== ");
                    f.addListener(ChannelFutureListener.CLOSE);
                    ctx.close();
                    ctx.pipeline().remove(this);
                    return;
                }
                if (key.equals("/delFile")){
                    if (cmdDel!=null) {
                        File file = new File("server/" + nick + "/" + cmdDel);
                        if( file.delete()){
                            System.out.println("server/" + nick + "/" + cmdDel + " file deleted");
                        } else {
                            System.out.println("file" +  cmdDel + " not detected");
                        }
                    }
                    System.out.println("Del List OK ");
                }
                sendFileList(ctx);
            }

            if (msg == null) {
                System.out.println("msg_Null");
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
        System.out.println("ctx_close_Exception");
        ctx.close();
    }

    public void sendFileList (ChannelHandlerContext ctx){
        File dir = new File("server/" + nick + "/");
        String[] arrFiles = dir.list();
        filesList = Arrays.asList(arrFiles);
        try {
            FileList fl = new FileList(filesList);
            ctx.writeAndFlush(fl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("tranfer List OK ");
    }

    public String getNick() {
        return this.nick;
    }

}
