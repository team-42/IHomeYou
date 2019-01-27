package de.weareprophet.ihomeyou.datastructure;

import de.weareprophet.ihomeyou.asset.AssetType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static de.weareprophet.ihomeyou.asset.AssetType.*;

public enum RoomTypes {
    KITCHEN(DinnerTable, Kitchen, Fishbowl, Shelf, KitchenTable, Oven),
    BATH(Bathtub, Toilet, Shelf),
    LIVING_ROOM(Shelf, Music, DinnerTable, BookShelf, Curtains, Couch, Tv, PrayerCarpet, Fishbowl, Picture, Cabinet),
    BED_ROOM(Shelf, Wardrobe, Curtains, PrayerCarpet, BookShelf, Picture, Bed, Bedside, Cabinet),
    HALLWAY(Shelf, Bench, BookShelf, Picture, Cabinet),
    OUTDOOR(Bench),
    OFFICE(Shelf, Desk, Curtains, BookShelf, Picture, Cabinet);

    Set<AssetType> validAssetTypes;

    RoomTypes(AssetType... validAssetTypes) {
        this.validAssetTypes = new HashSet<>();
        this.validAssetTypes.addAll(Arrays.asList(validAssetTypes));
    }

    public boolean validRoomAsset(AssetType type) {
        return this.validAssetTypes.contains(type);
    }
}
