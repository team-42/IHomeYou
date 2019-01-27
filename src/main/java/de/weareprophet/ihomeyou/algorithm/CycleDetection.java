package de.weareprophet.ihomeyou.algorithm;

import de.weareprophet.ihomeyou.datastructure.SimpleEdge;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Set;

public class CycleDetection {
    public static Set<List<SimpleEdge>> calculate(Graph<Pair<Integer, Integer>, SimpleEdge> g) {
        PatonCycleBase<Pair<Integer, Integer>, SimpleEdge> cycles = new PatonCycleBase<>(g);
        return cycles.getCycleBasis().getCycles();
    }
}
