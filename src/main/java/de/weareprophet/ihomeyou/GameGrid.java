package de.weareprophet.ihomeyou;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.frice.obj.sub.ImageObject;
import org.frice.obj.sub.ShapeObject;
import org.frice.resource.graphics.ColorResource;
import org.frice.resource.image.ImageResource;
import org.frice.util.shape.FRectangle;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.StringWriter;
import java.io.Writer;


public class GameGrid {
    private Table<Integer, Integer, ImageObject> gameGrid;
    private IHomeYouGame ihyg;
    private Graph<Pair<Integer, Integer>, DefaultEdge> graph;

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

    private Graph<Pair<Integer, Integer>, DefaultEdge> initNoWallGraph() {
        Graph<Pair<Integer, Integer>, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        // add vertices
        for(int c = 0; c <= COLS+1; c++) {
            for (int r = 0; r <= ROWS+1; r++) {
                graph.addVertex(Pair.of(c, r));
            }
        }

        // add edges
        for(int c = 0; c <= COLS+1; c++) {
            for (int r = 0; r <= ROWS+1; r++) {
                if(c+1 <= COLS+1) {
                    graph.addEdge(Pair.of(c, r), Pair.of(c+1, r));
                }

                if(r+1 <= ROWS+1) {
                    graph.addEdge(Pair.of(c, r), Pair.of(c, r+1));
                }
            }
        }
        return graph;
    }

    public boolean setObject(int row, int column, ImageResource res) {
        if(!gameGrid.contains(row, column)) {
            ImageObject obj = new ImageObject(res, SIZE * column + BORDERS + 8, SIZE * row + BORDERS + 8);
            gameGrid.put(row, column, obj);
            ihyg.addObject(obj);
            return true;
        }
        return false;
    }

    public boolean setWall(int row, int column, ImageResource res, WallDirection dir) {

        ImageObject obj = null;
        switch (dir) {
            case TOP:
                obj = new ImageObject(res, SIZE * column, SIZE * row);

                graph.removeEdge(Pair.of(column, row), Pair.of(column+1,row));
                break;
            case BOTTOM:
                obj = new ImageObject(res, 64 + SIZE * column, SIZE * row);

                graph.removeEdge(Pair.of(column, row+1), Pair.of(column+1,row+1));
                break;
            case LEFT:
                obj = new ImageObject(res, SIZE * column, SIZE * row);
                obj.rotate(90);

                graph.removeEdge(Pair.of(column, row), Pair.of(column,row+1));
                break;
            case RIGHT:
                obj = new ImageObject(res, SIZE * column, SIZE * row + 64);
                obj.rotate(90);

                graph.removeEdge(Pair.of(column+1, row), Pair.of(column+1,row+1));
                break;

        }
        ihyg.addObject(obj);
        return false;
    }



    public enum WallDirection {
        TOP, BOTTOM, LEFT, RIGHT
    }

    public void printWallGraph() {
        ComponentNameProvider<Pair<Integer, Integer>> vertexIdProvider = ele -> "V_" + ele.getFirst() + "_" + ele.getSecond();
        ComponentNameProvider<Pair<Integer, Integer>> vertexLabelProvider = ele -> "(" + ele.getFirst() + "," + ele.getSecond() + ")";

        GraphExporter<Pair<Integer, Integer>, DefaultEdge> exporter =
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
