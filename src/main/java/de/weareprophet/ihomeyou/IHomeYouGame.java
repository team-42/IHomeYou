package de.weareprophet.ihomeyou;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.GameInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXB;
import java.util.Objects;

public class IHomeYouGame {
    private static final Logger LOG = LogManager.getLogger(IHomeYouGame.class);

    public static void main(String[] args) {
        new IHomeYouGame().start();
    }

    private IHomeYouGame() {
        final GameInfo gameInfo = JAXB.unmarshal(
                Objects.requireNonNull(IHomeYouGame.class.getResourceAsStream("gameinfo.xml")),
                GameInfo.class);
        LOG.info("Game info: {}", gameInfo.getSubTitle());
        Game.setInfo(gameInfo);
        Game.init();
    }

    private void start() {
        final FlatLayoutSelection flatLayoutSelection = new FlatLayoutSelection();
        Game.screens().add(flatLayoutSelection);
        Game.screens().display(flatLayoutSelection);
        Game.start();
    }
}
