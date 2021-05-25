package local.patrick.battleships.server;

import local.patrick.battleships.common.PlayingField;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class holds all necessary resources for running a game from start to finish
 */
public class Game implements Runnable{
    private final Player playerA, playerB;
    private final PlayingField playerOneField = new PlayingField(),
            playerTwoField = new PlayingField();
    private final ConcurrentLinkedQueue<PlayerCommand> playerMessages = new ConcurrentLinkedQueue<>();
    private final Thread thread;

    public Game(Socket socket, Socket socket2) throws IOException {
        System.out.println("Creating match");
        playerA = new Player(socket, playerMessages, PlayerTag.One);
        playerB = new Player(socket2, playerMessages, PlayerTag.Two);
        thread = new Thread(this);
    }

    // Runs through player messages sequentially
    void processMessages(){

    }

    public void start(){
        thread.start();
    }

    @Override
    public void run() {
        playerA.start();
        playerB.start();
    }
}
