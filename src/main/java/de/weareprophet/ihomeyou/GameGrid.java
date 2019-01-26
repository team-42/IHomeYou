package de.weareprophet.ihomeyou;

import org.frice.obj.sub.ShapeObject;
import org.frice.resource.graphics.ColorResource;
import org.frice.util.shape.FRectangle;


class GameGrid {

    static final int SIZE = 50;
    static final int BORDERS = 10;

    static final int COLS = 20;
    static final int ROWS = 10;

    GameGrid(IHomeYouGame iHomeYouGame) {
        ShapeObject gameGrid = new ShapeObject(ColorResource.LIGHT_GRAY, new FRectangle(COLS * SIZE, ROWS * SIZE));
        gameGrid.setX(BORDERS);
        gameGrid.setY(BORDERS);
        iHomeYouGame.addObject(gameGrid);
    }

}
