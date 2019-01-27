package de.weareprophet.ihomeyou.customer;

public enum NeedsType {
    Food,
    Rest,
    Space,
    Work,
    Storage,
    Comfort,
    Cleanliness,
    Decoration,
    Luxury,
    Personal;

    public String getLabel() {
        return name();
    }
}
