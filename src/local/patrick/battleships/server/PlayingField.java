package local.patrick.battleships.server;

import local.patrick.battleships.common.Constants;
import local.patrick.battleships.common.TooManyShipsException;
import local.patrick.battleships.common.commands.FireAtCommand;
import local.patrick.battleships.common.commands.PlaceShipCommand;

import java.util.*;
import java.util.stream.Collectors;

import static local.patrick.battleships.common.Constants.*;

/**
 * A PlayingField contains the state of a PlayingField's ships and bomb results
 */
public class PlayingField {
    /**
     * .get(column).get(row)
     */
    private final HashSet<Ship> ships = new HashSet<>();
    private final List<Miss> misses = new ArrayList<>();
    /**
     * Tracks the amount of ships currently placed during the Preparation phase
     */
    private final HashMap<PlaceShipCommand.Type, Integer> shipCount = new HashMap<>();

    public PlayingField() {
        // Initializing the ship counts
        for (var type : PlaceShipCommand.Type.values()) {
            shipCount.put(type, 0);
        }
    }

    /**
     * Tries to place a ship on the playing field
     *
     * @throws IllegalStateException     If area is already occupied by another ship
     * @throws TooManyShipsException     If the maximum amount of ships of the type have already been placed
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


        ships.add(new Ship(command.orientation, command.type, command.column, command.row));
        var oldCount = shipCount.get(command.type);
        shipCount.put(command.type, oldCount + 1);
    }

    /**
     * Fires a shot at the spot in command and returns the new state of the spot
     *
     * @return Feedback of shot
     * @throws IndexOutOfBoundsException if the spot is not in the playing field
     */
    public SpotChange fireOnSpot(FireAtCommand command) throws IndexOutOfBoundsException {
        if (!isSpotInField(command.column, command.row))
            throw new IndexOutOfBoundsException("This spot is not in the playing field");

        var ship = getShipAt(command.column, command.row);
        if (ship.isPresent()) {
            try {
                return ship.get().firedAt(command.column, command.row);
            } catch (NotThisShipException e) {
                e.printStackTrace();
            }
        } else {
            var oldMisses = misses.stream().filter(miss -> miss.row == command.row && miss.column == command.column).collect(Collectors.toCollection(ArrayList::new));
            if (oldMisses.size() == 0) {
                misses.add(new Miss(command.column, command.row));
                return new SpotChange(Spot.EMPTY, Spot.MISS);
            } else if (oldMisses.size() == 1) {
                return new SpotChange(Spot.MISS, Spot.MISS);
            } else if (oldMisses.size() > 1) {
                throw new UnknownError("There should only ever one Miss in this section of code");
            }
        }
        throw new UnknownError("This section means there was another bug in looking up and handling shooting at ships");
    }


    /**
     * Checks if the sum of placed ships equals the sum of the Maxima
     *
     * @return true if all ships have been placed
     */
    public Boolean isComplete() {
        return shipCount.values().stream().mapToInt(i -> i).sum() == MAX_BATTLESHIPS + MAX_CARRIERS + MAX_DESTROYERS + MAX_SUBMARINES;
    }

    /**
     * Losing is defined as having no ship segments left
     *
     * @return true if no ship segments are left
     */
    public Boolean hasLost() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    /**
     * Checks if a ship to be placed won't exceed the boundaries as defined in Constants
     *
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
     *
     * @return true if a coordinate is within the boundaries
     */
    private boolean isSpotInField(int column, int row) {
        return column < MAX_COLUMNS && column >= 0 &&
                row < MAX_ROWS && row >= 0;
    }

    /**
     * Checks all spots that would be taken up by a ship to be placed are Empty
     *
     * @return true if all spots are free, false if a spot is already taken
     */
    private boolean isSpaceAvailable(PlaceShipCommand command) {
        return ships.stream().noneMatch(ship -> ship.isAt(command.column, command.row));
    }

    /**
     * Gets a Spot at a position
     */
    public Spot getSpotAt(int column, int row) {
        if (!isSpotInField(column, row))
            throw new IndexOutOfBoundsException("This spot is not in the playing field");

        var shipsAtHere = getShipAt(column, row);
        var missesAtHere = getMissAt(column, row);
        if (shipsAtHere.isPresent() && missesAtHere.isPresent())
            throw new UnknownError("This means somehow a position is occupied by both a Ship and a Miss, this should never happen");

        if (shipsAtHere.isPresent()) {
            return shipsAtHere.get().getSpotAt(column, row).get();
        } else if (missesAtHere.isPresent())
            return Spot.MISS;
        else
            return Spot.EMPTY;
    }

    /**
     * Tries to get a ship at a position
     *
     * @return Ship if given coordinates are occupied by a ship, None if not
     * @throws UnknownError If somehow two ships occupy the same position, this should not be possible
     */
    private Optional<Ship> getShipAt(int column, int row) {
        var shipsAtPosition = ships.stream().filter(ship -> ship.isAt(column, row)).collect(Collectors.toCollection(ArrayList::new));

        if (shipsAtPosition.size() > 1)
            throw new UnknownError("Somehow two ships are occupying the same position this should never be possible");
        if (shipsAtPosition.size() == 1)
            return Optional.ofNullable(shipsAtPosition.get(0));
        else
            return Optional.empty();
    }

    /**
     * Tries to get a Miss at a position
     *
     * @param column Which column the firing command lies on globally
     * @param row Which row the firing command lies on globally
     * @return An Optional with a value
     */
    private Optional<Miss> getMissAt(int column, int row) {
        var missesAtHere = misses.stream().filter(miss -> miss.column == column && miss.row == row).collect(Collectors.toCollection(ArrayList::new));
        if (missesAtHere.size() == 0)
            return Optional.empty();
        else
            return Optional.of(missesAtHere.get(0));
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
                switch (getSpotAt(column, row)) {
                    case EMPTY -> temp.append("   ");
                    case SHIP -> temp.append(" ==");
                    case MISS -> temp.append(" OO");
                    case HIT, SUNK -> temp.append(" XX");
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
                switch (getSpotAt(column, row)) {
                    case EMPTY, SHIP -> stringBuilder.append("   ");
                    case MISS -> stringBuilder.append(" OO");
                    case HIT, SUNK -> stringBuilder.append(" XX");
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

    /**
     * Represents a shot taken by the opponent that did not hit an allied ship
     */
    private static class Miss {
        public final int column, row;

        private Miss(int column, int row) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Miss miss = (Miss) o;
            return row == miss.row && column == miss.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column);
        }
    }

    /**
     * Represents a Ship and its segments as well as its location and orientation
     */
    private static class Ship {
        public final PlaceShipCommand.Orientation orientation;
        public final PlaceShipCommand.Type type;
        public final Spot[] spots;
        public final int column;
        public final int row;

        Ship(PlaceShipCommand.Orientation orientation, PlaceShipCommand.Type type, int column, int row) {
            this.orientation = orientation;
            this.type = type;
            this.row = row;
            this.column = column;

            this.spots = new Spot[type.size];
            for (int i = 0; i < type.size; i++) {
                spots[i] = Spot.SHIP;
            }
        }

        /**
         * Returns whether or not this ship takes up a coordinate.<br/>
         * This method deals with global rows and columns, so the offset from the playing field's 0,0 root
         *
         * @param column Which column the probe checks globally
         * @param row    Which row the probe checks globally
         * @return true if this ship occupies the given coordinates, false if it does not
         */
        public boolean isAt(int column, int row) {
            var tempCol = this.column;
            var tempRow = this.row;
            for (int i = 0; i < this.type.size; i++) {
                if (tempRow == row && tempCol == column)
                    return true;
                tempCol += orientation.columnVector;
                tempRow += orientation.rowVector;
            }
            return false;
        }

        /**
         * Fires at a position in this ship<br/>
         * This method deals with global rows and columns, so the offset from the playing field's 0,0 root
         *
         * @param column Which column the firing command lies on globally
         * @param row    Which row the firing command lies on globally
         * @return A pair describing the former state and the new state of the coordinate, mainly used for reporting that a ship has been sunk completely
         * @throws NotThisShipException when this ship does not occupy this area in the first place
         */
        public SpotChange firedAt(int column, int row) throws NotThisShipException {
            if (!isAt(column, row))
                throw new NotThisShipException("This ship is not even at that position");

            int index;
            if (row == this.row)
                index = Math.abs(this.column - column);
            else
                index = Math.abs(this.row - row);

            var old = spots[index];

            spots[index] = Spot.HIT;
            if (Arrays.stream(spots).noneMatch(spot -> spot == Spot.SHIP)) {
                Arrays.fill(spots, Spot.SUNK);
            }

            return new SpotChange(old, spots[index]);
        }

        /**
         * @return true if all segments of the ship have been hit
         */
        public boolean isSunk() {
            return Arrays.stream(spots).allMatch(spot -> spot == Spot.SUNK);
        }

        /**
         * Tries to get the Spot at a position
         * @param column Which column the firing command lies on globally
         * @param row Which row the firing command lies on globally
         * @return Spot if the given coordinates are actually occupied by this ship, None if there is a mismatch
         */
        public Optional<Spot> getSpotAt(int column, int row) {
            if (!isAt(column, row))
                return Optional.empty();

            int index;
            if (row == this.row)
                index = Math.abs(this.column - column);
            else
                index = Math.abs(this.row - row);

            return Optional.of(this.spots[index]);
        }
    }
}
