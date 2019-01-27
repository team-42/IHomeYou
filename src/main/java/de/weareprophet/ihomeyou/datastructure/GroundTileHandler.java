package de.weareprophet.ihomeyou.datastructure;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.weareprophet.ihomeyou.GameGrid;
import de.weareprophet.ihomeyou.IHomeYouGame;
import de.weareprophet.ihomeyou.asset.FloorType;
import org.frice.obj.sub.ImageObject;

public class GroundTileHandler {

    private Table<Integer, Integer, ImageObject> gameGridGroundTile;
    private IHomeYouGame iHomeYouGame;

    public GroundTileHandler(IHomeYouGame iHomeYouGame) {
        gameGridGroundTile = HashBasedTable.create();
        this.gameGridGroundTile = gameGridGroundTile;
        this.iHomeYouGame = iHomeYouGame;
    }


    public void setGroundTile(FloorType floorType, int column, int row) {
        if(gameGridGroundTile.contains(row, column)) {
            gameGridGroundTile.get(row, column).setRes(floorType.getResource());
        } else {
            ImageObject tileImage = new ImageObject(floorType.getResource(), column * GameGrid.SIZE + GameGrid.BORDERS,
                    row * GameGrid.SIZE + GameGrid.BORDERS);
            gameGridGroundTile.put(row, column, tileImage);
            iHomeYouGame.addObject(tileImage);
        }
    }

    public Table<Integer, Integer, ImageObject> getGameGridGroundTile() {
        return gameGridGroundTile;
    }

    public void reset() {
        for(int c = 0; c < GameGrid.COLS; c++) {
            for(int r = 0; r < GameGrid.ROWS; r++) {
                setGroundTile(FloorType.GRASS, c, r);
            }
        }
    }
}
