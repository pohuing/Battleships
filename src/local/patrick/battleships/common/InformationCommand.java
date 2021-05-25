package local.patrick.battleships.common;

public class InformationCommand extends Command{
    public final static String PREFIX = "INFORMATION";
    public final String message;

    public InformationCommand(String message) {
        this.message = message;
    }


    @Override
    public String serialize() {
        return PREFIX + SEPARATOR + message;
    }
}
