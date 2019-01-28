package de.weareprophet.ihomeyou.asset;

import de.weareprophet.ihomeyou.IHomeYouGame;
import org.frice.obj.button.SimpleText;
import org.frice.obj.sub.ImageObject;

import java.awt.event.KeyEvent;

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
        game.addKeyPressedEvent(KeyEvent.getExtendedKeyCodeForChar('1'), keyEvent -> {
            selected = WallType.Solid;
            updateSelectedTypeDescription();
        });
        game.addKeyPressedEvent(KeyEvent.getExtendedKeyCodeForChar('2'), keyEvent -> {
            selected = WallType.Window;
            updateSelectedTypeDescription();
        });
        game.addKeyPressedEvent(KeyEvent.getExtendedKeyCodeForChar('3'), keyEvent -> {
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
