package local.patrick.battleships.common;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public final static int MAX_COLUMNS = 10;
    public final static int MAX_ROWS = 10;
    public final static int DEFAULT_PORT = 5555;
    public final static int MAX_CARRIERS = 1, MAX_BATTLESHIPS = 2, MAX_DESTROYERS = 3, MAX_SUBMARINES = 4;
    public final static int CARRIER_SIZE = 4, BATTLESHIP_SIZE = 3, DESTROYER_SIZE = 2, SUBMARINE_SIZE = 1;

    private final static List<String> allowedRows = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");

    public static int rowToInt(String textual){
        return allowedRows.indexOf(textual);
    }

    public static String intToRow(int raw){
        return allowedRows.get(raw);
    }
}
