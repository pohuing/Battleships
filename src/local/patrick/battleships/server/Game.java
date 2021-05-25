package local.patrick.battleships.server;

import local.patrick.battleships.common.GetFieldCommand;
import local.patrick.battleships.common.PlayingField;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class holds all necessary resources for running a game from start to finish
 */
public class Game implements Runnable{
    private final Player playerOne, playerTwo;
    private final PlayingField playerOneField = new PlayingField(),
            playerTwoField = new PlayingField();
    private final LinkedBlockingQueue<PlayerCommand> playerMessages = new LinkedBlockingQueue<>();
    private final Thread thread;

    public Game(Socket socket, Socket socket2) throws IOException {
        System.out.println("Creating match");
        playerOne = new Player(socket, playerMessages, PlayerTag.One);
        playerTwo = new Player(socket2, playerMessages, PlayerTag.Two);
        thread = new Thread(this);
    }

    // Runs through player messages sequentially
    void processMessages() throws InterruptedException {
        // process playerMessages forever till game finished
        PlayerCommand command;
        while((command = playerMessages.take()) != null){
            if (command.command instanceof GetFieldCommand){
                if (command.player == PlayerTag.One){
                    playerOne.outgoingQueue.add(playerOneField.toAllyString());
                    playerOne.outgoingQueue.add(playerTwoField.toOpponentString());
                }else if(command.player == PlayerTag.Two){
                    playerTwo.outgoingQueue.add(playerTwoField.toAllyString());
                    playerTwo.outgoingQueue.add(playerOneField.toOpponentString());
                }
            }
        }
    }

    public void start(){
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
    }
}
