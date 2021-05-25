package local.patrick.battleships.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ServerGateway implements AutoCloseable {
    private final ServerSocket serverSocket;
    private final List<Game> activeGames = new ArrayList<>();

    public ServerGateway(Integer port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        System.out.println("Starting ServerGateway on port " + port);
    }

    public void run() throws IOException {
        while (true) {
            var socket = serverSocket.accept();
            System.out.println("Got a first client");
            var socket2 = serverSocket.accept();
            System.out.println("Got a second client, creating new game");
            var game = new Game(socket, socket2);
            activeGames.add(game);
            game.start();
        }
    }

    @Override
    public void close() throws Exception {
        serverSocket.close();
    }
}
