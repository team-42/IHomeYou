package de.weareprophet.ihomeyou.asset;

import de.weareprophet.ihomeyou.IHomeYouGame;
import org.frice.obj.button.SimpleText;
import org.frice.obj.sub.ImageObject;

public class WallSelector {

    private final SimpleText selectedWallDescription;
    private final ImageObject selectedWallImage;

    public WallSelector(IHomeYouGame game) {
        selectedWallDescription = new SimpleText("Wall: € 0", game.getXOfRightColumn() + 180, 20);
        game.addObject(selectedWallDescription);
        selectedWallImage = new ImageObject(WallType.Wall.getHorizontalResource(), game.getXOfRightColumn() + 180, 30);
        game.addObject(selectedWallImage);
    }
}
