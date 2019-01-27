package de.weareprophet.ihomeyou.asset;

import de.weareprophet.ihomeyou.GameGrid;
import org.frice.resource.image.ImageResource;

import java.net.URL;

public enum WallType {
    Wall("WallHorizontal", "WallVertical");

    private final String horizontalImage;

    private final String verticalImage;

    WallType(final String horizontalImage, final String verticalImage) {
        this.horizontalImage = horizontalImage;
        this.verticalImage = verticalImage;
    }


    public URL getHorizontalImageUrl() {
        return getClass().getResource(this.horizontalImage + ".png");
    }

    public ImageResource getHorizontalResource() {
        return ImageResource.fromPath(this.getHorizontalImageUrl().getPath());
    }

    public URL getVerticalImageUrl() {
        return getClass().getResource(this.verticalImage + ".png");
    }

    public ImageResource getVerticalResource() {
        return ImageResource.fromPath(this.getVerticalImageUrl().getPath());
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
