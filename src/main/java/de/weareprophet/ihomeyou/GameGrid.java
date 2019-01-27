package de.weareprophet.ihomeyou;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.weareprophet.ihomeyou.asset.AssetType;
import de.weareprophet.ihomeyou.asset.FloorType;
import de.weareprophet.ihomeyou.datastructure.FurnitureObject;
import de.weareprophet.ihomeyou.datastructure.GroundTileHandler;
import de.weareprophet.ihomeyou.datastructure.room.Room;
import de.weareprophet.ihomeyou.datastructure.SimpleEdge;
import de.weareprophet.ihomeyou.datastructure.Tile;
import de.weareprophet.ihomeyou.datastructure.room.RoomManager;
import org.frice.obj.sub.ImageObject;
import org.frice.resource.image.ImageResource;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.*;


public class GameGrid {
    private RoomManager roomManager;
    private GroundTileHandler gth;
    private Table<Integer, Integer, FurnitureObject> gameGrid;
    private List<ImageObject> walls;
    private IHomeYouGame ihyg;
    private Graph<Tile, SimpleEdge> wallGraph;
    private Graph<Tile, SimpleEdge> tileGraph;


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

    private Graph<Tile, SimpleEdge> initWallGraph() {
        Graph<Tile, SimpleEdge> graph = new SimpleGraph<>(SimpleEdge.class);

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
            roomManager.executeTileChange(gameGrid, gth);
            ihyg.addObject(obj);
            return true;
        }
        return false;
    }

    public void setWall(int row, int column, ImageResource res, WallDirection dir) {
        if(row == 0 || column == 0 || row == ROWS-1 || column == COLS-1) {
            return;
        }

        ImageObject obj = null;
        switch (dir) {
            case TOP:
                obj = new ImageObject(res, SIZE * column + BORDERS - 4, SIZE * row + BORDERS - 4);
//                wallGraph.removeEdge(Pair.of(column, row), Pair.of(column+1,row));
                wallGraph.addEdge(Tile.of(column, row), Tile.of(column+1,row));

                if(row-1 >= 0) tileGraph.removeEdge(Tile.of(column, row-1), Tile.of(column, row));
                break;
            case BOTTOM:
                obj = new ImageObject(res, SIZE * column + BORDERS - 4, SIZE * row + 64 + BORDERS - 4);
//                wallGraph.removeEdge(Pair.of(column, row+1), Pair.of(column+1,row+1));
                wallGraph.addEdge(Tile.of(column, row+1), Tile.of(column+1,row+1));

                if(row+1 < ROWS) tileGraph.removeEdge(Tile.of(column, row), Tile.of(column, row+1));
                break;
            case LEFT:
                obj = new ImageObject(res, SIZE * column + BORDERS - 4, SIZE * row + BORDERS - 4);
//                wallGraph.removeEdge(Pair.of(column, row), Pair.of(column,row+1));
                wallGraph.addEdge(Tile.of(column, row), Tile.of(column,row+1));

                if(column-1 >= 0) tileGraph.removeEdge(Tile.of(column-1, row), Tile.of(column, row));
                break;
            case RIGHT:
                obj = new ImageObject(res, SIZE * column + 64 + BORDERS - 4, SIZE * row + BORDERS - 4);
//                wallGraph.removeEdge(Pair.of(column+1, row), Pair.of(column+1,row+1));
                wallGraph.addEdge(Tile.of(column+1, row), Tile.of(column+1,row+1));

                if(column+1 < COLS) tileGraph.removeEdge(Tile.of(column, row), Tile.of(column+1, row));
                break;
        }
        walls.add(obj);
        ihyg.addObject(obj);

        roomManager.calculateRooms(tileGraph);
        roomManager.setRoomGroundTile(gth, wallGraph);

//        printGraph(tileGraph);
//        printGraph(wallGraph);
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
