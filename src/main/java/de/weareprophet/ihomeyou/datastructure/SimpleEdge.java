package de.weareprophet.ihomeyou.datastructure;

import org.jgrapht.graph.DefaultEdge;

public class SimpleEdge extends DefaultEdge {

    private static final long serialVersionUID = 3258408452382932855L;

    @Override
    public Tile getSource() {
        return (Tile) super.getSource();
    }

    @Override
    public Tile getTarget() {
        return (Tile) super.getTarget();
    }
}
