package local.patrick.battleships.common;

import java.util.Arrays;
import java.util.List;

public class Constants {
    // Column width of a playing field
    public final static int MAX_COLUMNS = 10;
    // Row height of a playing field, only up to 10 is supported as increasing this is non trivial due to representation
    // into alphabetic rows
    public final static int MAX_ROWS = 10;
    // Default port to run server and client on
    public final static int DEFAULT_PORT = 5555;
    // Game rules defining the max amount of ship types
    public final static int MAX_CARRIERS = 1, MAX_BATTLESHIPS = 2, MAX_DESTROYERS = 3, MAX_SUBMARINES = 4;
    // Game rules defining the size of a ship type
    public final static int CARRIER_SIZE = 4, BATTLESHIP_SIZE = 3, DESTROYER_SIZE = 2, SUBMARINE_SIZE = 1;

    private final static List<String> allowedRows = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");

    /**
     * Helper method to turn a string into a row index
     */
    public static int rowToInt(String textual){
        return allowedRows.indexOf(textual);
    }

    /**
     * Helper to turn a row index into a textual representation
     */
    public static String intToRow(int raw){
        return allowedRows.get(raw);
    }
}
