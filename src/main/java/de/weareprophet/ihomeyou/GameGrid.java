package de.weareprophet.ihomeyou;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.frice.obj.sub.ImageObject;
import org.frice.obj.sub.ShapeObject;
import org.frice.resource.graphics.ColorResource;
import org.frice.util.shape.FRectangle;


class GameGrid {
    private Table<Integer, Integer, ImageObject> gameGrid;
    private IHomeYouGame ihyg;

    static final int SIZE = 64;
    static final int BORDERS = 10;

    static final int COLS = 20;
    static final int ROWS = 10;

    GameGrid(IHomeYouGame iHomeYouGame) {
        gameGrid = HashBasedTable.create();
        ihyg = iHomeYouGame;

        ShapeObject gameGrid = new ShapeObject(ColorResource.LIGHT_GRAY, new FRectangle(COLS * SIZE, ROWS * SIZE));
        gameGrid.setX(BORDERS);
        gameGrid.setY(BORDERS);
        ihyg.addObject(gameGrid);
    }

    public boolean setObject(int row, int column, ImageObject obj) {
        if(!gameGrid.contains(row, column)) {
            gameGrid.put(row, column, obj);
            ihyg.addObject(obj);

            return true;
        }
        return false;
    }

    public void refresh() {

    }
}
