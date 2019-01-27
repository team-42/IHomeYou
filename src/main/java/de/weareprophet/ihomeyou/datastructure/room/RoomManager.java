package de.weareprophet.ihomeyou.datastructure.room;

import com.google.common.collect.Table;
import de.weareprophet.ihomeyou.asset.FloorType;
import de.weareprophet.ihomeyou.datastructure.FurnitureObject;
import de.weareprophet.ihomeyou.datastructure.GroundTileHandler;
import de.weareprophet.ihomeyou.datastructure.SimpleEdge;
import de.weareprophet.ihomeyou.datastructure.Tile;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManager {
    private List<Room> rooms;

    public RoomManager() {
        rooms = new ArrayList<>();
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void calculateRooms(Graph<Tile, SimpleEdge> tileGraph) {
        ConnectivityInspector<Tile, SimpleEdge> ci = new ConnectivityInspector<>(tileGraph);
        List<Set<Tile>> rooms = ci.connectedSets();

        List<Room> roomList = new ArrayList<>();
        for(Set<Tile> room : rooms) {
            if(room.size() <= 1) continue;
            Room r = new Room();
            r.addAllTiles(room);

            roomList.add(r);
        }
        this.rooms = roomList;
    }

    public void setRoomGroundTile(GroundTileHandler gth, Graph<Tile, SimpleEdge> wallGraph) {
        for(Room r : rooms) {
            boolean draw = true;
            for(Tile t : r.getTiles()) {
                // upper left corner
                if (t.getRow() == 0 && t.getColumn() == 0 && !(
                        wallGraph.containsEdge(t, Tile.of(t.getColumn() + 1, t.getRow())) &&
                                wallGraph.containsEdge(t, Tile.of(t.getColumn(), t.getRow() + 1)))) {
                    draw = false;
                    break;
                }
            }
            if (!draw) continue;

            for (Tile t : r.getTiles()) {
                gth.setGroundTile(FloorType.WOOD, t.getColumn(), t.getRow());
            }
        }
    }

    public void executeTileChange(Table<Integer, Integer, FurnitureObject> gameGrid, GroundTileHandler gth) {
        for(Room r : rooms) {
            List<FurnitureObject> furnitureObjects = r.getRoomInventory(gameGrid);
            System.out.println("Funniture Objects: " + furnitureObjects.size());
            if(furnitureObjects.size() > 0) {
                System.out.println("Furniture in Room: " + furnitureObjects.size());
                Map<RoomTypes, Integer> roomTypePerFurnitureCountMap = new HashMap<>();
                for (FurnitureObject fo : furnitureObjects) {
                    for (RoomTypes type : RoomTypes.values()) {
                        if (type.validRoomAsset(fo.getType())) {
                            if (!roomTypePerFurnitureCountMap.containsKey(type))
                                roomTypePerFurnitureCountMap.put(type, 1);
                            else roomTypePerFurnitureCountMap.put(type, roomTypePerFurnitureCountMap.get(type) +1);
                        }
                    }
                }

                Map.Entry<RoomTypes, Integer> maxEntry = null;
                for (Map.Entry<RoomTypes, Integer> entry : roomTypePerFurnitureCountMap.entrySet()) {
                    if (maxEntry == null || entry.getValue() > maxEntry.getValue())
                        maxEntry = entry;
                }

                System.out.println("Max Entry: " + maxEntry.getKey().name() + "|" + maxEntry.getValue());

                if (maxEntry.getValue() >= furnitureObjects.size() / 2) {
                    if (maxEntry.getKey().equals(RoomTypes.BATH) || maxEntry.getKey().equals(RoomTypes.KITCHEN)) {
                        for (Tile t : r.getTiles()) {
                            gth.setGroundTile(FloorType.TILE, t.getColumn(), t.getRow());
                        }
                    } else {
                        for (Tile t : r.getTiles()) {
                            gth.setGroundTile(FloorType.WOOD, t.getColumn(), t.getRow());
                        }
                    }
                }
            }
        }

    }
}
