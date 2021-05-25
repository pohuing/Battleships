package local.patrick.battleships.common;

import java.util.HashMap;

public class PlayingField {
    // [column][row]
    private final HashMap<Integer, HashMap<Integer, Spot>> field;
    private Integer Carriers, Battleships, Destroyers, Submarines, Patrols;

    public PlayingField() {
        field = new HashMap<>();
        for (int i = 0; i < Constants.MAX_COLUMNS; i++) {
            field.put(i, new HashMap<>());
            for (int j = 0; j < Constants.MAX_COLUMNS; j++) {
                field.get(i).put(j, Spot.EMPTY);
            }
        }
    }

    public void placeShip(PlaceShipCommand command) {
        int columnVector, rowVector;
        switch (command.orientation) {
            case LEFT -> {
                columnVector = -1;
                rowVector = 0;
            }
            case RIGHT -> {
                columnVector = 1;
                rowVector = 0;
            }
            case UP -> {
                columnVector = 0;
                rowVector = 1;
            }
            case DOWN -> {
                columnVector = 0;
                rowVector = -1;
            }
            default -> throw new IllegalStateException("Unexpected value: " + command.orientation);
        }

        var curColumn = command.column;
        var curRow = command.row;
        for (int i = 0; i < command.type.size; i++) {
            field.get(curColumn).put(curRow, Spot.SHIP);
            curColumn += columnVector;
            curRow += rowVector;
        }
    }

    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder("XX");
        for (int i = 0; i < Constants.MAX_COLUMNS; i++) {
            temp.append(String.format(" %02d", i));
        }

        for (int row = 0; row < Constants.MAX_ROWS; row++) {
            temp.append("\n");
            temp.append(String.format("%02d", row));
            for (int column = 0; column < Constants.MAX_COLUMNS; column++) {
                switch (field.get(column).get(row)) {
                    case EMPTY -> temp.append("   ");
                    case SHIP -> temp.append(" ==");
                    case MISS -> temp.append(" OO");
                    case HIT -> temp.append(" XX");
                }
            }
        }

        return temp.toString();
    }

    /*
    Hides the Ship positions
     */
    public String toOpponentString() {
        StringBuilder temp = new StringBuilder("XX");
        for (int column = 0; column < Constants.MAX_COLUMNS; column++) {
            temp.append(String.format(" %02d", column));
        }

        for (int row = 0; row < Constants.MAX_ROWS; row++) {
            temp.append("\n");
            temp.append(String.format("%02d", row));
            for (int column = 0; column < Constants.MAX_COLUMNS; column++) {
                switch (field.get(column).get(row)) {
                    case EMPTY, SHIP -> temp.append("   ");
                    case MISS -> temp.append(" OO");
                    case HIT -> temp.append(" XX");
                }
            }
        }
        return temp.toString();
    }

    public String toAllyString() {
        return toString();
    }
}
