package local.patrick.battleships.common;


import java.util.Arrays;

public abstract class Command {
    //public final static String SEPARATOR = "ヾ\\(｡>﹏<｡\\)ﾉﾞ✧*";
    public final static String SEPARATOR = ";";

    public static Command deserialize(String line) throws InstantiationException {
        var params = line.split(SEPARATOR);
        switch (params[0]) {
            case PlaceBombCommand.PREFIX:
                if (params.length == 3) {
                    var x = Integer.decode(params[1]);
                    var y = Integer.decode(params[2]);
                    return new PlaceBombCommand(x, y);
                }
            case PlaceShipCommand.PREFIX:
                if (params.length == 4) {
                    var x = Integer.decode(params[1]);
                    var y = Integer.decode(params[2]);
                    var orientation = PlaceShipCommand.Orientation.deserialize(params[3]);
                    return new PlaceShipCommand(x, y, orientation);
                }
            case GetFieldCommand.PREFIX:
                if (params.length == 1) {
                    return new GetFieldCommand();
                }
            case InformationCommand.PREFIX:
                if (params.length == 2) {
                    return new InformationCommand(params[1].replace("\\n", "\n"));
                }
            case QuitGameCommand.PREFIX:
                return new QuitGameCommand();
            default:
                throw new InstantiationException("Failed to deserialize params: " + Arrays.toString(params) + " Into subclass with appropriate params");

        }
    }

    public abstract String serialize();
}

class PlaceShipCommand extends Command {
    public final static String PREFIX = "PLACE_SHIP";
    public final Integer x, y;
    public final Orientation orientation;

    public PlaceShipCommand(Integer x, Integer y, Orientation orientation) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
    }

    @Override
    public String serialize() {
        return PREFIX + SEPARATOR + x + SEPARATOR + y + SEPARATOR + orientation;
    }

    public enum Orientation {
        LEFT, RIGHT, UP, DOWN;

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

}

