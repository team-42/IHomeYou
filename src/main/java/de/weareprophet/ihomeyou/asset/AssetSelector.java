package de.weareprophet.ihomeyou.asset;

import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.frice.Game;
import org.frice.obj.button.SimpleText;
import org.frice.obj.sub.ImageObject;
import org.frice.resource.graphics.ColorResource;

import java.util.Arrays;
import java.util.List;

public class AssetSelector {
    private static final Logger LOG = LogManager.getLogger(AssetSelector.class);
    private final ImageObject imgObj;
    private final List<AssetType> assets = Arrays.asList(AssetType.values());
    private int selected = 0;
    private final SimpleText selectedName;
    private final SimpleText selectedDesc;

    public AssetSelector(final Game game) {
        imgObj = new ImageObject(getSelected().getLargeResource());
        imgObj.setX(game.getSize().width - 300);
        imgObj.setY(400);
        selectedName = new SimpleText(getSelected().getName(), game.getSize().width - 150, 370);
        game.addObject(selectedName);
        selectedDesc = new SimpleText(getSelected().getDescription(), game.getSize().width - 300, 550);
        game.addObject(selectedDesc);
        game.addObject(imgObj);
        game.addKeyPressedEvent(KeyCode.CONTROL.getCode(), keyEvent -> {
            selected = (selected + 1) % assets.size();
            imgObj.setRes(getSelected().getLargeResource());
            selectedName.setText(getSelected().getName());
            selectedDesc.setText(getSelected().getDescription());
            LOG.debug("Selected asset changed to: {}", selected);
        });
        game.addObject(new SimpleText(ColorResource.BLACK, "Selected object:", game.getSize().width - 300, 370));
    }

    public AssetType getSelected() {
        return assets.get(selected);
    }
}
