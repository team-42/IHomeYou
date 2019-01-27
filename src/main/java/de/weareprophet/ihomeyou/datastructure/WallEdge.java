package de.weareprophet.ihomeyou.datastructure;


import de.weareprophet.ihomeyou.GameGrid;
import de.weareprophet.ihomeyou.asset.WallType;

public class WallEdge extends SimpleEdge {

    private WallType type;
    private GameGrid.WallDirection direction;

    public WallEdge() {

    }

    public WallEdge(WallType type) {
        this.type = type;
    }

    public WallType getType() {
        return type;
    }

    public void setType(WallType type) {
        this.type = type;
    }

    public void setDirection(GameGrid.WallDirection direction) {
        this.direction = direction;
    }

    public GameGrid.WallDirection getDirection() {
        return direction;
    }
}
