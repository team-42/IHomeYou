package de.weareprophet.ihomeyou;

import javafx.scene.input.KeyCode;
import org.frice.Game;
import org.frice.anim.rotate.SimpleRotate;
import org.frice.obj.sub.ShapeObject;
import org.frice.resource.graphics.ColorResource;
import org.frice.util.shape.FRectangle;

class Player {

    private final ShapeObject shape;

    private int row = 0;

    private int column = 0;

    Player(Game game) {
        shape = new ShapeObject(ColorResource.DARK_GRAY, new FRectangle(GameGrid.SIZE - 2 * GameGrid.BORDERS, GameGrid.SIZE - 2 * GameGrid.BORDERS));
        game.addObject(shape);
        shape.setX(GameGrid.BORDERS * 2);
        shape.setY(GameGrid.BORDERS * 2);
        SimpleRotate rotate = new SimpleRotate(2);
        shape.addAnim(rotate);

        game.addKeyPressedEvent(KeyCode.RIGHT.getCode(), event -> {
            if (column < GameGrid.COLS - 1) {
                shape.move(GameGrid.SIZE, 0);
                column++;
            }
        });
        game.addKeyPressedEvent(KeyCode.LEFT.getCode(), event -> {
            if (column > 0) {
                shape.move(-GameGrid.SIZE, 0);
                column--;
            }
        });
        game.addKeyPressedEvent(KeyCode.UP.getCode(), event -> {
            if (row > 0) {
                shape.move(0, -GameGrid.SIZE);
                row--;
            }
        });
        game.addKeyPressedEvent(KeyCode.DOWN.getCode(), event -> {
            if (row < GameGrid.ROWS - 1) {
                shape.move(0, GameGrid.SIZE);
                row++;
            }
        });
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
