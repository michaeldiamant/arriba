package arriba.server;


public class Both {


    public static void main(final String[] args) {
        //        new NettyAcceptor();
        new FixServer().start();
        new NettyInitiator();
    }
}
