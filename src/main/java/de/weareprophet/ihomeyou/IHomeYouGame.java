package de.weareprophet.ihomeyou;

import de.weareprophet.ihomeyou.asset.AssetSelector;
import de.weareprophet.ihomeyou.asset.AssetType;
import de.weareprophet.ihomeyou.asset.WallSelector;
import de.weareprophet.ihomeyou.asset.WallType;
import de.weareprophet.ihomeyou.customer.Customer;
import de.weareprophet.ihomeyou.customer.NeedsFulfillment;
import de.weareprophet.ihomeyou.customer.NeedsType;
import de.weareprophet.ihomeyou.datastructure.FurnitureObject;
import de.weareprophet.ihomeyou.datastructure.room.Room;
import de.weareprophet.ihomeyou.datastructure.room.RoomTypes;
import javafx.scene.input.KeyCode;
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
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import static org.frice.Initializer.launch;

public class IHomeYouGame extends Game {
    private static final Logger LOG = LogManager.getLogger(IHomeYouGame.class);
    private static final int MAX_DIFFICULTY = 400;
    private static final ScheduledExecutorService ES = Executors.newScheduledThreadPool(2);
    private WallSelector wallSelector;

    private enum GameState {
        InLevel,
        BetweenLevels,
        GameOver,
        Victory;
    }

    GameGrid grid;
    private Player player;
    private AssetSelector assetSelector;
    private int difficulty;
    private Customer currentCustomer;
    private SimpleText customerNumberOfPpl;
    private Map<NeedsType, SimpleText> customerNeedLabels;
    private SimpleText satisfactionOutput;
    private SimpleText nextLevelOutput;
    private SimpleText prestigeOutput;
    private GameState gameState;


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
        difficulty = 70;
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

        addKeyReleasedEvent(KeyCode.SPACE.getCode(),
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
        addKeyReleasedEvent(KeyCode.W.getCode(), event -> placeWall(wallSelector.getSelected(), GameGrid.WallDirection.TOP));
        addKeyReleasedEvent(KeyCode.A.getCode(), event -> placeWall(wallSelector.getSelected(), GameGrid.WallDirection.LEFT));
        addKeyReleasedEvent(KeyCode.S.getCode(), event -> placeWall(wallSelector.getSelected(), GameGrid.WallDirection.BOTTOM));
        addKeyReleasedEvent(KeyCode.D.getCode(), event -> placeWall(wallSelector.getSelected(), GameGrid.WallDirection.RIGHT));
        addKeyPressedEvent(KeyCode.U.getCode(), event -> {
            final AssetType selectedAsset = assetSelector.getSelected();
            if (!assetSelector.isSelectedAvailable() && player.getSkillPoints() >= selectedAsset.getSkillPoints()) {
                player.spendSkillPoints(selectedAsset.getSkillPoints());
                assetSelector.unlock(selectedAsset);
                LOG.debug("Unlocked asset: {}", selectedAsset.getName());
            } else {
                player.signalMistake(ES);
            }
        });
        addKeyReleasedEvent(KeyCode.ENTER.getCode(), getEvaluationListener());

        nextCustomer(1.0);
        renderCustomerInfo();
    }

    private void nextCustomer(final double satisfactionLevel) {
        currentCustomer = Customer.rngCustomer(difficulty);
        player.addBudget((int) Math.round(currentCustomer.getBudget() * satisfactionLevel));
    }

    @NotNull
    private Consumer<KeyEvent> getEvaluationListener() {
        return event -> {
            if (gameState == GameState.InLevel) {
                grid.calculateRoomAccessibility();
                final NeedsFulfillment.Builder fulfilment = NeedsFulfillment.builder();
                calculateRoomFulfilment(fulfilment);
                double satisfaction = currentCustomer.measureSatisfaction(fulfilment.build());
                LOG.info("Customer satisfaction: {}", satisfaction);
                satisfactionOutput.setText(NumberFormat.getPercentInstance(Locale.ENGLISH).format(satisfaction));
                addObject(nextLevelOutput);
                final ColorResource color;
                if (difficulty == MAX_DIFFICULTY) {
                    gameState = GameState.Victory;
                    nextLevelOutput.setColor(ColorResource.GREEN);
                    player.kill();
                    Arrays.stream(getKeyListeners()).forEach(this::removeKeyListener);
                    nextLevelOutput.setText("You have won the game!");
                }
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
                    color = new ColorResource(156, 240, 0);
                } else {
                    color = ColorResource.GREEN;
                }
                satisfactionOutput.setColor(color);
                if (gameState != GameState.GameOver) {
                    prestigeOutput.setText("+" + currentCustomer.getPrestige() + " skill points");
                    addObject(prestigeOutput);
                    player.addSkillPoints(currentCustomer.getPrestige());
                    difficulty = Math.min(MAX_DIFFICULTY, difficulty + 40);
                    nextCustomer(satisfaction);
                    gameState = GameState.BetweenLevels;
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

    private void calculateRoomFulfilment(NeedsFulfillment.Builder fulfilment) {
        List<Room> rooms = grid.getRoomManager().getRooms();
        for (int i = 1; i < rooms.size(); i++) { // skip the first element bc that's the outside
            final Room r = rooms.get(i);
            Collection<FurnitureObject> assetsInRoom = grid.getAssetsInRoom(r);
            final Map<WallType, Integer> wallCount = new EnumMap<>(WallType.class);
            for (final WallType w : WallType.values()) {
                wallCount.put(w, grid.getNumWallTypeInRoom(w, r));
            }
            int roomSize = r.getTiles().size();
            int assetCount = assetsInRoom.size();
            LOG.debug("Room with {} tiles, {} assets, accessibility {} and walls: {}", roomSize, assetCount, r.isAccessible(), wallCount);
            if (!r.isAccessible()) {
                continue; // ignore inaccessible rooms
            }
            for (final FurnitureObject furniture : assetsInRoom) {
                fulfilment.add(furniture.getType().getNeedsFulfillment());
            }
            rewardRoomConsistency(fulfilment, assetsInRoom, roomSize, assetCount);
            int roomComfort = Math.max(0, wallCount.get(WallType.Solid) * WallType.Solid.getPrice() + wallCount.get(WallType.Window) * WallType.Window.getPrice() - roomSize - wallCount.get(WallType.Door) * 4);
            LOG.debug("Room comfort: {}", roomComfort);
            fulfilment.add(NeedsType.Comfort, roomComfort);
        }
    }

    private void rewardRoomConsistency(NeedsFulfillment.Builder fulfilment, Collection<FurnitureObject> assetsInRoom, int roomSize, int assetCount) {
        final Map<RoomTypes, Integer> typeCount = new EnumMap<>(RoomTypes.class);
        RoomTypes bestType = RoomTypes.HALLWAY;
        int bestCount = 0;
        for (final RoomTypes roomType : RoomTypes.values()) {
            int validAssetCount = 0;
            for (final FurnitureObject asset : assetsInRoom) {
                if (roomType.validRoomAsset(asset.getType())) {
                    validAssetCount++;
                }
            }
            typeCount.put(roomType, validAssetCount);
            if (validAssetCount > bestCount) {
                bestType = roomType;
                bestCount = validAssetCount;
            }
        }

        boolean roomConsistency = true;
        for (final RoomTypes roomType : RoomTypes.values()) {
            if (roomType == bestType) {
                continue;
            }
            if (typeCount.get(roomType) > bestCount) {
                roomConsistency = false;
            }
        }
        LOG.debug("Room has type {} and is consistent: {}", bestType, roomConsistency);
        if (roomConsistency) {
            switch (bestType) {
                case BATH:
                    applyRoomPerk(fulfilment, assetsInRoom, roomSize, assetCount, NeedsType.Cleanliness);
                    break;
                case OFFICE:
                    applyRoomPerk(fulfilment, assetsInRoom, roomSize, assetCount, NeedsType.Work);
                    break;
                case KITCHEN:
                    applyRoomPerk(fulfilment, assetsInRoom, roomSize, assetCount, NeedsType.Food);
                    break;
                case LIVING_ROOM:
                    applyRoomPerk(fulfilment, assetsInRoom, roomSize, assetCount, NeedsType.Comfort);
                    break;
                case BED_ROOM:
                    applyRoomPerk(fulfilment, assetsInRoom, roomSize, assetCount, NeedsType.Personal);
                    break;
                case HALLWAY:
                    fulfilment.add(NeedsType.Space, (roomSize - assetCount) * 5);
                    break;
                default:
            }
        }
    }

    private void applyRoomPerk(NeedsFulfillment.Builder fulfilment, Collection<FurnitureObject> assetsInRoom, int roomSize, int assetCount, NeedsType needsType) {
        for (final FurnitureObject assetInRoom : assetsInRoom) {
            fulfilment.add(needsType, assetInRoom.getType().getNeedsFulfillment().getNeeds().getOrDefault(needsType, 0) * (roomSize - assetCount) / 30);
        }
        fulfilment.add(NeedsType.Space, (roomSize - assetCount) * 5);
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
            if (desireForType == 0) {
                needLabel.setText("None");
                needLabel.setColor(ColorResource.LIGHT_GRAY);
            } else if (desireForType < 60) {
                needLabel.setText("Minimal");
                needLabel.setColor(new ColorResource(0, 213, 106));
            } else if (desireForType < 120) {
                needLabel.setText("Low");
                needLabel.setColor(ColorResource.GREEN);
            } else if (desireForType < 180) {
                needLabel.setText("Moderate");
                needLabel.setColor(new ColorResource(156, 240, 0));
            } else if (desireForType < 240) {
                needLabel.setText("Medium");
                needLabel.setColor(ColorResource.ORANGE);
            } else if (desireForType < 300) {
                needLabel.setText("High");
                needLabel.setColor(ColorResource.RED);
            } else {
                needLabel.setText("Serious");
                needLabel.setColor(new ColorResource(128, 0, 0));
            }
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
