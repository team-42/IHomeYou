package de.weareprophet.ihomeyou;

import de.weareprophet.ihomeyou.asset.AssetSelector;
import de.weareprophet.ihomeyou.asset.AssetType;
import de.weareprophet.ihomeyou.asset.WallType;
import de.weareprophet.ihomeyou.customer.Customer;
import de.weareprophet.ihomeyou.customer.NeedsFulfillment;
import de.weareprophet.ihomeyou.customer.NeedsType;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.frice.Game;
import org.frice.obj.button.SimpleText;
import org.frice.resource.graphics.ColorResource;
import org.frice.resource.image.ImageResource;

import java.text.NumberFormat;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import static org.frice.Initializer.launch;

public class IHomeYouGame extends Game {
    private static final Logger LOG = LogManager.getLogger(IHomeYouGame.class);

    GameGrid grid;
    private Player player;
    private AssetSelector assetSelector;
    private Customer customer;
    private SimpleText customerNumberOfPpl;
    private Map<NeedsType, SimpleText> customerNeedLabels;
    private SimpleText satisfactionOutput;


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
        customer = Customer.easyCustomer();
        initNeedLabels();
        assetSelector = new AssetSelector(this);
        addObject(new SimpleText("Current customer information", getXOfRightColumn(), 80));
        addObject(new SimpleText("Number of people: ", getXOfRightColumn(), 110));
        customerNumberOfPpl = new SimpleText("", getXOfRightColumn() + 170, 110);
        addObject(customerNumberOfPpl);
        addObject(new SimpleText("Customer satisfaction: ", getXOfRightColumn(), 340));
        satisfactionOutput = new SimpleText(ColorResource.LIGHT_GRAY, "Press Enter to evaluate", getXOfRightColumn(), 360);
        addObject(satisfactionOutput);

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
                    double satisfaction = customer.measureSatisfaction(fulfilment.build());
                    LOG.info("Customer satisfaction: {}", satisfaction);
                    satisfactionOutput.setText(NumberFormat.getPercentInstance(Locale.ENGLISH).format(satisfaction));
                    final ColorResource color;
                    if (satisfaction < 0.2) {
                        color = ColorResource.RED;
                    } else if (satisfaction < 0.5) {
                        color = ColorResource.ORANGE;
                    } else if (satisfaction < 0.8) {
                        color = new ColorResource(156, 240, 0);
                    } else {
                        color = ColorResource.GREEN;
                    }
                    satisfactionOutput.setColor(color);
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

        renderCustomerInfo();
    }

    private void initNeedLabels() {
        customerNeedLabels = new EnumMap<>(NeedsType.class);
        int yOffset = 130;
        for (final NeedsType t : NeedsType.values()) {
            addObject(new SimpleText(t.getLabel() + " need:", getXOfRightColumn(), yOffset));
            SimpleText needIntensityLabel = new SimpleText("", getXOfRightColumn() + 170, yOffset);
            customerNeedLabels.put(t, needIntensityLabel);
            addObject(needIntensityLabel);
            yOffset += 20;
        }
    }

    private void renderCustomerInfo() {
        customerNumberOfPpl.setText(String.valueOf(customer.getNumberOfPpl()));
        Map<NeedsType, Integer> customerDesires = customer.getDesire().getNeeds();
        for (final NeedsType t : NeedsType.values()) {
            final Integer desireForType = customerDesires.getOrDefault(t, 0);
            SimpleText needLabel = customerNeedLabels.get(t);
            if (desireForType == 0) {
                needLabel.setText("None");
                needLabel.setColor(ColorResource.LIGHT_GRAY);
            } else if (desireForType < 50) {
                needLabel.setText("Low");
                needLabel.setColor(ColorResource.GREEN);
            } else if (desireForType < 100) {
                needLabel.setText("Medium");
                needLabel.setColor(ColorResource.ORANGE);
            } else {
                needLabel.setText("High");
                needLabel.setColor(ColorResource.RED);
            }
        }
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
