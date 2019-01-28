package de.weareprophet.ihomeyou.asset;

import de.weareprophet.ihomeyou.GameGrid;
import org.frice.resource.image.ImageResource;

import java.net.URL;

public enum WallType {
    Solid("Wall", "WallHorizontal", "WallVertical", 3),
    Door("Door", "wall_horizontal_stone_door", "wall_vertical_stone_door", 20),
    Window("Window", "wall_horizontal_stone_window", "wall_vertical_stone_window", 10);

    private final String label;
    private final String horizontalImage;

    private final String verticalImage;
    private final int price;

    WallType(final String label, final String horizontalImage, final String verticalImage, final int price) {
        this.label = label;
        this.horizontalImage = horizontalImage;
        this.verticalImage = verticalImage;
        this.price = price;
    }


    public URL getHorizontalImageUrl() {
        return getClass().getResource(this.horizontalImage + ".png");
    }

    public ImageResource getHorizontalResource() {
        return ImageResource.fromWeb(this.getHorizontalImageUrl().toString());
    }

    public URL getVerticalImageUrl() {
        return getClass().getResource(this.verticalImage + ".png");
    }

    public ImageResource getVerticalResource() {
        return ImageResource.fromWeb(this.getVerticalImageUrl().toString());
    }

    public int getPrice() {
        return price;
    }

    public String getLabel() {
        return label;
    }

    public ImageResource getResource(GameGrid.WallDirection wallDirection) {
        switch (wallDirection) {
            case TOP:
            case BOTTOM:
                return getHorizontalResource();
            case LEFT:
            case RIGHT:
                return getVerticalResource();
            default:
                throw new AssertionError();
        }
    }
}
