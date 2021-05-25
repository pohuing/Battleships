package local.patrick.battleships.client;

import local.patrick.battleships.common.Command;
import local.patrick.battleships.common.Constants;
import local.patrick.battleships.common.GetFieldCommand;
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Client listen exiting nominally");
    }
}
