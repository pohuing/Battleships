package local.patrick.battleships.common;

public class PlaceBombCommand extends Command {
    public final static String PREFIX = "PLACE_BOMB";
    public final Integer column, row;

    public PlaceBombCommand(Integer column, Integer row) {
        this.column = column;
        this.row = row;
    }

    @Override
    public String serialize() {
        return PREFIX + SEPARATOR + column + SEPARATOR + row;
    }
}
