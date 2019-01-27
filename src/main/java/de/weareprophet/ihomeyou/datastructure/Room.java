package de.weareprophet.ihomeyou.datastructure;

import com.google.common.collect.Table;

import java.util.*;

public class Room {
    private Set<Tile> roomTiles = new HashSet<>();

    public void addTile(Tile tile) {
        roomTiles.add(tile);
    }

    public void addAllTiles(Collection<Tile> tiles) {
        roomTiles.addAll(tiles);
    }

    public Collection<Tile> getTiles() {
        return roomTiles;
    }

    public List<FurnitureObject> getRoomInventory(Table<Integer, Integer, FurnitureObject> gameGrid) {
        List<FurnitureObject> inventory = new ArrayList<>();

        for(Tile t : roomTiles) {
            if(gameGrid.contains(t.getRow(), t.getColumn())) {
                inventory.add(gameGrid.get(t.getRow(), t.getColumn()));
            }
        }

        return inventory;
    }

    @Override
    public String toString() {
        return "Tiles: " + roomTiles;
    }
}
