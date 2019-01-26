package de.weareprophet.ihomeyou.asset;

import org.frice.resource.image.ImageResource;

import java.net.URL;

public enum AssetType {
    WallL("wall_left.png", 1, 1),
    WallR("wall_right.png", 1, 1),
    WallB("wall_bottom.png", 1, 1),
    WallT("wall_top.png", 1, 1),
    WallBL("wall_bottomleft.png", 1, 1),
    WallBR("wall_bottomright.png", 1, 1),
    WallTL("wall_topleft.png", 1, 1),
    WallTR("wall_topright.png", 1, 1);

    private final String imageName;
    private final int rowTileCount;

    private final int colTileCount;


    AssetType(final String imageName, final int rowTileCount, final int colTileCount) {
        this.imageName = imageName;
        this.rowTileCount = rowTileCount;
        this.colTileCount = colTileCount;
    }

    public URL getImageUrl() {
        return getClass().getResource(this.imageName);
    }

    ImageResource getResource() {
        return ImageResource.fromPath(this.getImageUrl().getPath());
    }

}
