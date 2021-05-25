package local.patrick.battleships.common;

public class QuitGameCommand extends Command {
    public final static String PREFIX = "QUIT_GAME";

    @Override
    public String serialize() {
        return PREFIX;
    }
}
