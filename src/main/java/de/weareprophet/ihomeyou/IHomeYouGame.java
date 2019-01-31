package de.weareprophet.ihomeyou;

import de.weareprophet.ihomeyou.asset.AssetSelector;
import de.weareprophet.ihomeyou.asset.AssetType;
import de.weareprophet.ihomeyou.asset.WallSelector;
import de.weareprophet.ihomeyou.asset.WallType;
import de.weareprophet.ihomeyou.customer.Customer;
import de.weareprophet.ihomeyou.customer.DifficultyModel;
import de.weareprophet.ihomeyou.customer.NeedsFulfillment;
import de.weareprophet.ihomeyou.customer.NeedsType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.frice.Game;
import org.frice.obj.button.SimpleText;
import org.frice.obj.sub.ShapeObject;
import org.frice.resource.graphics.ColorResource;
import org.frice.util.shape.FRectangle;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import static org.frice.Initializer.launch;

public class IHomeYouGame extends Game {
    private static final Logger LOG = LogManager.getLogger(IHomeYouGame.class);
    private static final ScheduledExecutorService ES = Executors.newScheduledThreadPool(2);
    private static final ColorResource YELLOW_GREEN = new ColorResource(156, 240, 0);
    private WallSelector wallSelector;

    private enum GameState {
        InLevel,
        BetweenLevels,
        GameOver,
        Victory;
    }

    private DifficultyModel difficultyModel;
    private GameGrid grid;
    private Player player;
    private AssetSelector assetSelector;
    private Customer currentCustomer;
    private SimpleText customerNumberOfPpl;
    private Map<NeedsType, SimpleText> customerNeedLabels;
    private SimpleText satisfactionOutput;
    private SimpleText nextLevelOutput;
    private SimpleText prestigeOutput;
    private GameState gameState;


    public static void main(String[] args) {
        launch(new IHomeYouGame());
    }


    @Override
    public void onInit() {
        setSize(1366, 720);
        setBounds(0, 0, 1366, 720);
        setLocation(0, 0);
        setTitle("I Home You!");
        grid = new GameGrid(this);
        difficultyModel = new DifficultyModel(430, 70, 10, 1.6f, 1.8f, 6, 80, 30, 50, 70);
        player = new Player(this);
        gameState = GameState.InLevel;
        initNeedLabels();
        assetSelector = new AssetSelector(this);
        wallSelector = new WallSelector(this);
        addObject(new SimpleText("Current customer information", getXOfRightColumn(), 80));
        addObject(new SimpleText("Number of people: ", getXOfRightColumn(), 110));
        customerNumberOfPpl = new SimpleText("", getXOfRightColumn() + 170, 110);
        addObject(customerNumberOfPpl);
        addObject(new SimpleText("Customer satisfaction: ", getXOfRightColumn(), 340));
        satisfactionOutput = new SimpleText(ColorResource.LIGHT_GRAY, "Press Enter to evaluate", getXOfRightColumn(), 360);
        addObject(satisfactionOutput);
        prestigeOutput = new SimpleText(ColorResource.GREEN, "", getXOfRightColumn() + 50, 360);
        nextLevelOutput = new SimpleText(ColorResource.RED, "Press Enter for next customer", getXOfRightColumn(), 380);
        addObject(new ShapeObject(ColorResource.DARK_GRAY, new FRectangle(300, 2), getXOfRightColumn(), 60));
        addObject(new ShapeObject(ColorResource.DARK_GRAY, new FRectangle(300, 2), getXOfRightColumn(), 400));

        addKeyReleasedEvent(KeyEvent.VK_SPACE,
                event -> {
                    final AssetType selectedAsset = assetSelector.getSelected();
                    LOG.debug("New {} placed at row {} col {}", selectedAsset.name(), player.getRow(), player.getColumn());
                    if (gameState == GameState.InLevel && assetSelector.isSelectedAvailable() && player.canPay(selectedAsset.getPrice()) && grid.setFurniture(
                            player.getRow(),
                            player.getColumn(),
                            selectedAsset)) {
                        player.pay(selectedAsset.getPrice());
                    } else {
                        player.signalMistake(ES);
                    }
                });
        addKeyReleasedEvent(KeyEvent.getExtendedKeyCodeForChar('W'), event -> placeWall(wallSelector.getSelected(), GameGrid.WallDirection.TOP));
        addKeyReleasedEvent(KeyEvent.getExtendedKeyCodeForChar('A'), event -> placeWall(wallSelector.getSelected(), GameGrid.WallDirection.LEFT));
        addKeyReleasedEvent(KeyEvent.getExtendedKeyCodeForChar('S'), event -> placeWall(wallSelector.getSelected(), GameGrid.WallDirection.BOTTOM));
        addKeyReleasedEvent(KeyEvent.getExtendedKeyCodeForChar('D'), event -> placeWall(wallSelector.getSelected(), GameGrid.WallDirection.RIGHT));
        addKeyPressedEvent(KeyEvent.getExtendedKeyCodeForChar('U'), event -> {
            final AssetType selectedAsset = assetSelector.getSelected();
            if (!assetSelector.isSelectedAvailable() && player.getSkillPoints() >= selectedAsset.getSkillPoints()) {
                player.spendSkillPoints(selectedAsset.getSkillPoints());
                assetSelector.unlock(selectedAsset);
                LOG.debug("Unlocked asset: {}", selectedAsset.getName());
            } else {
                player.signalMistake(ES);
            }
        });
        addKeyReleasedEvent(KeyEvent.VK_ENTER, getEvaluationListener());

        nextCustomer(1.0);
        renderCustomerInfo();
    }

    private void nextCustomer(final double satisfactionLevel) {
        currentCustomer = difficultyModel.nextCustomer();
        player.addBudget((int) Math.round(currentCustomer.getBudget() * satisfactionLevel));
    }

    @NotNull
    private Consumer<KeyEvent> getEvaluationListener() {
        return event -> {
            if (gameState == GameState.InLevel) {
                grid.calculateRoomAccessibility();
                final NeedsFulfillment.Builder fulfilment = NeedsFulfillment.builder();
                new ScoringHelper(grid, difficultyModel).calculateRoomFulfilment(fulfilment);
                double satisfaction = currentCustomer.measureSatisfaction(fulfilment.build());
                LOG.info("Customer satisfaction: {}", satisfaction);
                satisfactionOutput.setText(NumberFormat.getPercentInstance(Locale.ENGLISH).format(satisfaction));
                addObject(nextLevelOutput);
                final ColorResource color;
                if (satisfaction < 0.2) {
                    color = ColorResource.RED;
                    gameState = GameState.GameOver;
                    nextLevelOutput.setColor(ColorResource.RED);
                    player.kill();
                    Arrays.stream(getKeyListeners()).forEach(this::removeKeyListener);
                    nextLevelOutput.setText("GAME OVER!");
                } else if (satisfaction < 0.5) {
                    color = ColorResource.ORANGE;
                } else if (satisfaction < 0.8) {
                    color = YELLOW_GREEN;
                } else {
                    color = ColorResource.GREEN;
                }
                satisfactionOutput.setColor(color);
                if (gameState != GameState.GameOver) {
                    nextCustomer(satisfaction);
                    if (currentCustomer == null) {
                        gameState = GameState.Victory;
                        nextLevelOutput.setColor(ColorResource.GREEN);
                        player.kill();
                        Arrays.stream(getKeyListeners()).forEach(this::removeKeyListener);
                        nextLevelOutput.setText("You have won the game!");
                    } else {
                        prestigeOutput.setText("+" + currentCustomer.getPrestige() + " skill points");
                        addObject(prestigeOutput);
                        player.addSkillPoints(currentCustomer.getPrestige());
                        gameState = GameState.BetweenLevels;
                    }
                }
            } else if (gameState == GameState.BetweenLevels) {
                satisfactionOutput.setColor(ColorResource.LIGHT_GRAY);
                satisfactionOutput.setText("Press Enter to evaluate");
                removeObject(prestigeOutput);
                removeObject(nextLevelOutput);
                grid.resetGameGrid();
                gameState = GameState.InLevel;
                renderCustomerInfo();
            }

        };
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
        customerNumberOfPpl.setText(String.valueOf(currentCustomer.getNumberOfPpl()));
        Map<NeedsType, Integer> customerDesires = currentCustomer.getDesire().getNeeds();
        for (final NeedsType t : NeedsType.values()) {
            final Integer desireForType = customerDesires.getOrDefault(t, 0);
            SimpleText needLabel = customerNeedLabels.get(t);
            final DifficultyModel.NeedIntensityLabel needIntensity = DifficultyModel.NeedIntensityLabel.getForNeedIntensity(difficultyModel, t, desireForType);
            needLabel.setText(needIntensity.getName());
            needLabel.setColor(needIntensity.getColor());
        }
    }

    private void placeWall(WallType wallType, GameGrid.WallDirection wallDirection) {
        if (gameState == GameState.InLevel && player.canPay(wallType.getPrice())
                && grid.setWall(player.getRow(), player.getColumn(), wallType, wallDirection)) {
            player.pay(wallType.getPrice());
        } else {
            player.signalMistake(ES);
        }
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
