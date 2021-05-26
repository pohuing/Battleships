package local.patrick.battleships.common.commands;


import java.util.Arrays;

/**
 *
 */
public abstract class Command {
    //public final static String SEPARATOR = "ヾ\\(｡>﹏<｡\\)ﾉﾞ✧*";
    public final static String SEPARATOR = ";";

    public static Command deserialize(String line) throws InstantiationException {
        var params = line.split(SEPARATOR);
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

    public abstract String serialize();
}

