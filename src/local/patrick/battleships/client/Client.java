package local.patrick.battleships.client;

import local.patrick.battleships.common.*;
import local.patrick.battleships.server.PlayerCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private final String targetAddress;
    private Thread listeningThread;
    private Socket socket;



    public Client(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public void run() throws IOException {
        socket = new Socket("localhost", Constants.PORT);
        listeningThread = new Thread(this::listen);
        listeningThread.start();


        try(var out = new PrintWriter(socket.getOutputStream(), true)){
            var inLine = "";
            var buffread = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                inLine = buffread.readLine();
                switch (inLine){
                    case "?":
                        out.println(GetFieldCommand.PREFIX);
                        break;
                    case "q":
                        out.println(QuitGameCommand.PREFIX);
                        return;
                }
            }
        }
    }

    private void listen(){
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            var inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                try {
                    var deserialized = Command.deserialize(inputLine);
                    if (deserialized instanceof InformationCommand) {
                        System.out.println(((InformationCommand)deserialized).message);
                    }if (deserialized instanceof QuitGameCommand){
                        System.out.println("The other player quit");
                        return;
                    }
                }catch (InstantiationException e){
                    System.out.println("Got unparseable message from server: " + inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from Server");
        }         System.out.println("Client listen exiting nominally");
        System.out.println("Enter q to quit");
    }
}
