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
    Luxury(2.0f);

    private float priceFactor;

    NeedsType() {
        priceFactor = 1.0f;
    }

    NeedsType(final float priceFactor) {
        this.priceFactor = priceFactor;
    }

    public String getLabel() {
        return name();
    }

    public float getPriceFactor() {
        return priceFactor;
    }
}
