package local.patrick.battleships.common.commands;

/**
 * Tells the other side one party has quit
 */
public class QuitGameCommand extends Command {
    public final static String PREFIX = "QUIT_GAME";

    @Override
    public String serialize() {
        return PREFIX;
    }
}
