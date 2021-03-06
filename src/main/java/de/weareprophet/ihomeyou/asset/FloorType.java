package de.weareprophet.ihomeyou.asset;

import org.frice.resource.image.ImageResource;

public enum FloorType {
    GRASS ("floor_grass.png"),
    TILE ("floor_tile.png"),
    TILE_KITCHEN ("floor_kitchen.png"),
    WOOD ("floor_wood.png"),
    OFFICE("floor_office.png");

    private String imagePath;

    FloorType(final String imagePath) {
        this.imagePath = imagePath;
    }

    public ImageResource getResource() {
        return ImageResource.fromWeb(getClass().getResource(imagePath).toString());
    }
}
