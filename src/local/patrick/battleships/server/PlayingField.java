package local.patrick.battleships.server;

import local.patrick.battleships.common.Constants;
import local.patrick.battleships.common.TooManyShipsException;
import local.patrick.battleships.common.commands.FireAtCommand;
import local.patrick.battleships.common.commands.PlaceShipCommand;

import java.util.HashMap;

import static local.patrick.battleships.common.Constants.*;

/**
 * A PlayingField contains the state of a PlayingField's ships and bomb results
 */
public class PlayingField {
    /** .get(column).get(row) */
    private final HashMap<Integer, HashMap<Integer, Spot>> field = new HashMap<>();
    /** Tracks the amount of ships currently placed during the Preparation phase */
    private final HashMap<PlaceShipCommand.Type, Integer> shipCount = new HashMap<>();

    public PlayingField() {
        // Filling in the playing field with Empty spots
        for (int column = 0; column < Constants.MAX_COLUMNS; column++) {
            field.put(column, new HashMap<>());
            for (int row = 0; row < Constants.MAX_COLUMNS; row++) {
                field.get(column).put(row, Spot.EMPTY);
            }
        }

        // Initializing the ship counts
        for (var type : PlaceShipCommand.Type.values()) {
            shipCount.put(type, 0);
        }
    }

    /**
     * Tries to place a ship on the playing field
     * @throws IllegalStateException If area is already occupied by another ship
     * @throws TooManyShipsException If the maximum amount of ships of the type have already been placed
     * @throws IndexOutOfBoundsException If the ship would be outside the playing area with any of it's segments
     */
    public void placeShip(PlaceShipCommand command) throws IllegalStateException, TooManyShipsException, IndexOutOfBoundsException {
        var maxship = switch (command.type) {
            case Carrier -> Constants.MAX_CARRIERS;
            case Battleship -> Constants.MAX_BATTLESHIPS;
            case Destroyer -> Constants.MAX_DESTROYERS;
            case Submarine -> Constants.MAX_SUBMARINES;
        };
        if (shipCount.get(command.type) >= maxship) {
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
        var oldCount = shipCount.get(command.type);
        shipCount.put(command.type, oldCount + 1);
    }

    /**
     * Fires a shot at the spot in command and returns the new state of the spot
     * @return Feedback of shot
     * @throws IndexOutOfBoundsException if the spot is not in the playing field
     */
    public Spot fireOnSpot(FireAtCommand command) throws IndexOutOfBoundsException {
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
        // All switch branches return and the switch is exhaustive of Spot, there should be no way to ever reach this
        // but Javac thinks it's possible so here's an exception to prove to the compiler this function will either
        // return something or throw
        throw new UnknownError("This section shouldn't be reachable");
    }

    /**
     * Checks if the sum of placed ships equals the sum of the Maxima
     * @return true if all ships have been placed
     */
    public Boolean isComplete() {
        return shipCount.values().stream().mapToInt(i -> i).sum() == MAX_BATTLESHIPS + MAX_CARRIERS + MAX_DESTROYERS + MAX_SUBMARINES;
    }

    /**
     * Losing is defined as having no ship segments left
     * @return true if no ship segments are left
     */
    public Boolean hasLost(){
        return field.values().stream().noneMatch(row -> row.containsValue(Spot.SHIP));
    }

    /**
     * Checks if a ship to be placed won't exceed the boundaries as defined in Constants
     * @return true if ship will never exceed the boundaries
     */
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

    /**
     * Checks if a coordinate is within the boundaries as defined in Constants
     * @return true if a coordinate is within the boundaries
     */
    private boolean isSpotInField(int column, int row) {
        return column < MAX_COLUMNS && column >= 0 &&
                row < MAX_ROWS && row >= 0;
    }

    /**
     * Checks all spots that would be taken up by a ship to be placed are Empty
     * @return true if all spots are free, false if the ship leaves the playing field or a spot is already taken
     */
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

        temp.append("Carriers: ").append(shipCount.get(PlaceShipCommand.Type.Carrier))
                .append(" Battleships: ").append(shipCount.get(PlaceShipCommand.Type.Battleship))
                .append(" Destroyers: ").append(shipCount.get(PlaceShipCommand.Type.Destroyer))
                .append(" Submarines: ").append(shipCount.get(PlaceShipCommand.Type.Submarine))
                .append("\n");

        temp.append("XX");
        for (int i = 0; i < Constants.MAX_COLUMNS; i++) {
            temp.append(String.format(" %02d", i));
        }

        for (int row = 0; row < Constants.MAX_ROWS; row++) {
            temp.append("\n");
            temp.append(intToRow(row));
            temp.append(" ");
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

    /**
     * Hides ships that have not been hit yet
     */
    public String toOpponentString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Carriers: ").append(shipCount.get(PlaceShipCommand.Type.Carrier))
                .append(" Battleships: ").append(shipCount.get(PlaceShipCommand.Type.Battleship))
                .append(" Destroyers: ").append(shipCount.get(PlaceShipCommand.Type.Destroyer))
                .append(" Submarines: ").append(shipCount.get(PlaceShipCommand.Type.Submarine))
                .append("\n");

        stringBuilder.append("XX");
        for (int i = 0; i < Constants.MAX_COLUMNS; i++) {
            stringBuilder.append(String.format(" %02d", i));
        }

        for (int row = 0; row < Constants.MAX_ROWS; row++) {
            stringBuilder.append("\n");
            stringBuilder.append(intToRow(row));
            stringBuilder.append(" ");
            for (int column = 0; column < Constants.MAX_COLUMNS; column++) {
                switch (field.get(column).get(row)) {
                    case EMPTY, SHIP -> stringBuilder.append("   ");
                    case MISS -> stringBuilder.append(" OO");
                    case HIT -> stringBuilder.append(" XX");
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Shows full information about the playing field in human readable format
     */
    public String toAllyString() {
        return toString();
    }
}
