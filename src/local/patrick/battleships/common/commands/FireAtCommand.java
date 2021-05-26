package local.patrick.battleships.common.commands;

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
