package local.patrick.battleships.common;

/**
 * Thrown when a player tries to place a new ship when the max amount of ships of that type is already reached
 */
public class TooManyShipsException extends Throwable {
    public TooManyShipsException(String s) {
        super(s);
    }
}
