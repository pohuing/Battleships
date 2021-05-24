package local.patrick.battleships.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ServerGateway implements AutoCloseable {
    private ServerSocket serverSocket;
    private List<Game> activeGames = new ArrayList<>();

    public ServerGateway(Integer port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        System.out.println("Starting ServerGateway on port " + port);
    }

    public void run() throws IOException {
        while (true) {
            var socket = serverSocket.accept();
            var socket2 = serverSocket.accept();
            var game = new Game(socket);
            activeGames.add(game);
            game.run();
        }
    }

    @Override
    public void close() throws Exception {
        for (var game : activeGames) {
            game.close();
        }
    }
}
