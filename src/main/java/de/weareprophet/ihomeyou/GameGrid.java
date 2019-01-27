package de.weareprophet.ihomeyou;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import de.weareprophet.ihomeyou.asset.AssetType;
import de.weareprophet.ihomeyou.asset.WallType;
import de.weareprophet.ihomeyou.datastructure.*;
import de.weareprophet.ihomeyou.datastructure.room.Room;
import de.weareprophet.ihomeyou.datastructure.room.RoomManager;
import org.frice.obj.sub.ImageObject;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class GameGrid {
    private RoomManager roomManager;
    private GroundTileHandler gth;
    private Table<Integer, Integer, FurnitureObject> gameGrid;
    private List<ImageObject> walls;
    private IHomeYouGame ihyg;
    private Graph<Tile, WallEdge> wallGraph;
    private Graph<Tile, SimpleEdge> tileGraph;

    private Multimap<Tile, WallEdge> wallEdgeHashMap;


    public static final int SIZE = 64;
    public static final int BORDERS = 10;

    public static final int COLS = 16;
    public static final int ROWS = 10;

    GameGrid(IHomeYouGame iHomeYouGame) {
        gth = new GroundTileHandler(iHomeYouGame);
        ihyg = iHomeYouGame;
        init();
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public void resetGameGrid() {
//        gameGridGroundTile.values().forEach(ihyg::removeObject);
        gameGrid.values().forEach(fo -> ihyg.removeObject(fo.getObj()));
        walls.forEach(ihyg::removeObject);

        init();
    }

    public void init() {
        wallEdgeHashMap = HashMultimap.create();
        gameGrid = HashBasedTable.create();
        walls = new ArrayList<>();
        wallGraph = null;
        tileGraph = null;
        roomManager = new RoomManager();
        gth.reset();

        wallGraph = initWallGraph();
        tileGraph = initTileGraph();
    }


    public Collection<FurnitureObject> getAssetsInGrid() {
        return gameGrid.values();
    }

    public Collection<FurnitureObject> getAssetsInRoom(Room room) {
        return room.getRoomInventory(gameGrid);
    }

    public int getNumWallTypeInRoom(WallType type, Room r) {
        int count = 0;
        for(Tile t : r.getTiles()) {
            if(wallEdgeHashMap.containsKey(t)) {
                for (WallEdge we : wallEdgeHashMap.get(t)) {
                    if (we.getType().equals(type)) count++;
                }
            }
        }

        return count;
    }

    private Graph<Tile, WallEdge> initWallGraph() {
        Graph<Tile, WallEdge> graph = new SimpleGraph<>(WallEdge.class);

        // add vertices
        for(int c = 0; c <= COLS+1; c++) {
            for (int r = 0; r <= ROWS+1; r++) {
                graph.addVertex(Tile.of(c, r));
            }
        }
        return graph;
    }


    private Graph<Tile, SimpleEdge> initTileGraph() {
        Graph<Tile, SimpleEdge> graph = new SimpleGraph<>(SimpleEdge.class);

        // add vertices
        for(int c = 0; c < COLS; c++) {
            for (int r = 0; r < ROWS; r++) {
                graph.addVertex(Tile.of(c, r));
            }
        }

        for(int c = 0; c < COLS; c++) {
            for (int r = 0; r < ROWS; r++) {
                if(c+1 < COLS) {
                    graph.addEdge(Tile.of(c, r), Tile.of(c + 1, r));
                }

                if(r+1 < ROWS) {
                    graph.addEdge(Tile.of(c, r), Tile.of(c, r + 1));
                }
            }
        }
        return graph;
    }

    public boolean setFurniture(int row, int column, AssetType at) {
        if(!gameGrid.contains(row, column)) {
            ImageObject obj = new ImageObject(at.getResource(), SIZE * column + BORDERS + 8, SIZE * row + BORDERS + 8);
            gameGrid.put(row, column, FurnitureObject.of(at, obj));
            roomManager.executeTileChangesForAllRooms(gameGrid, gth);
            ihyg.addObject(obj);
            return true;
        }
        return false;
    }

    public boolean setWall(int row, int column, WallType wallType, WallDirection dir) {
        if(row == 0 || column == 0 || row == ROWS-1 || column == COLS-1) {
            return false;
        }

        ImageObject obj = null;
        WallEdge w = null;
        switch (dir) {
            case TOP:
                obj = new ImageObject(wallType.getResource(WallDirection.TOP), SIZE * column + BORDERS - 4, SIZE * row + BORDERS - 4);
                if(wallGraph.containsEdge(Tile.of(column, row), Tile.of(column+1,row))) return false;
                w = wallGraph.addEdge(Tile.of(column, row), Tile.of(column+1,row));

                if(row-1 >= 0) tileGraph.removeEdge(Tile.of(column, row-1), Tile.of(column, row));
                break;
            case BOTTOM:
                obj = new ImageObject(wallType.getResource(WallDirection.BOTTOM), SIZE * column + BORDERS - 4, SIZE * row + 64 + BORDERS - 4);
                if(wallGraph.containsEdge(Tile.of(column, row+1), Tile.of(column+1,row+1))) return false;
                w = wallGraph.addEdge(Tile.of(column, row+1), Tile.of(column+1,row+1));

                if(row+1 < ROWS) tileGraph.removeEdge(Tile.of(column, row), Tile.of(column, row+1));
                break;
            case LEFT:
                obj = new ImageObject(wallType.getResource(WallDirection.LEFT), SIZE * column + BORDERS - 4, SIZE * row + BORDERS - 4);
                if(wallGraph.containsEdge(Tile.of(column, row), Tile.of(column,row+1))) return false;
                w = wallGraph.addEdge(Tile.of(column, row), Tile.of(column,row+1));

                if(column-1 >= 0) tileGraph.removeEdge(Tile.of(column-1, row), Tile.of(column, row));
                break;
            case RIGHT:
                obj = new ImageObject(wallType.getResource(WallDirection.RIGHT), SIZE * column + 64 + BORDERS - 4, SIZE * row + BORDERS - 4);
                if(wallGraph.containsEdge(Tile.of(column+1, row), Tile.of(column+1,row+1))) return false;
                w = wallGraph.addEdge(Tile.of(column+1, row), Tile.of(column+1,row+1));

                if(column+1 < COLS) tileGraph.removeEdge(Tile.of(column, row), Tile.of(column+1, row));
                break;
        }
        if(w != null) {
            w.setType(wallType);
            wallEdgeHashMap.put(Tile.of(column, row), w);
        }

        walls.add(obj);
        ihyg.addObject(obj);

        roomManager.calculateRooms(tileGraph);
        roomManager.setRoomGroundTile(gameGrid, gth, wallGraph);

//        printGraph(tileGraph);
//        printGraph(wallGraph);
        return true;
    }

    public enum WallDirection {
        TOP, BOTTOM, LEFT, RIGHT
    }

    private void printGraph(Graph<Tile, SimpleEdge> g) {
        ComponentNameProvider<Tile> vertexIdProvider = ele -> "V_" + ele.getFirst() + "_" + ele.getSecond();
        ComponentNameProvider<Tile> vertexLabelProvider = ele -> "(" + ele.getFirst() + "," + ele.getSecond() + ")";

        GraphExporter<Tile, SimpleEdge> exporter =
                new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
        Writer writer = new StringWriter();
        try {
            exporter.exportGraph(g, writer);
        } catch (ExportException e) {
            e.printStackTrace();
        }
        System.out.println(writer.toString());
    }
}
