package de.weareprophet.ihomeyou;

import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.frice.Game;
import org.frice.anim.rotate.SimpleRotate;
import org.frice.obj.sub.ShapeObject;
import org.frice.resource.graphics.ColorResource;
import org.frice.util.shape.FRectangle;

import static org.frice.Initializer.launch;

public class IHomeYouGame extends Game {
    private static final Logger LOG = LogManager.getLogger(IHomeYouGame.class);
    private ShapeObject player;

    GameGrid grid;


    public static void main(String[] args) {
        launch(IHomeYouGame.class);
    }


    @Override
    public void onInit() {
        player = new ShapeObject(ColorResource.DARK_GRAY, new FRectangle(GameGrid.SIZE - 2 * GameGrid.BORDERS, GameGrid.SIZE - 2 * GameGrid.BORDERS));
        setSize(1366, 720);
        setBounds(0, 0, 1366, 720);
        setLocation(0, 0);
        grid = new GameGrid(this);

        addObject(player);
        player.setX(GameGrid.BORDERS * 2);
        player.setY(GameGrid.BORDERS * 2);
        SimpleRotate rotate = new SimpleRotate(2);
        player.addAnim(rotate);

        addKeyPressedEvent(KeyCode.RIGHT.getCode(), event -> player.move(GameGrid.SIZE, 0));
        addKeyPressedEvent(KeyCode.LEFT.getCode(), event -> player.move(-GameGrid.SIZE, 0));
        addKeyPressedEvent(KeyCode.UP.getCode(), event -> player.move(0, -GameGrid.SIZE));
        addKeyPressedEvent(KeyCode.DOWN.getCode(), event -> player.move(0, GameGrid.SIZE));
    }

    @Override
    public void onRefresh() {
        super.onRefresh();


    }

    @Override
    public void onExit() {
        System.exit(0);
    }
}
