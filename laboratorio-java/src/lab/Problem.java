package lab;

import java.util.List;
import java.util.Objects;

/** Contrato mínimo de un problema: estados inmutables y transiciones explícitas. */
public interface Problem {
    record Edge(String state, String action, double cost) {
        public Edge {
            Objects.requireNonNull(state); Objects.requireNonNull(action);
            if (!Double.isFinite(cost) || cost < 0) throw new IllegalArgumentException("Coste finito y no negativo requerido.");
        }
    }
    String initial();
    boolean goal(String state);
    List<Edge> next(String state);
    default double heuristic(String state) { return 0; }
    default String label(String state) { return state; }
}
