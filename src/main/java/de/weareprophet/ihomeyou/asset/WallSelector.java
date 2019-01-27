package de.weareprophet.ihomeyou.asset;

import de.weareprophet.ihomeyou.IHomeYouGame;
import javafx.scene.input.KeyCode;
import org.frice.obj.button.SimpleText;
import org.frice.obj.sub.ImageObject;

public class WallSelector {

    private final SimpleText selectedWallDescription;
    private final ImageObject selectedWallImage;

    private WallType selected;

    public WallSelector(IHomeYouGame game) {
        selected = WallType.Solid;
        selectedWallDescription = new SimpleText("", game.getXOfRightColumn() + 180, 20);
        game.addObject(selectedWallDescription);
        selectedWallImage = new ImageObject(selected.getHorizontalResource(), game.getXOfRightColumn() + 180, 30);
        game.addObject(selectedWallImage);
        updateSelectedTypeDescription();
        game.addKeyPressedEvent(KeyCode.DIGIT1.getCode(), keyEvent -> {
            selected = WallType.Solid;
            updateSelectedTypeDescription();
        });
        game.addKeyPressedEvent(KeyCode.DIGIT2.getCode(), keyEvent -> {
            selected = WallType.Window;
            updateSelectedTypeDescription();
        });
        game.addKeyPressedEvent(KeyCode.DIGIT3.getCode(), keyEvent -> {
            selected = WallType.Door;
            updateSelectedTypeDescription();
        });
    }

    private void updateSelectedTypeDescription() {
        selectedWallDescription.setText(selected.getLabel() + ": € " + selected.getPrice());
        selectedWallImage.setRes(selected.getHorizontalResource());
    }

    public WallType getSelected() {
        return selected;
    }
}
