package de.weareprophet.ihomeyou;

import org.frice.Game;

import static org.frice.Initializer.launch;

public class IHomeYouGame extends Game {
    public static void main(String[] args) {
        launch(IHomeYouGame.class);
    }


    @Override
    public void onInit() {
        setSize(1366, 720);
        setLocation(0, 0);
    }
}
