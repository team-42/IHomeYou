package de.weareprophet.ihomeyou.asset;

import org.frice.resource.image.ImageResource;

import java.net.URL;

public enum AssetType {
    Shelf("Shelf", "Stores anything.", "shelf"),
    Bench("Bench", "Provides a nice rest.", "bench"),
    Wardrobe("Wardrobe", "Stores clothes.", "wardrobe"),
    Music("Home Theatre", "Pure entertainment.", "music-home-theatre"),
    KitchenTable("Kitchen table", "Used to sit and eat.", "kitchen-table"),
    Curtains("Curtains", "Make the room more cozy.", "curtains"),
    PrayerCarpet("Prayer carpet", "Used to pray.", "prayer-carpet"),
    BookShelf("Bookshelf", "Stores books.", "bookshelf"),
    DinnerTable("Dinner table", "Classy way to eat.", "dinner-table");
    private final String name;
    private final String description;
    private final String imageName;


    AssetType(final String name, final String description, final String imageName) {
        this.name = name;
        this.description = description;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ImageResource getResource() {
        return ImageResource.fromPath(this.getImageUrl().getPath());
    }

    public ImageResource getLargeResource() {
        return ImageResource.fromPath(this.getLargeImageUrl().getPath());
    }

    public URL getImageUrl() {
        return getClass().getResource(this.imageName + ".png");
    }

    public URL getLargeImageUrl() {
        return getClass().getResource(this.imageName + "_L.png");
    }

}
