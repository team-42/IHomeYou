package de.weareprophet.ihomeyou.datastructure;

import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;

public class SimpleEdge extends DefaultEdge {

    private static final long serialVersionUID = 3258408452382932855L;

    @Override
    public Pair<Integer, Integer> getSource() {
        return (Pair<Integer, Integer>) super.getSource();
    }

    @Override
    public Pair<Integer, Integer> getTarget() {
        return (Pair<Integer, Integer>) super.getTarget();
    }
}