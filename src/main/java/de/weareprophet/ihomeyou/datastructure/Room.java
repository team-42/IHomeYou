package de.weareprophet.ihomeyou.datastructure;

import com.google.common.collect.Table;
import de.weareprophet.ihomeyou.asset.AssetType;

import java.util.*;

public class Room {
    private Set<Tile> roomTiles = new HashSet<>();

    public void addTile(Tile tile) {
        roomTiles.add(tile);
    }

    public Collection<Tile> getTiles() {
        return roomTiles;
    }

    public List<AssetType> getRoomInventory(Table<Integer, Integer, AssetType> gameGrid) {
        List<AssetType> inventory = new ArrayList<>();

        for(Tile t : roomTiles) {
            if(gameGrid.contains(t.getRow(), t.getColumn())) {
                inventory.add(gameGrid.get(t.getRow(), t.getColumn()));
            }
        }

        return inventory;
    }
}
