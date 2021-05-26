package local.patrick.battleships.common.commands;

import java.util.Locale;

import static local.patrick.battleships.common.Constants.*;

public class PlaceShipCommand extends Command {
    public final static String PREFIX = "PLACE_SHIP";
    public final Integer column, row;
    public final Orientation orientation;
    public final Type type;

    public PlaceShipCommand(Integer column, Integer row, Orientation orientation, Type type) {
        this.column = column;
        this.row = row;
        this.orientation = orientation;
        this.type = type;
    }


    @Override
    public String serialize() {
        return PREFIX + SEPARATOR + column + SEPARATOR + row + SEPARATOR + orientation + SEPARATOR + type;
    }

    public enum Orientation {
        LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1);
        public final int columnVector, rowVector;

        Orientation(int columnVector, int rowVector) {
            this.columnVector = columnVector;
            this.rowVector = rowVector;
        }

        public static Orientation deserialize(String raw) throws InstantiationException {
            return switch (raw) {
                case "LEFT" -> LEFT;
                case "RIGHT" -> RIGHT;
                case "UP" -> UP;
                case "DOWN" -> DOWN;
                default -> throw new InstantiationException("Shit's fucked ");
            };
        }
    }

    public enum Type {
        Carrier(CARRIER_SIZE), Battleship(BATTLESHIP_SIZE), Destroyer(DESTROYER_SIZE), Submarine(SUBMARINE_SIZE);
        public final int size;

        Type(int _size) {
            this.size = _size;
        }

        public static Type deserialize(String raw) throws InstantiationException {
            return switch (raw.toUpperCase(Locale.ROOT)) {
                case "CARRIER" -> Carrier;
                case "BATTLESHIP" -> Battleship;
                case "DESTROYER" -> Destroyer;
                case "SUBMARINE" -> Submarine;
                default -> throw new InstantiationException("Ship's fucked ");
            };
        }
    }

}
