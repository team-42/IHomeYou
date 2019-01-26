package de.weareprophet.ihomeyou.asset;

import de.weareprophet.ihomeyou.IHomeYouGame;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.frice.obj.button.SimpleText;
import org.frice.obj.sub.ImageObject;
import org.frice.resource.graphics.ColorResource;

import java.util.Arrays;
import java.util.List;

public class AssetSelector {
    private static final Logger LOG = LogManager.getLogger(AssetSelector.class);
    private static final int Y_OFFSET = 440;
    private final ImageObject imgObj;
    private final List<AssetType> assets = Arrays.asList(AssetType.values());
    private final SimpleText selectedName;
    private final SimpleText selectedDesc;
    private final SimpleText price;
    private int selected = 0;

    public AssetSelector(final IHomeYouGame game) {
        imgObj = new ImageObject(getSelected().getLargeResource());
        imgObj.setX(game.getXOfRightColumn());
        imgObj.setY(Y_OFFSET + 30);
        game.addObject(imgObj);
        selectedName = new SimpleText(getSelected().getName(), game.getXOfRightColumn() + 150, Y_OFFSET);
        game.addObject(selectedName);
        selectedDesc = new SimpleText(getSelected().getDescription(), game.getXOfRightColumn(), Y_OFFSET + 180);
        game.addObject(selectedDesc);
        game.addObject(new SimpleText("Price: € ", game.getXOfRightColumn(), Y_OFFSET + 200));
        price = new SimpleText(String.valueOf(getSelected().getPrice()), game.getXOfRightColumn() + 80, Y_OFFSET + 200);
        game.addObject(price);
        game.addKeyPressedEvent(KeyCode.CONTROL.getCode(), keyEvent -> {
            selected = (selected + 1) % assets.size();
            imgObj.setRes(getSelected().getLargeResource());
            selectedName.setText(getSelected().getName());
            selectedDesc.setText(getSelected().getDescription());
            price.setText(String.valueOf(getSelected().getPrice()));
            LOG.debug("Selected asset changed to: {}", selected);
        });
        game.addObject(new SimpleText(ColorResource.BLACK, "Selected object:", game.getXOfRightColumn(), Y_OFFSET));
    }

    public AssetType getSelected() {
        return assets.get(selected);
    }
}
