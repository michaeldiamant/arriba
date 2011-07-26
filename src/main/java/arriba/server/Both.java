package arriba.server;


public class Both {


    public static void main(final String[] args) {
        new FixServer().start();
        new NettyInitiator();
    }
}
