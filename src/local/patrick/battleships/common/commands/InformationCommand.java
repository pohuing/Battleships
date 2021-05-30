package local.patrick.battleships.common.commands;

/**
 * A command asking to print out information such as game state or other misc. server messages
 * Also used for feedback to commands such as PlaceShipCommand
 */
public class InformationCommand extends Command{
    public final static String PREFIX = "INFORMATION";
    public final String message;

    public InformationCommand(String message) {
        this.message = message;
    }

    private String escapeMessage(){
        return message.replace("\n", "\\n");
    }

    @Override
    public String serialize() {
        return PREFIX + SEPARATOR + escapeMessage();
    }
}
