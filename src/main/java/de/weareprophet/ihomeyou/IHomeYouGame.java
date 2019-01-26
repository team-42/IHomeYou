package de.weareprophet.ihomeyou;

import org.frice.Game;
import org.frice.obj.sub.ShapeObject;
import org.frice.resource.graphics.ColorResource;
import org.frice.util.shape.FRectangle;

import static org.frice.Initializer.launch;

public class IHomeYouGame extends Game {
    public static void main(String[] args) {
        launch(IHomeYouGame.class);
    }


    @Override
    public void onInit() {
        setSize(1366, 720);
        setLocation(0, 0);

        ShapeObject obj1 = new ShapeObject(ColorResource.DARK_GRAY, new FRectangle(50, 50));
        addObject(obj1);
    }

    @Override
    public void onExit() {
        System.exit(0);
    }
}
