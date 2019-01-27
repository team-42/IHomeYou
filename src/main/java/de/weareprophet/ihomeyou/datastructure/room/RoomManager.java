package de.weareprophet.ihomeyou.datastructure.room;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import de.weareprophet.ihomeyou.asset.FloorType;
import de.weareprophet.ihomeyou.asset.WallType;
import de.weareprophet.ihomeyou.datastructure.*;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

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

    public void calculateRoomAccessibility(Multimap<Tile, WallEdge> wallEdgeHashMap) {
        SimpleGraph<Room, DefaultEdge> roomGraph = new SimpleGraph<>(DefaultEdge.class);

        // create vertex for each room
        rooms.forEach(roomGraph::addVertex);

        Room outdoor = null;

        for(Room r : rooms) {
            if(r.getRoomType() != null && r.getRoomType().equals(RoomTypes.OUTDOOR)) outdoor = r;
            for(Tile t : r.getTiles()) {
                if(wallEdgeHashMap.containsKey(t)) {
                    for (WallEdge we : wallEdgeHashMap.get(t)) {
                        if (we.getType().equals(WallType.Door)) {
                            Tile neighborTile = null;
                            switch (we.getDirection()) {
                                case TOP:
                                    neighborTile = Tile.of(we.getSource().getColumn(), we.getSource().getRow()-1);
                                    break;
                                case BOTTOM:
                                    neighborTile = Tile.of(we.getSource().getColumn(), we.getSource().getRow());
                                    break;
                                case LEFT:
                                    neighborTile = Tile.of(we.getSource().getColumn()-1, we.getSource().getRow());
                                    break;
                                case RIGHT:
                                    neighborTile = Tile.of(we.getSource().getColumn(), we.getSource().getRow());
                                    break;
                            }
                            for(Room neighborRoomCandidate : rooms) {
                                if(neighborRoomCandidate.isTilePartOfRoom(neighborTile)) {
                                    roomGraph.addEdge(r, neighborRoomCandidate);
                                }
                            }
                        }
                    }
                }
            }
        }

        // calculate connectivity
        ConnectivityInspector<Room, DefaultEdge> ci = new ConnectivityInspector<>(roomGraph);
        Set<Room> accessibleRooms = ci.connectedSetOf(outdoor);
        accessibleRooms.forEach(r -> r.setAccessible(true));
    }

    public void setRoomGroundTile(Table<Integer, Integer, FurnitureObject> gameGrid, GroundTileHandler gth, Graph<Tile, WallEdge> wallGraph) {
        for(Room r : rooms) {
            boolean draw = true;
            for(Tile t : r.getTiles()) {
                // upper left corner
                if (t.getRow() == 0 && t.getColumn() == 0 && !(
                        wallGraph.containsEdge(t, Tile.of(t.getColumn() + 1, t.getRow())) &&
                                wallGraph.containsEdge(t, Tile.of(t.getColumn(), t.getRow() + 1)))) {
                    draw = false;
                    r.setRoomType(RoomTypes.OUTDOOR);
                    break;
                }
            }
            if (!draw) continue;

            executeTileChange(r, gameGrid, gth);

//            for (Tile t : r.getTiles()) {
//                gth.setGroundTile(FloorType.WOOD, t.getColumn(), t.getRow());
//            }
        }
    }

    public void executeTileChangesForAllRooms(Table<Integer, Integer, FurnitureObject> gameGrid, GroundTileHandler gth) {
        for (Room r : rooms) {
            executeTileChange(r, gameGrid, gth);
        }
    }

    public void executeTileChange(Room r, Table<Integer, Integer, FurnitureObject> gameGrid, GroundTileHandler gth) {
        List<FurnitureObject> furnitureObjects = r.getRoomInventory(gameGrid);
        if(furnitureObjects.size() > 0) {
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

            if (maxEntry.getValue() >= furnitureObjects.size() / 2) {
                if (maxEntry.getKey().equals(RoomTypes.BATH)) {
                    for (Tile t : r.getTiles()) {
                        gth.setGroundTile(FloorType.TILE, t.getColumn(), t.getRow());
                    }
                } else if(maxEntry.getKey().equals(RoomTypes.KITCHEN)) {
                    for (Tile t : r.getTiles()) {
                        gth.setGroundTile(FloorType.TILE_KITCHEN, t.getColumn(), t.getRow());
                    }
                } else {
                    for (Tile t : r.getTiles()) {
                        gth.setGroundTile(FloorType.WOOD, t.getColumn(), t.getRow());
                    }
                }
            } else {
                for (Tile t : r.getTiles()) {
                    gth.setGroundTile(FloorType.WOOD, t.getColumn(), t.getRow());
                }
            }
        } else if(r.getRoomType() == null || !r.getRoomType().equals(RoomTypes.OUTDOOR)){
            for (Tile t : r.getTiles()) {
                gth.setGroundTile(FloorType.WOOD, t.getColumn(), t.getRow());
            }
        }
    }
}
