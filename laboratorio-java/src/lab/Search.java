package lab;

import java.util.*;
import java.util.function.BooleanSupplier;

/** Búsqueda con reaperturas, límites de recursos y trazas acotadas. No usa Swing. */
public final class Search {
    private Search() {}
    public enum Strategy {
        BFS("BFS · anchura"), DFS("DFS · profundidad"), DLS("DLS · profundidad limitada"),
        IDS("IDS · profundización iterativa"), UCS("UCS · coste uniforme"),
        GREEDY("Greedy · mejor h"), ASTAR("A* · g + h");
        private final String title;
        Strategy(String title) { this.title=title; }
        public String toString() { return title; }
    }
    public enum Status { FOUND, NO_SOLUTION, CUTOFF, LIMIT, CANCELLED }
    public record Options(int depthLimit, int maxNodes, int maxTrace, long maxMillis, boolean unsafeClosed) {
        public Options {
            if(depthLimit<0 || depthLimit>200 || maxNodes<1 || maxTrace<1 || maxMillis<1)
                throw new IllegalArgumentException("Límite de profundidad 0–200; presupuestos positivos.");
        }
        public static Options defaults(int depth) { return new Options(depth,150000,1200,15000,false); }
    }
    public record Frame(int number,int iteration,String state,String action,String event,
                        double g,double h,int depth,String frontier,List<String> path) {
        public Frame { path=List.copyOf(path); }
    }
    public record Result(Status status,List<String> route,List<String> actions,double cost,
                         int expanded,int generated,int peak,double millis,List<Frame> trace,boolean traceTruncated) {
        public Result { route=List.copyOf(route); actions=List.copyOf(actions); trace=List.copyOf(trace); }
        public boolean found() { return status==Status.FOUND; }
    }
    private record Node(String state,String action,double g,double h,int depth,long id,Node parent) {}
    private record Outcome(Status status,Node goal) {}
    private static final class Budget {
        final long start=System.nanoTime();
        final List<Frame> trace=new ArrayList<>();
        int expanded,generated,peak,extracted; boolean truncated;
    }
    public static Result solve(Problem problem,Strategy strategy,Options options,BooleanSupplier cancelled) {
        Objects.requireNonNull(problem); Objects.requireNonNull(strategy); Objects.requireNonNull(options); Objects.requireNonNull(cancelled);
        Budget b=new Budget(); Outcome outcome=null;
        if(strategy==Strategy.IDS) {
            for(int depth=0;depth<=options.depthLimit();depth++) {
                outcome=search(problem,Strategy.DLS,options,cancelled,b,depth,depth);
                if(outcome.status()!=Status.CUTOFF) break;
            }
        } else outcome=search(problem,strategy,options,cancelled,b,options.depthLimit(),-1);
        List<String> route=outcome.goal()==null?List.of():path(outcome.goal());
        List<String> actions=new ArrayList<>();
        for(Node n=outcome.goal();n!=null&&n.parent()!=null;n=n.parent()) actions.add(n.action());
        Collections.reverse(actions);
        return new Result(outcome.status(),route,actions,outcome.goal()==null?Double.NaN:outcome.goal().g(),
                b.expanded,b.generated,b.peak,(System.nanoTime()-b.start)/1_000_000.0,b.trace,b.truncated);
    }
    private static Outcome search(Problem p,Strategy s,Options o,BooleanSupplier cancel,Budget b,int limit,int iteration) {
        Comparator<Node> cmp=Comparator.<Node>comparingDouble(n->priority(n,s)).thenComparingLong(Node::id);
        PriorityQueue<Node> frontier=new PriorityQueue<>(cmp);
        Map<String,Double> closed=new HashMap<>();
        frontier.add(new Node(p.initial(),"Inicio",0,heuristic(p,p.initial()),0,b.generated++,null));
        boolean cut=false;
        while(!frontier.isEmpty()) {
            if(cancel.getAsBoolean() || Thread.currentThread().isInterrupted()) return new Outcome(Status.CANCELLED,null);
            if(b.generated>o.maxNodes() || (System.nanoTime()-b.start)/1_000_000>=o.maxMillis()) return new Outcome(Status.LIMIT,null);
            b.peak=Math.max(b.peak,frontier.size());
            Node n=frontier.remove(); b.extracted++;
            double key=dominance(n,s);
            Double old=closed.get(n.state());
            boolean booleanPruning=(o.unsafeClosed()&&s==Strategy.DLS)||s==Strategy.GREEDY;
            if(old!=null&&(booleanPruning||old<=key)) {
                record(p,n,"DESCARTADO: repetido no mejor",frontier,cmp,b,o,iteration); continue;
            }
            // Una meta a profundidad == límite sigue siendo una solución válida.
            if(p.goal(n.state())) {
                record(p,n,"META",frontier,cmp,b,o,iteration);
                return new Outcome(Status.FOUND,n);
            }
            if(s==Strategy.DLS&&n.depth()>=limit) {
                cut=true; record(p,n,"CORTE de profundidad",frontier,cmp,b,o,iteration); continue;
            }
            closed.put(n.state(),key); b.expanded++;
            for(Problem.Edge e:p.next(n.state())) {
                if(cancel.getAsBoolean() || Thread.currentThread().isInterrupted()) return new Outcome(Status.CANCELLED,null);
                if(b.generated>=o.maxNodes()) return new Outcome(Status.LIMIT,null);
                double g=n.g()+e.cost();
                if(!Double.isFinite(g)) throw new IllegalArgumentException("El coste acumulado se desborda.");
                frontier.add(new Node(e.state(),e.action(),g,heuristic(p,e.state()),n.depth()+1,b.generated++,n));
            }
            b.peak=Math.max(b.peak,frontier.size());
            record(p,n,old==null?"EXPANDIDO":"REABIERTO: mejor "+(s==Strategy.UCS||s==Strategy.ASTAR?"g":"profundidad"),frontier,cmp,b,o,iteration);
        }
        return new Outcome(cut?Status.CUTOFF:Status.NO_SOLUTION,null);
    }
    private static double heuristic(Problem p,String state) {
        double h=p.heuristic(state);
        if(!Double.isFinite(h)||h<0) throw new IllegalArgumentException("h debe ser finita y no negativa.");
        return h;
    }
    private static double dominance(Node n,Strategy s) { return s==Strategy.ASTAR||s==Strategy.UCS?n.g():n.depth(); }
    private static double priority(Node n,Strategy s) {
        return switch(s) { case BFS,IDS->n.depth(); case DFS,DLS->-n.depth(); case UCS->n.g(); case GREEDY->n.h(); case ASTAR->n.g()+n.h(); };
    }
    private static List<String> path(Node node) {
        List<String> out=new ArrayList<>(); for(Node n=node;n!=null;n=n.parent()) out.add(n.state());
        Collections.reverse(out); return out;
    }
    private static void record(Problem p,Node n,String event,PriorityQueue<Node> queue,Comparator<Node> cmp,Budget b,Options o,int iteration) {
        if(b.trace.size()>=o.maxTrace()) {
            b.truncated=true;
            if(!event.equals("META")) return;
            b.trace.remove(b.trace.size()-1);
        }
        PriorityQueue<Node> copy=new PriorityQueue<>(cmp); copy.addAll(queue);
        StringJoiner line=new StringJoiner(" | ");
        for(int i=0;i<16&&!copy.isEmpty();i++) {
            Node x=copy.remove(); line.add(p.label(x.state())+" [d="+x.depth()+",g="+fmt(x.g())+",h="+fmt(x.h())+"]");
        }
        if(!copy.isEmpty()) line.add("… +"+copy.size()+" nodos");
        b.trace.add(new Frame(b.extracted,iteration,n.state(),n.action(),event,n.g(),n.h(),n.depth(),line.toString(),path(n)));
    }
    public static String fmt(double n) { return !Double.isFinite(n)?"—":n==Math.rint(n)?Long.toString((long)n):String.format(Locale.ROOT,"%.2f",n); }
    public static String statusText(Status s) { return switch(s) {
        case FOUND->"Solución encontrada"; case CUTOFF->"Corte: aumenta el límite de profundidad";
        case NO_SOLUTION->"Espacio finito agotado: sin solución"; case LIMIT->"Presupuesto agotado; no prueba que sea imposible"; case CANCELLED->"Cálculo cancelado"; }; }
}
