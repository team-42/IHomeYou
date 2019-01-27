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
    private Graph<Tile, SimpleEdge> wallGraph;
    private Graph<Tile, SimpleEdge> tileGraph;


    static final int SIZE = 64;
    static final int BORDERS = 10;

    static final int COLS = 16;
    static final int ROWS = 10;

    GameGrid(IHomeYouGame iHomeYouGame) {
        ihyg = iHomeYouGame;
        gameGridGroundTile = HashBasedTable.create();
        init();
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
        rooms = new ArrayList<>();
        wallGraph = null;
        tileGraph = null;

        initGameGridGroundTiles();
        wallGraph = initWallGraph();
        tileGraph = initTileGraph();
    }

    private void initGameGridGroundTiles() {
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
//                wallGraph.removeEdge(Pair.of(column, row), Pair.of(column+1,row));
                wallGraph.addEdge(Tile.of(column, row), Tile.of(column+1,row));
                tileGraph.removeEdge(Tile.of(column, row), Tile.of(column+1, row));
                break;
            case BOTTOM:
                obj = new ImageObject(res, SIZE * column + BORDERS - 4, SIZE * row + 64 + BORDERS - 4);
//                wallGraph.removeEdge(Pair.of(column, row+1), Pair.of(column+1,row+1));
                wallGraph.addEdge(Tile.of(column, row+1), Tile.of(column+1,row+1));
//                tileGraph.removeEdge(Tile.of(c, r+1))
                break;
            case LEFT:
                obj = new ImageObject(res, SIZE * column + BORDERS - 4, SIZE * row + BORDERS - 4);
//                wallGraph.removeEdge(Pair.of(column, row), Pair.of(column,row+1));
                wallGraph.addEdge(Tile.of(column, row), Tile.of(column,row+1));
                break;
            case RIGHT:
                obj = new ImageObject(res, SIZE * column + 64 + BORDERS - 4, SIZE * row + BORDERS - 4);
//                wallGraph.removeEdge(Pair.of(column+1, row), Pair.of(column+1,row+1));
                wallGraph.addEdge(Tile.of(column+1, row), Tile.of(column+1,row+1));
                break;
        }
        walls.add(obj);
        ihyg.addObject(obj);

        rooms = calculateRooms();
        setRoomGroundTile();

        System.out.println("Room Count: " + rooms.size());
//        printGraph();
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
        Set<List<SimpleEdge>> cycles = CycleDetection.calculate(wallGraph);

        List<Room> roomList = new ArrayList<>();
        // simple approx, is accurate for rectangular rooms
        for(List<SimpleEdge> cycle : cycles) {
            System.out.println("Edge List" + cycle);
            int maxTop = ROWS;
            int maxLeft = COLS;
            int maxRight = 0;
            int maxBottom = 0;
            for(SimpleEdge se : cycle) {
                if(se.getSource().getRow() < maxTop ) maxTop = se.getSource().getRow();
                if(se.getSource().getColumn() < maxLeft) maxLeft = se.getSource().getColumn();
                if(se.getTarget().getRow() > maxBottom) maxBottom = se.getTarget().getRow();
                if(se.getTarget().getColumn() > maxRight) maxRight = se.getTarget().getColumn();
            }

            Room room = new Room();
            for(int r = maxTop; r < maxBottom; r++) {
                for(int c = maxLeft; c < maxRight; c++) {
                    room.addTile(Tile.of(c, r));
                }
            }
            roomList.add(room);
        }

        // complex bfs-based room detection


        return roomList;
    }

    public void printGraph(Graph g) {
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
