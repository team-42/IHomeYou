package de.weareprophet.ihomeyou.asset;

import org.frice.resource.image.ImageResource;

import java.net.URL;

public enum AssetType {
    Shelf("Shelf", "Stores anything.", "shelf", 40, 0),
    Bench("Bench", "Provides a nice rest.", "bench", 30, 0),
    Wardrobe("Wardrobe", "Stores clothes.", "wardrobe", 120, 0),
    Music("Sound system", "Pure entertainment.", "music-home-theatre", 500, 1),
    KitchenTable("Kitchen table", "Used to sit and eat.", "kitchen-table", 60, 0),
    Curtains("Curtains", "Make the room more cozy.", "curtains", 40, 0),
    PrayerCarpet("Prayer carpet", "Used to pray.", "prayer-carpet", 20, 0),
    BookShelf("Bookshelf", "Stores books.", "bookshelf", 40, 0),
    DinnerTable("Dinner table", "Classy way to eat.", "dinner-table", 120, 1),
    Picture("Picture", "A fine painting.", "picture", 250, 0),
    Kitchen("Kitchen", "Used to make delicious meals.", "kitchen", 1000, 2),
    Toilet("Toilet", "Where the best ideas come from.", "toilet", 80, 0),
    Desk("Desk", "The place for hard work.", "desk", 150, 1),
    Bed("Bed", "Provides a wonderful rest", "bed", 200, 1),
    Bedside("Bedside", "Storage close to your bed.", "bedside", 50, 0),
    Cabinet("Cabinet", "Provides additional storage.", "cabinet", 60, 0),
    Tv("TV", "Amazing movies.", "tv", 800, 1),
    Bathtub("Bathtub", "Gets you really clean.", "bathtub", 180, 0),
    Couch("Couch", "Every flat should have one.", "couch", 250, 0),
    Fishbowl("Fishbowl", "Provides company.", "fishbowl", 50, 1);

    private final String name;
    private final String description;
    private final String imageName;
    private final int price;
    private final int skillPoints;


    AssetType(final String name, final String description, final String imageName, final int price, final int skillPoints) {
        this.name = name;
        this.description = description;
        this.imageName = imageName;
        this.price = price;
        this.skillPoints = skillPoints;
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

    public int getSkillPoints() {
        return skillPoints;
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
