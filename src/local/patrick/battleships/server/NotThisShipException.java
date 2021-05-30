package local.patrick.battleships.server;

/**
 * Thrown if there is an internal logic error that leads to an incorrect method call where a ship can't be fired upon
 * because it doesn't even occupy the spot passed as the parameters
 */
public class NotThisShipException extends Throwable{
    public NotThisShipException(String s) {
        super(s);
    }
}
