package de.weareprophet.ihomeyou.asset;

import de.weareprophet.ihomeyou.customer.NeedsFulfillment;
import org.frice.resource.image.ImageResource;

import java.net.URL;

import static de.weareprophet.ihomeyou.customer.NeedsType.*;

public enum AssetType {
    // Rest
    Bench("Bench", "Provides a nice rest.", "bench", 30, 0,
            NeedsFulfillment.builder().add(Rest, 20).add(Comfort, 10).build()),
    Bed("Bed", "Provides a wonderful rest", "bed", 200, 1,
            NeedsFulfillment.builder().add(Rest, 150).add(Comfort, 50).build()),
    Armchair("Armchair", "Used after a hard day's work.", "armchair", 100, 1,
            NeedsFulfillment.builder().add(Rest, 50).add(Comfort, 50).build()),
    Couch("Couch", "Every flat should have one.", "couch", 250, 2,
            NeedsFulfillment.builder().add(Rest, 150).add(Comfort, 100).build()),
    // Storage
    Shelf("Shelf", "Stores anything.", "shelf", 40, 0,
            NeedsFulfillment.builder().add(Storage, 20).add(Decoration, 10).add(Personal, 10).build()),
    BookShelf("Bookshelf", "Stores books.", "bookshelf", 40, 0,
            NeedsFulfillment.builder().add(Storage, 20).add(Decoration, 20).build()),
    Bedside("Bedside", "Storage close to your bed.", "bedside", 50, 1,
            NeedsFulfillment.builder().add(Storage, 30).add(Comfort, 20).build()),
    Cabinet("Cabinet", "Provides additional storage.", "cabinet", 60, 1,
            NeedsFulfillment.builder().add(Storage, 50).add(Decoration, 10).build()),
    Wardrobe("Wardrobe", "Stores clothes.", "wardrobe", 160, 1,
            NeedsFulfillment.builder().add(Storage, 120).add(Personal, 40).build()),
    // Luxury / Entertainment
    Chandelier("Chandelier", "Light for rich people.", "chandelier", 150, 1,
            NeedsFulfillment.builder().add(Luxury, 150).build()),
    Music("Sound system", "Pure entertainment.", "music-home-theatre", 300, 2,
            NeedsFulfillment.builder().add(Comfort, 50).add(Luxury, 180).add(Personal, 70).build()),
    Tv("TV", "Amazing movies.", "tv", 500, 2,
            NeedsFulfillment.builder().add(Luxury, 400).add(Personal, 100).build()),
    // Food
    KitchenTable("Kitchen table", "Used to sit and eat.", "kitchen-table", 60, 0,
            NeedsFulfillment.builder().add(Food, 10).add(Comfort, 20).add(Personal, 20).add(Decoration, 10).build()),
    DinnerTable("Dinner table", "Classy way to eat.", "dinner-table", 120, 2,
            NeedsFulfillment.builder().add(Food, 10).add(Comfort, 50).add(Decoration, 40).add(Personal, 20).build()),
    Microwave("Microwave", "For the quick and healthy meal.", "microwave", 60, 0,
            NeedsFulfillment.builder().add(Food, 50).add(Comfort, 10).build()),
    Fridge("Fridge", "Keeps things cool.", "fridge", 140, 1,
            NeedsFulfillment.builder().add(Food, 100).add(Comfort, 40).build()),
    Oven("Oven", "Can heat things up.", "oven", 140, 1,
            NeedsFulfillment.builder().add(Food, 100).add(Comfort, 40).build()),
    Kitchen("Kitchen", "Used to make delicious meals.", "kitchen", 500, 3,
            NeedsFulfillment.builder().add(Food, 400).add(Luxury, 100).build()),
    // Decoration
    Flower("Flower", "Who doesn't love flowers?", "flower", 20, 0,
            NeedsFulfillment.builder().add(Decoration, 20).build()),
    Curtains("Curtains", "Make the room more cozy.", "curtains", 40, 1,
            NeedsFulfillment.builder().add(Decoration, 20).add(Personal, 20).build()),
    Picture("Picture", "A fine painting.", "picture", 250, 2,
            NeedsFulfillment.builder().add(Decoration, 100).add(Personal, 50).add(Luxury, 100).build()),
    // Personal
    PrayerCarpet("Prayer carpet", "Used to pray.", "prayer-carpet", 20, 0,
            NeedsFulfillment.builder().add(Personal, 20).build()),
    Mirror("Mirror", "Used to look at yourself", "mirror", 30, 1,
            NeedsFulfillment.builder().add(Personal, 20).add(Storage, 10).build()),
    Fishbowl("Fishbowl", "Provides company.", "fishbowl", 50, 1,
            NeedsFulfillment.builder().add(Decoration, 20).add(Personal, 30).build()),
    // Cleanliness
    Toilet("Toilet", "Where the best ideas come from.", "toilet", 80, 0,
            NeedsFulfillment.builder().add(Cleanliness, 40).add(Personal, 40).build()),
    Bathtub("Bathtub", "Gets you really clean.", "bathtub", 180, 0,
            NeedsFulfillment.builder().add(Cleanliness, 100).add(Comfort, 80).build()),
    WashingMachine("Washing machine", "Fresh clothes for your household.", "washing-machine", 180, 1,
            NeedsFulfillment.builder().add(Cleanliness, 100).add(Personal, 40).add(Comfort, 40).build()),
    // Work
    PlainDesk("Desk", "A place to get some work done.", "plaindesk", 120, 1,
            NeedsFulfillment.builder().add(Work, 80).add(Storage, 30).add(Comfort, 10).build()),
    Desk("PC Desk", "The place for hard work.", "desk", 280, 2,
            NeedsFulfillment.builder().add(Work, 180).add(Storage, 60).add(Comfort, 40).build()),
    ;

    private final String name;
    private final String description;
    private final String imageName;
    private final int price;
    private final int skillPoints;
    private final NeedsFulfillment needsFulfillment;


    AssetType(final String name, final String description, final String imageName, final int price, final int skillPoints, final NeedsFulfillment needsFulfillment) {
        this.name = name;
        this.description = description;
        this.imageName = imageName;
        this.price = price;
        this.skillPoints = skillPoints;
        this.needsFulfillment = needsFulfillment;
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

    public NeedsFulfillment getNeedsFulfillment() {
        return needsFulfillment;
    }

    public ImageResource getResource() {
        return ImageResource.fromWeb(this.getImageUrl().toString());
    }

    public ImageResource getLargeResource() {
        return ImageResource.fromWeb(this.getLargeImageUrl().toString());
    }

    public URL getImageUrl() {
        return getClass().getResource(this.imageName + ".png");
    }

    public URL getLargeImageUrl() {
        return getClass().getResource(this.imageName + "_L.png");
    }

}
