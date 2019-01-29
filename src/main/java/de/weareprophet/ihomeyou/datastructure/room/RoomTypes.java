package de.weareprophet.ihomeyou.datastructure.room;

import de.weareprophet.ihomeyou.asset.AssetType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static de.weareprophet.ihomeyou.asset.AssetType.*;

public enum RoomTypes {
    KITCHEN(DinnerTable, Kitchen, Fishbowl, Shelf, KitchenTable, Oven, Microwave, Fridge, Flower),
    BATH(Bathtub, Toilet, WashingMachine, Shelf, Flower, Mirror),
    LIVING_ROOM(Shelf, Music, DinnerTable, BookShelf, Curtains, Armchair, Couch, Tv, PrayerCarpet, Fishbowl, Picture, Cabinet, Chandelier, Flower),
    BED_ROOM(Shelf, Wardrobe, Curtains, PrayerCarpet, BookShelf, Picture, Bed, Bedside, Cabinet, Chandelier, Flower),
    HALLWAY(Shelf, Bench, BookShelf, Picture, Cabinet, Chandelier, Flower, Mirror),
    OUTDOOR(Bench, Flower),
    OFFICE(Shelf, Desk, PlainDesk, Curtains, BookShelf, Picture, Cabinet, Armchair, Flower);

    Set<AssetType> validAssetTypes;

    RoomTypes(AssetType... validAssetTypes) {
        this.validAssetTypes = new HashSet<>();
        this.validAssetTypes.addAll(Arrays.asList(validAssetTypes));
    }

    public boolean validRoomAsset(AssetType type) {
        return this.validAssetTypes.contains(type);
    }
}
