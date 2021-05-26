package local.patrick.battleships.common;

import java.util.HashMap;

import static local.patrick.battleships.common.Constants.*;

public class PlayingField {
    // [column][row]
    private final HashMap<Integer, HashMap<Integer, Spot>> field;
    private final HashMap<PlaceShipCommand.Type, Integer> shipcount = new HashMap<>();

    public PlayingField() {
        field = new HashMap<>();
        for (int column = 0; column < Constants.MAX_COLUMNS; column++) {
            field.put(column, new HashMap<>());
            for (int row = 0; row < Constants.MAX_COLUMNS; row++) {
                field.get(column).put(row, Spot.EMPTY);
            }
        }

        for (var type : PlaceShipCommand.Type.values()) {
            shipcount.put(type, 0);
        }
    }

    public void placeShip(PlaceShipCommand command) throws IllegalStateException, TooManyShipsException, IndexOutOfBoundsException {
        var maxship = switch (command.type) {
            case Carrier -> Constants.MAX_CARRIERS;
            case Battleship -> Constants.MAX_BATTLESHIPS;
            case Destroyer -> Constants.MAX_DESTROYERS;
            case Submarine -> Constants.MAX_SUBMARINES;
        };
        if (shipcount.get(command.type) >= maxship) {
            throw new TooManyShipsException("Exceeded max amount of " + command.type);
        }
        if (!isCommandInField(command)) {
            throw new IndexOutOfBoundsException("The ship will partly or fully be outside the playing field");
        }
        if (!isSpaceAvailable(command)) {
            throw new IllegalStateException("This area is already taken by a ship");
        }


        var curColumn = command.column;
        var curRow = command.row;
        for (int i = 0; i < command.type.size; i++) {
            field.get(curColumn).put(curRow, Spot.SHIP);
            curColumn += command.orientation.columnVector;
            curRow += command.orientation.rowVector;
        }
        var oldCount = shipcount.get(command.type);
        shipcount.put(command.type, oldCount + 1);
    }

    /**
     * Fires a shot at the spot in command and returns the new state of the spot
     * @param command
     * @return
     * @throws IndexOutOfBoundsException
     */
    public Spot fireOnSpot(PlaceBombCommand command) throws Exception {
        if(!isSpotInField(command.column, command.row))
            throw new IndexOutOfBoundsException("This spot is not in the playing field");

        switch (field.get(command.column).get(command.row)) {
            case EMPTY -> {
                field.get(command.column).put(command.row, Spot.MISS);
                return Spot.MISS;
            }
            case SHIP -> {
                field.get(command.column).put(command.row, Spot.HIT);
                return Spot.HIT;
            }
            case MISS -> {
                return Spot.MISS;
            }
            case HIT -> {
                return Spot.HIT;
            }
        }
        throw new Exception("This section shouldn't be reachable");
    }

    public Boolean isComplete() {
        return shipcount.values().stream().mapToInt(i -> i).sum() == MAX_BATTLESHIPS + MAX_CARRIERS + MAX_DESTROYERS + MAX_SUBMARINES;
    }

    private boolean isCommandInField(PlaceShipCommand command) {
        // Are base coordinates in playing field
        if (!isSpotInField(command.column, command.row))
            return false;

        // Where is the farthest away from base segment of ship
        var greatestColumnExtent = command.column + command.orientation.columnVector * command.type.size;
        var greatestRowExtent = command.row + command.orientation.rowVector * command.type.size;

        // Are they in the playing field still
        if (greatestColumnExtent < 0 || greatestColumnExtent > Constants.MAX_COLUMNS) {
            return false;
        }
        return greatestRowExtent >= 0 && greatestRowExtent <= Constants.MAX_ROWS;
    }

    private boolean isSpotInField(int column, int row) {
        return column < MAX_COLUMNS && column >= 0 &&
                row < MAX_ROWS && row >= 0;
    }

    private boolean isSpaceAvailable(PlaceShipCommand command) {
        var curColumn = command.column;
        var curRow = command.row;
        for (int i = 0; i < command.type.size; i++) {
            try {
                if (field.get(curColumn).get(curRow) == Spot.SHIP)
                    return false;
                curColumn += command.orientation.columnVector;
                curRow += command.orientation.rowVector;
            } catch (NullPointerException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();

        temp.append("Carriers: ").append(shipcount.get(PlaceShipCommand.Type.Carrier))
                .append(" Battleships: ").append(shipcount.get(PlaceShipCommand.Type.Battleship))
                .append(" Destroyers: ").append(shipcount.get(PlaceShipCommand.Type.Destroyer))
                .append(" Submarines: ").append(shipcount.get(PlaceShipCommand.Type.Submarine))
                .append("\n");

        temp.append("XX");
        for (int i = 0; i < Constants.MAX_COLUMNS; i++) {
            temp.append(String.format(" %02d", i));
        }

        for (int row = 0; row < Constants.MAX_ROWS; row++) {
            temp.append("\n");
            temp.append(intToRow(row));
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
        StringBuilder temp = new StringBuilder();

        temp.append("Carriers: ").append(shipcount.get(PlaceShipCommand.Type.Carrier))
                .append(" Battleships: ").append(shipcount.get(PlaceShipCommand.Type.Battleship))
                .append(" Destroyers: ").append(shipcount.get(PlaceShipCommand.Type.Destroyer))
                .append(" Submarines: ").append(shipcount.get(PlaceShipCommand.Type.Submarine))
                .append("\n");

        temp.append("XX");
        for (int i = 0; i < Constants.MAX_COLUMNS; i++) {
            temp.append(String.format(" %02d", i));
        }

        for (int row = 0; row < Constants.MAX_ROWS; row++) {
            temp.append("\n");
            temp.append(intToRow(row));
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
