package local.patrick.battleships.common;

import java.util.HashMap;

public class PlayingField {
    private final HashMap<Integer, HashMap<Integer, Spot>> field;

    public PlayingField() {
        field = new HashMap<>();
        for (int i = 0; i < Constants.DIMENSIONX; i++) {
            field.put(i, new HashMap<>());
            for (int j = 0; j < Constants.DIMENSIONX; j++) {
                field.get(i).put(j, Spot.EMPTY);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder("XX");
        for (int i = 0; i < Constants.DIMENSIONX; i++) {
            temp.append(String.format(" %02d", i));
        }

        for (int i = 0; i < Constants.DIMENSIONY; i++) {
            temp.append("\n");
            temp.append(String.format("%02d", i));
            for (int j = 0; j < Constants.DIMENSIONX; j++) {
                switch (field.get(i).get(j)){
                    case EMPTY -> temp.append("   ");
                    case SHIP -> temp.append(" ==");
                    case MISS -> temp.append(" OO");
                    case HIT -> temp.append(" XX");
                }
            }
        }

        return temp.toString();
    }

    /*
    Hides the Ship positions
     */
    public String toOpponentString(){
        StringBuilder temp = new StringBuilder("XX");
        for (int i = 0; i < Constants.DIMENSIONX; i++) {
            temp.append(String.format(" %02d", i));
        }

        for (int i = 0; i < Constants.DIMENSIONY; i++) {
            temp.append("\n");
            temp.append(String.format("%02d", i));
            for (int j = 0; j < Constants.DIMENSIONX; j++) {
                switch (field.get(i).get(j)){
                    case EMPTY, SHIP -> temp.append("   ");
                    case MISS -> temp.append(" OO");
                    case HIT -> temp.append(" XX");
                }
            }
        }
        return temp.toString();
    }

    public String toAllyString(){
        return toString();
    }
}
