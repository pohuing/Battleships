package local.patrick.battleships.common.commands;

/**
 * A command to ask the server to fire at a spot on the opponent's field
 */
public class FireAtCommand extends Command {
    public final static String PREFIX = "PLACE_BOMB";
    public final Integer column, row;

    public FireAtCommand(Integer column, Integer row) {
        this.column = column;
        this.row = row;
    }

    @Override
    public String serialize() {
        return PREFIX + SEPARATOR + column + SEPARATOR + row;
    }
}
