package de.weareprophet.ihomeyou.datastructure.room;

import de.weareprophet.ihomeyou.asset.FloorType;
import de.weareprophet.ihomeyou.datastructure.GroundTileHandler;
import de.weareprophet.ihomeyou.datastructure.SimpleEdge;
import de.weareprophet.ihomeyou.datastructure.Tile;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
}
