package local.patrick.battleships.common;

public class GetFieldCommand extends Command {
    public final static String PREFIX = "GET_FIELD";

    @Override
    public String serialize() {
        return PREFIX;
    }
}
