package de.weareprophet.ihomeyou.datastructure.room;

import com.google.common.collect.Table;
import de.weareprophet.ihomeyou.datastructure.FurnitureObject;
import de.weareprophet.ihomeyou.datastructure.Tile;

import java.util.*;

public class Room {
    private Set<Tile> roomTiles = new HashSet<>();
    private RoomTypes roomType;
    private boolean accessible = false;

    public void addAllTiles(Collection<Tile> tiles) {
        roomTiles.addAll(tiles);
    }

    public Collection<Tile> getTiles() {
        return roomTiles;
    }

    public boolean isTilePartOfRoom(Tile t) {
        return roomTiles.contains(t);
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

    public void setRoomType(RoomTypes roomType) {
        this.roomType = roomType;
    }

    public RoomTypes getRoomType() {
        return roomType;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    @Override
    public String toString() {
        return "Tiles: " + roomTiles;
    }
}
