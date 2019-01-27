package de.weareprophet.ihomeyou.algorithm;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CycleDetection {

    public static int calculate(Graph<Pair<Integer, Integer>, DefaultEdge> g) {
        PatonCycleBase<Pair<Integer, Integer>, DefaultEdge> cycles = new PatonCycleBase<>(g);
        return cycles.getCycleBasis().getCycles().size();
    }

}
