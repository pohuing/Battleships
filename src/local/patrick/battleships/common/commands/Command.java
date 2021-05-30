package local.patrick.battleships.common.commands;


import java.util.Arrays;

/**
 * Parent class to define command based communications
 */
public abstract class Command {
    //public final static String SEPARATOR = "ヾ\\(｡>﹏<｡\\)ﾉﾞ✧*";
    public final static String SEPARATOR = ";";

    /**
     * Tries to deserialize a String into a Command subclass
     * @param line The line to be deserialized
     * @return An instance of Command
     * @throws InstantiationException If line could not be parsed into a known subclass of Command
     */
    public static Command deserialize(String line) throws InstantiationException {
        var params = line.split(SEPARATOR);
        // The first Word decides what kind of Command line is
        // Then parameter count will be checked
        // Then the conversion is tried
        switch (params[0]) {
            case FireAtCommand.PREFIX:
                if (params.length == 3) {
                    var x = Integer.decode(params[1]);
                    var y = Integer.decode(params[2]);
                    return new FireAtCommand(x, y);
                }
            case PlaceShipCommand.PREFIX:
                if (params.length == 5) {
                    var x = Integer.decode(params[1]);
                    var y = Integer.decode(params[2]);
                    var orientation = PlaceShipCommand.Orientation.deserialize(params[3]);
                    var type = PlaceShipCommand.Type.deserialize(params[4]);
                    return new PlaceShipCommand(x, y, orientation, type);
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

    /**
     * Turns command into a String ready for sending across a socket
     * @return Textual representation of a Command
     */
    public abstract String serialize();
}

