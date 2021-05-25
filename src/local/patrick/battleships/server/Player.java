package local.patrick.battleships.server;

import local.patrick.battleships.common.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

class Player {
    private final Socket socket;
    private final Thread sendingThread, receivingThread;
    // A buffer for writing things to the respective client
    public final ConcurrentLinkedQueue<String> outgoingQueue = new ConcurrentLinkedQueue<>();
    // Queue for reporting back information to Game object
    private final LinkedBlockingQueue<PlayerCommand> feedbackChannel;
    public final PlayerTag tag;

    Player(Socket socket, LinkedBlockingQueue<PlayerCommand> feedbackChannel, PlayerTag tag) {
        this.socket = socket;
        this.feedbackChannel = feedbackChannel;
        this.tag = tag;

        sendingThread = new Thread(this::send);
        receivingThread = new Thread(this::listen);
    }

    void start(){
        System.out.println(tag + ": Starting communication threads");
        System.out.println(tag + ": Socket closed?:" + socket.isClosed());

        sendingThread.start();
        receivingThread.start();
    }

    void listen() {
        System.out.println(tag + ": in listen, socket closed?: " + socket.isClosed());
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            var inputLine = "";
            while(!socket.isClosed()) {
                // TODO fix busy poll
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Got message from Player " + tag + ": " + inputLine);
                    feedbackChannel.add(new PlayerCommand(tag, Command.deserialize(inputLine)));
                }
            }
        } catch (IOException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    void send() {
        System.out.println(tag + ": in send, socket closed?: " + socket.isClosed());
        outgoingQueue.add("Hello World to " + tag);
        try (var out = new PrintWriter(socket.getOutputStream(), true)) {
            while(!socket.isClosed()){
                var outline = outgoingQueue.poll();
                if (outline == null)
                    continue; // TODO fix busy poll
                out.println(outline);
                System.out.println("Sent message to Player " + tag + ": " + outline);

            }
            System.out.println(tag + " send thread has terminated nominally");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
