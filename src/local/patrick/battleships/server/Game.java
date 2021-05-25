package local.patrick.battleships.server;

import local.patrick.battleships.common.*;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class holds all necessary resources for running a game from start to finish
 */
public class Game implements Runnable {
    private final Player playerOne, playerTwo;
    private final LinkedBlockingQueue<PlayerCommand> playerMessages = new LinkedBlockingQueue<>();
    private final Thread thread;
    private GamePhase gamePhase = GamePhase.PREPARATION;

    public Game(Socket socket, Socket socket2) throws IOException {
        System.out.println("Creating match");
        playerOne = new Player(socket, playerMessages, PlayerTag.One);
        playerTwo = new Player(socket2, playerMessages, PlayerTag.Two);
        thread = new Thread(this);
    }

    // Runs through player messages sequentially
    void processMessages() throws InterruptedException {
        // process playerMessages forever till game finished
        while (true) {
            PlayerCommand command = playerMessages.take();
            var player = switch (command.player) {
                case One -> playerOne;
                case Two -> playerTwo;
            };
            var opponent = switch (command.player) {
                case One -> playerTwo;
                case Two -> playerOne;
            };

            if (command.command instanceof GetFieldCommand) {
                player.outgoingQueue.add(new InformationCommand(player.playingField.toAllyString()));
                player.outgoingQueue.add(new InformationCommand(opponent.playingField.toOpponentString()));
            } else if (command.command instanceof QuitGameCommand) {
                playerOne.outgoingQueue.add(command.command);
                playerTwo.outgoingQueue.add(command.command);
                break;
            } else if (command.command instanceof PlaceShipCommand) {
                if (gamePhase != GamePhase.PREPARATION) {
                    player.outgoingQueue.add(new InformationCommand("This can only be done during the preparation phase"));
                }
                player.playingField.placeShip((PlaceShipCommand) command.command);
                player.outgoingQueue.add(new InformationCommand(player.playingField.toAllyString()));
            }
        }
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        playerOne.start();
        playerTwo.start();
        try {
            processMessages();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Ending game thread");
    }
}
