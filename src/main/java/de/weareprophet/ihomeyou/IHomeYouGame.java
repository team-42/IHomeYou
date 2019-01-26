package de.weareprophet.ihomeyou;

import de.weareprophet.ihomeyou.asset.AssetSelector;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.frice.Game;
import org.frice.obj.sub.ImageObject;

import static org.frice.Initializer.launch;

public class IHomeYouGame extends Game {
    private static final Logger LOG = LogManager.getLogger(IHomeYouGame.class);

    GameGrid grid;
    private Player player;
    private AssetSelector assetSelector;


    public static void main(String[] args) {
        launch(IHomeYouGame.class);
    }


    @Override
    public void onInit() {
        setSize(1366, 720);
        setBounds(0, 0, 1366, 720);
        setLocation(0, 0);
        setTitle("I Home You!");
        grid = new GameGrid(this);
        player = new Player(this);
        assetSelector = new AssetSelector(this);

        addKeyReleasedEvent(KeyCode.SPACE.getCode(),
                event -> {
                    LOG.debug("New {} placed at row {} col {}", assetSelector.getSelected().name(), player.getRow(), player.getColumn());
                    grid.setObject(
                            player.getRow(),
                            player.getColumn(),
                            assetSelector.getSelected().getResource());
                });
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
