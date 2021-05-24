package local.patrick.battleships.server;

import local.patrick.battleships.common.Command;
import local.patrick.battleships.common.PlaceBombCommand;
import local.patrick.battleships.common.PlayingField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class holds all necessary resources for running a game from start to finish
 */
public class Game implements AutoCloseable {
    private final Socket playerOneSocket, playerTwoSocket;
    private final PlayingField playerOneField = new PlayingField(),
            playerTwoField = new PlayingField();

    public Game(Socket socket, Socket socket2) throws IOException {
        this.playerOneSocket = socket;
        this.playerTwoSocket = socket2;
    }

    public void run() {
        try (var in = new BufferedReader(new InputStreamReader(playerOneSocket.getInputStream()));
             var out = new PrintWriter(playerOneSocket.getOutputStream(), true)) {

            String inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Got line from Client: " + inputLine);
                processLine(inputLine);
                inputLine = "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void processLine(String line) {
        try {
            var deserialized = Command.deserialize(line);
            if (deserialized instanceof PlaceBombCommand) {
                var downcast = ((PlaceBombCommand) deserialized);

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void close() throws Exception {
        playerOneSocket.close();
    }
}
