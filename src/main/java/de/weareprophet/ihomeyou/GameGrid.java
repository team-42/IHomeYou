package de.weareprophet.ihomeyou;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.weareprophet.ihomeyou.algorithm.CycleDetection;
import de.weareprophet.ihomeyou.asset.AssetType;
import de.weareprophet.ihomeyou.datastructure.Room;
import de.weareprophet.ihomeyou.datastructure.SimpleEdge;
import de.weareprophet.ihomeyou.datastructure.Tile;
import org.frice.obj.sub.ImageObject;
import org.frice.obj.sub.ShapeObject;
import org.frice.resource.graphics.ColorResource;
import org.frice.resource.image.ImageResource;
import org.frice.util.shape.FRectangle;
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
    private Table<Integer, Integer, AssetType> gameGrid;
    private IHomeYouGame ihyg;
    private Graph<Tile, SimpleEdge> graph;

    static final int SIZE = 64;
    static final int BORDERS = 10;

    static final int COLS = 16;
    static final int ROWS = 10;

    GameGrid(IHomeYouGame iHomeYouGame) {
        gameGrid = HashBasedTable.create();
        ihyg = iHomeYouGame;

        ShapeObject gameGrid = new ShapeObject(ColorResource.LIGHT_GRAY, new FRectangle(COLS * SIZE, ROWS * SIZE));
        gameGrid.setX(BORDERS);
        gameGrid.setY(BORDERS);
        ihyg.addObject(gameGrid);

        graph = initNoWallGraph();
//        printWallGraph();
    }

    public Collection<AssetType> getAssetsInGrid() {
        return gameGrid.values();
    }

    public Collection<AssetType> getAssetsInRoom(Room room) {
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
//
//        // add edges
//        for(int c = 0; c <= COLS+1; c++) {
//            for (int r = 0; r <= ROWS+1; r++) {
//                if(c+1 <= COLS+1) {
//                    graph.addEdge(Pair.of(c, r), Pair.of(c+1, r));
//                }
//
//                if(r+1 <= ROWS+1) {
//                    graph.addEdge(Pair.of(c, r), Pair.of(c, r+1));
//                }
//            }
//        }
        return graph;
    }

    public boolean setObject(int row, int column, AssetType at) {
        if(!gameGrid.contains(row, column)) {
            ImageObject obj = new ImageObject(at.getResource(), SIZE * column + BORDERS + 8, SIZE * row + BORDERS + 8);
            gameGrid.put(row, column, at);
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
        ihyg.addObject(obj);
        List<Room> sets = calculateRooms();
        System.out.println("Room Count: " + sets.size());
//        printWallGraph();
    }

    public enum WallDirection {
        TOP, BOTTOM, LEFT, RIGHT
    }

    public List<Room> calculateRooms() {
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
