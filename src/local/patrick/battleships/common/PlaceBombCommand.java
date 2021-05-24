package local.patrick.battleships.common;

public class PlaceBombCommand extends Command {
    public final static String PREFIX = "PLACE_BOMB";
    private final Integer x, y;

    public PlaceBombCommand(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String serialize() {
        return String.format("%s %d %d", PREFIX, x, y);
    }
}
