package de.weareprophet.ihomeyou.asset;

import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.frice.Game;
import org.frice.obj.sub.ImageObject;
import org.frice.resource.image.ImageResource;

import java.util.Arrays;
import java.util.List;

public class AssetSelector {
    private static final Logger LOG = LogManager.getLogger(AssetSelector.class);
    private final ImageObject imgObj;
    private final List<AssetType> assets = Arrays.asList(AssetType.values());
    private int selected = 0;

    public AssetSelector(final Game game) {
        imgObj = new ImageObject(ImageResource.fromPath(getSelected().getImageUrl().getPath()));
        imgObj.setX(game.getSize().width - 100);
        imgObj.setY(10);
        game.addObject(imgObj);
        game.addKeyTypedEvent(KeyCode.PAGE_DOWN.getCode(), keyEvent -> {
            if (selected < assets.size() - 1) {
                selected++;
                imgObj.setRes(getSelected().getResource());
                LOG.debug("Selected asset changed to: {}", selected);
            }
        });
        game.addKeyTypedEvent(KeyCode.PAGE_UP.getCode(), keyEvent -> {
            if (selected > 0) {
                selected--;
                imgObj.setRes(getSelected().getResource());
                LOG.debug("Selected asset changed to: {}", selected);
            }
        });
    }

    private AssetType getSelected() {
        return assets.get(selected);
    }
}
