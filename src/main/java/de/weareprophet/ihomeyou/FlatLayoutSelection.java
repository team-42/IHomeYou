package de.weareprophet.ihomeyou;

import de.gurkenlabs.litiengine.gui.screens.Screen;

import java.awt.*;

public class FlatLayoutSelection extends Screen {
    protected FlatLayoutSelection() {
        super("Layout screen");
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);

        g.setColor(Color.RED);
        g.setBackground(Color.BLUE);
        g.draw3DRect(20, 20, 200, 200, false);
    }
}
