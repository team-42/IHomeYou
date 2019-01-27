package de.weareprophet.ihomeyou;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.weareprophet.ihomeyou.algorithm.CycleDetection;
import de.weareprophet.ihomeyou.asset.AssetType;
import de.weareprophet.ihomeyou.asset.FloorType;
import de.weareprophet.ihomeyou.datastructure.FurnitureObject;
import de.weareprophet.ihomeyou.datastructure.Room;
import de.weareprophet.ihomeyou.datastructure.SimpleEdge;
import de.weareprophet.ihomeyou.datastructure.Tile;
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
    public List<Room> rooms;

    private Table<Integer, Integer, FurnitureObject> gameGrid;
    private Table<Integer, Integer, ImageObject> gameGridGroundTile;
    private List<ImageObject> walls;
    private IHomeYouGame ihyg;
    private Graph<Tile, SimpleEdge> graph;

    static final int SIZE = 64;
    static final int BORDERS = 10;

    static final int COLS = 16;
    static final int ROWS = 10;

    GameGrid(IHomeYouGame iHomeYouGame) {
        init();
        ihyg = iHomeYouGame;

        initGameGridGroundTiles();
        graph = initNoWallGraph();
    }

    public void resetGameGrid() {
        gameGridGroundTile.values().forEach(ihyg::removeObject);
        gameGrid.values().forEach(fo -> ihyg.removeObject(fo.getObj()));
        walls.forEach(ihyg::removeObject);

        init();
    }

    private void init() {
        gameGrid = HashBasedTable.create();
        gameGridGroundTile = HashBasedTable.create();
        walls = new ArrayList<>();
        rooms = new ArrayList<>();
        graph = null;
    }

    private void initGameGridGroundTiles() {
        gameGridGroundTile = HashBasedTable.create();
        for(int c = 0; c < COLS; c++) {
            for(int r = 0; r < ROWS; r++) {
                setGroundTile(FloorType.GRASS, c, r);
            }
        }
    }

    private void setGroundTile(FloorType floorType, int column, int row) {

        if(gameGridGroundTile.contains(row, column)) {
            gameGridGroundTile.get(row, column).setRes(floorType.getResource());
        } else {
            ImageObject tileImage = new ImageObject(floorType.getResource(), column * SIZE + BORDERS, row * SIZE + BORDERS);
            gameGridGroundTile.put(row, column, tileImage);
            ihyg.addObject(tileImage);
        }
    }


    public Collection<FurnitureObject> getAssetsInGrid() {
        return gameGrid.values();
    }

    public Collection<FurnitureObject> getAssetsInRoom(Room room) {
        return room.getRoomInventory(gameGrid);
    }

    private Graph<Tile, SimpleEdge> initNoWallGraph() {
        Graph<Tile, SimpleEdge> graph = new SimpleGraph<>(SimpleEdge.class);

        // add vertices
        for(int c = 0; c <= COLS+1; c++) {
            for (int r = 0; r <= ROWS+1; r++) {
                graph.addVertex(Tile.of(c, r));
            }
        }
        return graph;
    }

    public boolean setFurniture(int row, int column, AssetType at) {
        if(!gameGrid.contains(row, column)) {
            ImageObject obj = new ImageObject(at.getResource(), SIZE * column + BORDERS + 8, SIZE * row + BORDERS + 8);
            gameGrid.put(row, column, FurnitureObject.of(at, obj));
            ihyg.addObject(obj);
            return true;
        }
        return false;
    }

    public void setWall(int row, int column, ImageResource res, WallDirection dir) {

        ImageObject obj = null;
        switch (dir) {
            case TOP:
                obj = new ImageObject(res, SIZE * column + BORDERS - 4, SIZE * row + BORDERS - 4);
//                graph.removeEdge(Pair.of(column, row), Pair.of(column+1,row));
                graph.addEdge(Tile.of(column, row), Tile.of(column+1,row));
                break;
            case BOTTOM:
                obj = new ImageObject(res, SIZE * column + BORDERS - 4, SIZE * row + 64 + BORDERS - 4);
//                graph.removeEdge(Pair.of(column, row+1), Pair.of(column+1,row+1));
                graph.addEdge(Tile.of(column, row+1), Tile.of(column+1,row+1));
                break;
            case LEFT:
                obj = new ImageObject(res, SIZE * column + BORDERS - 4, SIZE * row + BORDERS - 4);
//                graph.removeEdge(Pair.of(column, row), Pair.of(column,row+1));
                graph.addEdge(Tile.of(column, row), Tile.of(column,row+1));
                break;
            case RIGHT:
                obj = new ImageObject(res, SIZE * column + 64 + BORDERS - 4, SIZE * row + BORDERS - 4);
//                graph.removeEdge(Pair.of(column+1, row), Pair.of(column+1,row+1));
                graph.addEdge(Tile.of(column+1, row), Tile.of(column+1,row+1));
                break;
        }
        walls.add(obj);
        ihyg.addObject(obj);

        rooms = calculateRooms();
        setRoomGroundTile();

        System.out.println("Room Count: " + rooms.size());
//        printWallGraph();
    }

    private void setRoomGroundTile() {
        for(Room r : rooms) {
            for (Tile t : r.getTiles()) {
                setGroundTile(FloorType.WOOD, t.getColumn(), t.getRow());
            }
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public enum WallDirection {
        TOP, BOTTOM, LEFT, RIGHT
    }

    private List<Room> calculateRooms() {
        Set<List<SimpleEdge>> cycles = CycleDetection.calculate(graph);

        List<Room> roomList = new ArrayList<>();
        for(List<SimpleEdge> cycle : cycles) {
            Room room = new Room();
            for(SimpleEdge se : cycle) {
                room.addTile(se.getSource());
            }
            roomList.add(room);
        }

        return roomList;
    }

    public void printWallGraph() {
        ComponentNameProvider<Tile> vertexIdProvider = ele -> "V_" + ele.getFirst() + "_" + ele.getSecond();
        ComponentNameProvider<Tile> vertexLabelProvider = ele -> "(" + ele.getFirst() + "," + ele.getSecond() + ")";

        GraphExporter<Tile, SimpleEdge> exporter =
                new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
        Writer writer = new StringWriter();
        try {
            exporter.exportGraph(graph, writer);
        } catch (ExportException e) {
            e.printStackTrace();
        }
        System.out.println(writer.toString());
    }
}
