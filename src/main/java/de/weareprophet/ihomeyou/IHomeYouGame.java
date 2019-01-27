package de.weareprophet.ihomeyou;

import de.weareprophet.ihomeyou.asset.AssetSelector;
import de.weareprophet.ihomeyou.asset.AssetType;
import de.weareprophet.ihomeyou.asset.WallType;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.frice.Game;
import org.frice.resource.image.ImageResource;

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
                    final AssetType selectedAsset = assetSelector.getSelected();
                    LOG.debug("New {} placed at row {} col {}", selectedAsset.name(), player.getRow(), player.getColumn());
                    if (assetSelector.isSelectedAvailable() && player.pay(selectedAsset.getPrice())) {
                        grid.setObject(
                                player.getRow(),
                                player.getColumn(),
                                selectedAsset);
                    } else {
                        player.signalMistake();
                    }
                });

        addKeyReleasedEvent(KeyCode.W.getCode(), event -> placeWall(WallType.Horizontal.getResource(), GameGrid.WallDirection.TOP));
        addKeyReleasedEvent(KeyCode.A.getCode(), event -> placeWall(WallType.Vertical.getResource(), GameGrid.WallDirection.LEFT));
        addKeyReleasedEvent(KeyCode.S.getCode(), event -> placeWall(WallType.Horizontal.getResource(), GameGrid.WallDirection.BOTTOM));
        addKeyReleasedEvent(KeyCode.D.getCode(), event -> placeWall(WallType.Vertical.getResource(), GameGrid.WallDirection.RIGHT));
    }

    private void placeWall(ImageResource wallType, GameGrid.WallDirection wallDirection) {
        grid.setWall(player.getRow(), player.getColumn(), wallType, wallDirection);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();


    }

    @Override
    public void onExit() {
        System.exit(0);
    }

    public int getXOfRightColumn() {
        return GameGrid.COLS * GameGrid.SIZE + 2 * GameGrid.BORDERS;
    }
}
