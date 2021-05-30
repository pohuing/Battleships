package local.patrick.battleships.server;

import local.patrick.battleships.common.commands.Command;
import local.patrick.battleships.common.commands.InformationCommand;
import local.patrick.battleships.common.commands.QuitGameCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A Player holds all information necessary to facilitate communications with a client including playing field state
 */
class Player {
    private final Socket socket;
    private final Thread sendingThread, receivingThread;
    /** A buffer for writing things to the respective client */
    public final LinkedBlockingQueue<Command> outgoingQueue = new LinkedBlockingQueue<>();
    /** Queue for reporting back information to Game object */
    private final LinkedBlockingQueue<PlayerCommand> feedbackChannel;
    public final PlayerTag tag;
    public final PlayingField playingField = new PlayingField();

    Player(Socket socket, LinkedBlockingQueue<PlayerCommand> feedbackChannel, PlayerTag tag) {
        this.socket = socket;
        this.feedbackChannel = feedbackChannel;
        this.tag = tag;

        sendingThread = new Thread(this::send);
        receivingThread = new Thread(this::listen);
    }

    /**
     * Starts up communications threads
     */
    void start(){
        System.out.println(tag + ": Starting communication threads");
        System.out.println(tag + ": Socket closed?:" + socket.isClosed());

        sendingThread.start();
        receivingThread.start();
    }

    /**
     * Listening part of connection
     * Stops when Socket dies
     */
    void listen() {
        System.out.println(tag + ": in listen, socket closed?: " + socket.isClosed());
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            while(!socket.isClosed()) {
                var inputLine = "";
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Got message from Player " + tag + ": " + inputLine);
                    try{
                        feedbackChannel.add(new PlayerCommand(tag, Command.deserialize(inputLine)));
                    }catch (InstantiationException e){
                        System.out.println(tag + ": Failed to parse command: " + inputLine);
                    }
                }
            }
            System.out.println(tag + ": listen thread has exited nominally");
        } catch (IOException e) {
            if (e instanceof SocketException)
                System.out.println(tag + ": listen thread terminated with socket closed");
            else
                e.printStackTrace();
        }
    }

    /**
     * Sending part of socket
     * Serializes Commands from the outgoingQueue and sends them on the socket
     * Closes socket if QuitGameCommand is encountered after sending it to client
     */
    void send() {
        System.out.println(tag + ": in send, socket closed?: " + socket.isClosed());
        outgoingQueue.add(new InformationCommand("Hello World to " + tag));

        try (var out = new PrintWriter(socket.getOutputStream(), true)) {
            Command outline;
            // Take a command off the queue and send it out
            while((outline = outgoingQueue.take()) != null && !socket.isClosed()){
                out.println(outline.serialize());
                System.out.println(tag + ": Sent message to Player " + tag + ": " + outline);
                if (outline instanceof QuitGameCommand)
                    break;
            }
            System.out.println(tag + ": send thread has terminated nominally");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
