package de.weareprophet.ihomeyou.asset;

import org.frice.resource.image.ImageResource;

import java.net.URL;

public enum WallType {
    Horizontal("WallHorizontal"),
    Vertical("WallVertical");

    private final String imageName;

    WallType(final String imageFile) {
        this.imageName = imageFile;
    }


    public URL getImageUrl() {
        return getClass().getResource(this.imageName + ".png");
    }

    public ImageResource getResource() {
        return ImageResource.fromPath(this.getImageUrl().getPath());
    }
}
