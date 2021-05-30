package local.patrick.battleships.server;

/**
 * Since java is missing a Pair class this is a substitute
 */
public class SpotChange {
    public final Spot old, current;

    public SpotChange(Spot old, Spot current) {
        this.old = old;
        this.current = current;
    }

    @Override
    public String toString() {
        if (old != current && current == Spot.SUNK)
            return "Ship was sunk!";
        else return current.toString();
    }
}
