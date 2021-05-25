package local.patrick.battleships.client;

import local.patrick.battleships.common.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Client {
    private final String targetAddress;
    private final int targetPort;
    private Socket socket;


    public Client(String targetAddress, int targetPort) {
        this.targetAddress = targetAddress;
        this.targetPort = targetPort;
    }

    public void run() throws IOException {
        socket = new Socket(targetAddress, targetPort);
        Thread listeningThread = new Thread(this::listen);
        listeningThread.start();


        try (var out = new PrintWriter(socket.getOutputStream(), true)) {
            var inLine = "";
            var buffread = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("? for current playing fields, q to quit game, CDSB for Carrier Destroyer Submarine Battleship");
                inLine = buffread.readLine();
                switch (inLine) {
                    case "?":
                        out.println(GetFieldCommand.PREFIX);
                        break;
                    case "C", "D", "S", "B":
                        var command = createShipCommand(inLine, buffread);
                        out.println(command.serialize());
                        break;
                    case "q":
                        out.println(QuitGameCommand.PREFIX);
                        return;
                }
            }
        }
    }

    private PlaceShipCommand createShipCommand(String initial, BufferedReader stdin) throws IOException {
        var type = switch (initial) {
            case "C" -> PlaceShipCommand.Type.Carrier;
            case "D" -> PlaceShipCommand.Type.Destroyer;
            case "S" -> PlaceShipCommand.Type.Submarine;
            case "B" -> PlaceShipCommand.Type.Battleship;
            default -> throw new IllegalStateException("Unexpected value: " + initial);
        };

        var input = "";
        PlaceShipCommand.Orientation orientation = null;
        while (null == orientation) {
            System.out.println("Enter a direction initial letter D U L R");
            input = stdin.readLine();
            orientation = switch (input) {
                case "D" -> PlaceShipCommand.Orientation.DOWN;
                case "U" -> PlaceShipCommand.Orientation.UP;
                case "L" -> PlaceShipCommand.Orientation.LEFT;
                case "R" -> PlaceShipCommand.Orientation.RIGHT;
                default -> null;
            };
        }
        input = "";
        List<String> allowedRows = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        int row = -1;
        while ((row = allowedRows.indexOf(input)) <= -1) {
            System.out.println("Enter Row A - J");
            input = stdin.readLine();
        }
        input = "";
        int column = -1;
        while (column <= -1){
            System.out.println("Enter column 0-9");
            input = stdin.readLine();
            try{
                column = Integer.parseInt(input);
            }catch(NumberFormatException ignored){
            }
        }
        return new PlaceShipCommand(column, row, orientation, type);
    }

    private void listen() {
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            var inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                //System.out.println(inputLine);
                try {
                    var deserialized = Command.deserialize(inputLine);
                    if (deserialized instanceof InformationCommand) {
                        System.out.println(((InformationCommand) deserialized).message);
                    }
                    if (deserialized instanceof QuitGameCommand) {
                        System.out.println("The other player quit");
                        return;
                    }
                } catch (InstantiationException e) {
                    System.out.println("Got unparseable message from server: " + inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from Server");
        }
        System.out.println("Client listen exiting nominally");
        System.out.println("Enter q to quit");
    }
}
