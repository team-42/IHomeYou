package de.weareprophet.ihomeyou.asset;

import org.frice.resource.image.ImageResource;

import java.net.URL;

public enum AssetType {
    Shelf("Shelf", "Stores anything.", "shelf", 60),
    Bench("Bench", "Provides a nice rest.", "bench", 30),
    Wardrobe("Wardrobe", "Stores clothes.", "wardrobe", 120),
    Music("Home Theatre", "Pure entertainment.", "music-home-theatre", 500),
    KitchenTable("Kitchen table", "Used to sit and eat.", "kitchen-table", 60),
    Curtains("Curtains", "Make the room more cozy.", "curtains", 40),
    PrayerCarpet("Prayer carpet", "Used to pray.", "prayer-carpet", 20),
    BookShelf("Bookshelf", "Stores books.", "bookshelf", 40),
    DinnerTable("Dinner table", "Classy way to eat.", "dinner-table", 120);
    private final String name;
    private final String description;
    private final String imageName;
    private final int price;


    AssetType(final String name, final String description, final String imageName, final int price) {
        this.name = name;
        this.description = description;
        this.imageName = imageName;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
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
