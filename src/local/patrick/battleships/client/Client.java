package local.patrick.battleships.client;

import local.patrick.battleships.common.commands.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static local.patrick.battleships.common.Constants.*;

public class Client implements AutoCloseable{
    private final String targetAddress;
    private final int targetPort;
    private Socket socket;


    public Client(String targetAddress, int targetPort) {
        this.targetAddress = targetAddress;
        this.targetPort = targetPort;
    }

    /**
     * Connects to the server and handles user input
     */
    public void run() throws IOException {
        socket = new Socket(targetAddress, targetPort);
        Thread listeningThread = new Thread(this::listen);
        listeningThread.start();


        try (var out = new PrintWriter(socket.getOutputStream(), true)) {
            var inLine = "";
            var stdin = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("? for current playing fields, q to quit game, CBDS for Carrier Battleship Destroyer Submarine");
                System.out.println("F to initiate firing at the opponent");
                inLine = stdin.readLine();
                switch (inLine) {
                    case "?" -> out.println(GetFieldCommand.PREFIX);
                    case "C", "D", "S", "B" -> {
                        var command = createShipCommand(inLine, stdin);
                        out.println(command.serialize());
                    }
                    case "F" -> {
                        var command = shootAt(stdin);
                        out.println(command.serialize());
                    }
                    case "q" -> {
                        out.println(QuitGameCommand.PREFIX);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Parses user input into a createShipCommand
     * @param initial First letter of the input
     * @param stdin User input
     * @return Always a createShipCommand
     */
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

        int row = getRow(stdin);
        int column = getColumn(stdin);
        return new PlaceShipCommand(column, row, orientation, type);
    }

    /**
     * Creates a FireAtCommand from user input
     * @param stdin user input
     * @return Always a FireAtCommand based on user input
     */
    private FireAtCommand shootAt(BufferedReader stdin) throws IOException {
        var row = getRow(stdin);
        var column = getColumn(stdin);
        return new FireAtCommand(column, row);
    }

    /**
     * Gets a column between 0 and the max column
     * @param stdin user input
     * @return an integer in the playing field width
     */
    private int getColumn(BufferedReader stdin) throws IOException {
        String input;
        int column = -1;
        while (column <= -1 || column > MAX_COLUMNS) {
            System.out.println("Enter column 0-" + (MAX_COLUMNS - 1));
            input = stdin.readLine();
            try {
                column = Integer.parseInt(input);
            } catch (NumberFormatException ignored) {
            }
        }
        return column;
    }

    /**
     * Gets a row between 0 and the max row
     * @param stdin user input
     * @return an integer between 0 and max row
     */
    private int getRow(BufferedReader stdin) throws IOException {
        String input = "";
        int row;
        while ((row = rowToInt(input)) <= -1 || row > MAX_ROWS) {
            System.out.println("Enter Row A - J");
            input = stdin.readLine();
        }
        return row;
    }

    /**
     * Indiscriminately prints all messages from the server to the User
     */
    private void listen() {
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            var inputLine = "";
            while ((inputLine = in.readLine()) != null) {
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

    @Override
    public void close() throws Exception {
        if (!socket.isClosed())
            socket.close();
    }
}
