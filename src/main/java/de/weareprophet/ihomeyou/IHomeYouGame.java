package de.weareprophet.ihomeyou;

import de.weareprophet.ihomeyou.asset.AssetSelector;
import de.weareprophet.ihomeyou.asset.AssetType;
import de.weareprophet.ihomeyou.asset.WallType;
import de.weareprophet.ihomeyou.customer.Customer;
import de.weareprophet.ihomeyou.customer.NeedsFulfillment;
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
    private Customer customer = Customer.easyCustomer();


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
                    if (assetSelector.isSelectedAvailable() && player.canPay(selectedAsset.getPrice()) && grid.setObject(
                            player.getRow(),
                            player.getColumn(),
                            selectedAsset)) {
                        player.pay(selectedAsset.getPrice());
                    } else {
                        player.signalMistake();
                    }
                });
        addKeyReleasedEvent(KeyCode.ENTER.getCode(),
                event -> {
                    final NeedsFulfillment.Builder fulfilment = NeedsFulfillment.builder();
                    for (final AssetType asset : grid.getAssetsInGrid()) {
                        fulfilment.add(asset.getNeedsFulfillment());
                    }
                    LOG.info("Customer satisfaction: {}", customer.measureSatisfaction(fulfilment.build()));
                });

        addKeyReleasedEvent(KeyCode.W.getCode(), event -> placeWall(WallType.Horizontal.getResource(), GameGrid.WallDirection.TOP));
        addKeyReleasedEvent(KeyCode.A.getCode(), event -> placeWall(WallType.Vertical.getResource(), GameGrid.WallDirection.LEFT));
        addKeyReleasedEvent(KeyCode.S.getCode(), event -> placeWall(WallType.Horizontal.getResource(), GameGrid.WallDirection.BOTTOM));
        addKeyReleasedEvent(KeyCode.D.getCode(), event -> placeWall(WallType.Vertical.getResource(), GameGrid.WallDirection.RIGHT));
        addKeyPressedEvent(KeyCode.U.getCode(), event -> {
            final AssetType selectedAsset = assetSelector.getSelected();
            if (!assetSelector.isSelectedAvailable() && player.getSkillPoints() >= selectedAsset.getSkillPoints()) {
                player.spendSkillPoints(selectedAsset.getSkillPoints());
                assetSelector.unlock(selectedAsset);
                LOG.debug("Unlocked asset: {}", selectedAsset.getName());
            }
        });
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
