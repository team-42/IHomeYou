package de.weareprophet.ihomeyou.datastructure;


import de.weareprophet.ihomeyou.asset.WallType;

public class WallEdge extends SimpleEdge {

    private WallType type;

    public WallEdge(WallType type) {
        this.type = type;
    }

    public WallType getType() {
        return type;
    }

    public void setType(WallType type) {
        this.type = type;
    }
}
