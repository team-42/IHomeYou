package de.weareprophet.ihomeyou.algorithm;

import de.weareprophet.ihomeyou.datastructure.SimpleEdge;
import de.weareprophet.ihomeyou.datastructure.Tile;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.PatonCycleBase;

import java.util.List;
import java.util.Set;

public class CycleDetection {
    public static Set<List<SimpleEdge>> calculate(Graph<Tile, SimpleEdge> g) {
        PatonCycleBase<Tile, SimpleEdge> cycles = new PatonCycleBase<>(g);
        return cycles.getCycleBasis().getCycles();
    }
}
