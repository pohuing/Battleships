package local.patrick.battleships.common.commands;

public class QuitGameCommand extends Command {
    public final static String PREFIX = "QUIT_GAME";

    @Override
    public String serialize() {
        return PREFIX;
    }
}
