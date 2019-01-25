package de.weareprophet.ihomeyou;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;

public class Sample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("BasicAppSample");
        settings.setVersion("0.1");
    }

    public static void main(String[] args) {
        launch(args);
    }
}