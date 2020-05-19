import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private boolean authOk = true;
    public static String userName = null;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String input = "";
        input = (String)(input + msg);
        ctx.pipeline().addLast(new MainHandler());//username
        // /auth user1
        if (authOk) {
            ctx.fireChannelRead(input);
            return;
        }
        if (input.split(" ")[0].equals("/auth")) {
            userName = input.split(" ")[1];
            authOk = true;
            System.out.println("AuthOk");
            ctx.pipeline().addLast(new MainHandler());
        }
    }

    public static String getUsername() {
        if (userName != null) {
            return userName;
        }
        return "Username is not registered";
    }
}
