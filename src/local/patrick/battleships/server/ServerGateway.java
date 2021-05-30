package local.patrick.battleships.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * A ServerGateway listens for new connections and creates and starts Games once enough Players for a Game have been
 * found
 */
public class ServerGateway implements AutoCloseable {
    private final ServerSocket serverSocket;

    public ServerGateway(Integer port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        System.out.println("Starting ServerGateway on port " + port);
    }

    /**
     * Just runs forever filling games and starting them
     */
    public void run() throws IOException {
        //noinspection InfiniteLoopStatement
        while (true) {
            var socket = serverSocket.accept();
            System.out.println("Got a first client");
            var socket2 = serverSocket.accept();
            System.out.println("Got a second client, creating new game");
            var game = new Game(socket, socket2);
            game.start();
        }
    }

    @Override
    public void close() throws Exception {
        serverSocket.close();
    }
}
