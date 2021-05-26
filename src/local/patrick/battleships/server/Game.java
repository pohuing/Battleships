package local.patrick.battleships.server;

import local.patrick.battleships.common.TooManyShipsException;
import local.patrick.battleships.common.commands.*;

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
    private Player activePlayer = null;

    public Game(Socket socket, Socket socket2) throws IOException {
        System.out.println("Creating match");
        socket.setKeepAlive(true);
        socket2.setKeepAlive(true);
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
                    continue;
                }
                try {
                    player.playingField.placeShip((PlaceShipCommand) command.command);
                } catch (IllegalStateException | IndexOutOfBoundsException | TooManyShipsException e) {
                    player.outgoingQueue.add(new InformationCommand(e.getMessage()));
                }
                player.outgoingQueue.add(new InformationCommand(player.playingField.toAllyString()));

                if (player.playingField.isComplete() && opponent.playingField.isComplete()) {
                    System.out.println("Game has switched to Battle phase");
                    gamePhase = GamePhase.BATTLE;
                    activePlayer = opponent;
                    player.outgoingQueue.add(new InformationCommand("Both players have finished preparation, battle begins!"));
                    opponent.outgoingQueue.add(new InformationCommand("Both players have finished preparation, battle begins!"));
                    player.outgoingQueue.add(new InformationCommand("It is the opponent's turn to shoot"));
                    opponent.outgoingQueue.add(new InformationCommand("It is your turn to shoot"));
                }
            } else if (command.command instanceof FireAtCommand) {
                // Discord invalid input
                if (gamePhase != GamePhase.BATTLE) {
                    player.outgoingQueue.add(new InformationCommand("This can only be done during the battle phase"));
                    continue;
                }

                if (activePlayer.tag == command.player) {
                    try {
                        var result = opponent.playingField.fireOnSpot((FireAtCommand) command.command);
                        player.outgoingQueue.add(new InformationCommand(result.toString()));
                        player.outgoingQueue.add(new InformationCommand(opponent.playingField.toOpponentString()));
                        opponent.outgoingQueue.add(new InformationCommand(result.toString()));
                        opponent.outgoingQueue.add(new InformationCommand(opponent.playingField.toAllyString()));

                        // Did this sink the final ship?
                        // Transition into post battle phase
                        if (opponent.playingField.hasLost()) {
                            gamePhase = GamePhase.FINISHED;
                            player.outgoingQueue.add(new InformationCommand("You have hit the final ship, you've won!"));
                            opponent.outgoingQueue.add(new InformationCommand("Your final ship has been sunk, you've lost!"));
                        }
                        activePlayer = opponent;
                    } catch (IndexOutOfBoundsException e) {
                        player.outgoingQueue.add(new InformationCommand(e.getMessage()));
                    } catch (UnknownError e) {
                        e.printStackTrace();
                    }
                } else {
                    player.outgoingQueue.add(new InformationCommand("It is not your turn to fire"));
                }
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
