package local.patrick.battleships.common.commands;

/**
 * A command asking for a printout of the game state
 */
public class GetFieldCommand extends Command {
    public final static String PREFIX = "GET_FIELD";

    @Override
    public String serialize() {
        return PREFIX;
    }
}
