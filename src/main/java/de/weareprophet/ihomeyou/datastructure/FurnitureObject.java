package de.weareprophet.ihomeyou.datastructure;

import de.weareprophet.ihomeyou.asset.AssetType;
import org.frice.obj.sub.ImageObject;

public class FurnitureObject {
    private AssetType type;
    private ImageObject obj;

    public FurnitureObject(AssetType type, ImageObject obj) {
        this.type = type;
        this.obj = obj;
    }

    public static FurnitureObject of(AssetType type, ImageObject obj) {
        return new FurnitureObject(type, obj);
    }

    public AssetType getType() {
        return type;
    }

    public ImageObject getObj() {
        return obj;
    }
}
