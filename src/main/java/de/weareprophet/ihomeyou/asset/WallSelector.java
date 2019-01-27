package de.weareprophet.ihomeyou.asset;

import de.weareprophet.ihomeyou.IHomeYouGame;
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
    }

    private void updateSelectedTypeDescription() {
        selectedWallDescription.setText(selected.getLabel() + ": € " + selected.getPrice());
    }

    public WallType getSelected() {
        return selected;
    }
}
