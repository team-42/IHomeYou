package de.weareprophet.ihomeyou;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.GameInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXB;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class IHomeYouGame {
    private static final Logger LOG = LogManager.getLogger(IHomeYouGame.class);

    public static void main(String[] args) throws IOException {
        new IHomeYouGame().start();
    }

    private IHomeYouGame() throws IOException {
        final GameInfo gameInfo = JAXB.unmarshal(
                Objects.requireNonNull(IHomeYouGame.class.getResourceAsStream("gameinfo.xml")),
                GameInfo.class);
        LOG.info("Game info: {}", gameInfo.getSubTitle());
        Game.setInfo(gameInfo);
        Game.init();
    }

    private void start() throws IOException {
        final FlatLayoutSelection flatLayoutSelection = new FlatLayoutSelection();
        Game.screens().add(flatLayoutSelection);
        Game.screens().display(flatLayoutSelection);

        final Image cursor = ImageIO.read(getClass().getResource("/mouse.png"));
        Game.window().getRenderComponent().setCursor(cursor, 16, 16);
        Game.start();
    }
}
