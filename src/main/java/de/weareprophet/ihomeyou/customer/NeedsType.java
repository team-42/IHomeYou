package de.weareprophet.ihomeyou.customer;

public enum NeedsType {
    Space,
    Rest,
    Work,
    Storage,
    Cleanliness,
    Personal,
    Food,
    Comfort,
    Decoration,
    Luxury;

    public String getLabel() {
        return name();
    }
}
