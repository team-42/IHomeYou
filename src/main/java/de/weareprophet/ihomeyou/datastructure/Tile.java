package de.weareprophet.ihomeyou.datastructure;

import org.jgrapht.alg.util.Pair;

public class Tile extends Pair<Integer, Integer> {
    /**
     * Create a new pair
     *
     * @param column  the first element
     * @param row the second element
     */
    private Tile(Integer column, Integer row) {
        super(column, row);
    }

    public static Tile of(Integer column, Integer row) {
        return new Tile(column, row);
    }

    public Integer getRow() {
        return getSecond();
    }

    public Integer getColumn() {
        return getFirst();
    }
}
