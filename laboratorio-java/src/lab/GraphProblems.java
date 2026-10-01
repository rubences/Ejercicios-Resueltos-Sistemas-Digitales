package lab;

import java.util.*;

/** Datos transcritos de los diagramas del repositorio; el orden de sucesores es explícito. */
public final class GraphProblems {
    private GraphProblems() {}
    public static final class Graph implements Problem {
        private final String start,target;
        private final Map<String,List<Edge>> edges=new LinkedHashMap<>();
        private final Map<String,Double> h=new LinkedHashMap<>();
        private final Map<String,double[]> positions=new LinkedHashMap<>();
        public Graph(String start,String target) { this.start=start;this.target=target;node(start);node(target); }
        public Graph node(String s) { edges.computeIfAbsent(s,k->new ArrayList<>()); return this; }
        public Graph edge(String a,String b,double c) { node(a);node(b);edges.get(a).add(new Edge(b,a+" → "+b,c));return this; }
        public Graph both(String a,String b,double c) { return edge(a,b,c).edge(b,a,c); }
        public Graph h(String s,double v) { if(!Double.isFinite(v)||v<0)throw new IllegalArgumentException("h inválida");node(s);h.put(s,v);return this; }
        public Graph at(String s,double x,double y) { positions.put(s,new double[]{x,y});return this; }
        public Set<String> nodes() { return Collections.unmodifiableSet(edges.keySet()); }
        public double[] position(String s) { double[] p=positions.get(s); return p==null?null:p.clone(); }
        public String target() { return target; }
        public String initial() { return start; }
        public boolean goal(String s) { return s.equals(target); }
        public List<Edge> next(String s) { return List.copyOf(edges.getOrDefault(s,List.of())); }
        public double heuristic(String s) { return h.getOrDefault(s,0.0); }
        public Graph withEndpoints(String from,String to) {
            if(!edges.containsKey(from)||!edges.containsKey(to))throw new IllegalArgumentException("El nodo inicial o meta no existe.");
            Graph g=new Graph(from,to);edges.forEach((k,v)->g.edges.put(k,new ArrayList<>(v)));g.positions.putAll(positions);
            // Las h de los PDF solo estiman la meta original. Otra meta utiliza h=0.
            if(to.equals(target))g.h.putAll(h);return g;
        }
        public Graph sortNumeric() { edges.values().forEach(v->v.sort(Comparator.comparingInt(e->Integer.parseInt(e.state()))));return this; }
    }
    public static Graph grafo2() {
        Graph g=new Graph("0","9");
        int[][] e={{0,5,1},{5,2,6},{5,3,3},{2,1,8},{2,3,8},{2,6,3},{2,7,9},{1,5,5},{1,7,1},{1,8,6},
            {8,1,7},{8,3,10},{8,5,2},{8,6,9},{8,7,6},{3,6,6},{3,8,6},{6,8,8},{6,9,6},{9,4,2},{9,8,1}};
        for(int[] a:e)g.edge(""+a[0],""+a[1],a[2]);
        double[] h={6,2,3,5,12,3,4,12,9,0};for(int i=0;i<h.length;i++)g.h(""+i,h[i]);
        return g.at("0",.10,.12).at("5",.32,.12).at("2",.65,.18).at("1",.88,.36).at("8",.51,.42)
            .at("3",.25,.62).at("6",.51,.77).at("7",.83,.66).at("9",.14,.86).at("4",.82,.90).sortNumeric();
    }
    public static Graph clase() {
        Graph g=new Graph("0","10");
        int[][] e={{0,6,4},{6,2,2},{6,3,8},{2,3,5},{2,9,6},{9,1,7},{9,4,7},
            {4,0,8},{4,1,5},{4,3,8},{4,5,1},{4,7,2},{4,8,1},{4,9,10},{4,10,5},
            {7,1,6},{7,3,4},{7,4,3},{7,10,2},{5,0,10},{5,1,10},{3,0,4},{3,8,4},{8,1,2},{1,3,9},{10,2,2}};
        for(int[] a:e)g.edge(""+a[0],""+a[1],a[2]);
        int[] h={23,13,14,7,3,15,9,2,6,4,0};for(int i=0;i<h.length;i++)g.h(""+i,h[i]);
        return g.at("0",.14,.13).at("6",.37,.13).at("2",.63,.13).at("9",.86,.28).at("4",.56,.43)
            .at("5",.12,.48).at("7",.8,.58).at("10",.82,.88).at("3",.25,.72).at("8",.08,.87).at("1",.48,.9).sortNumeric();
    }
    public static Graph romania(String start) {
        Graph g=new Graph(start,"Bucharest");
        // Arad: Zerind primero, igual que ExampleDLSProblemPruning.pdf.
        g.both("Arad","Zerind",75).both("Arad","Sibiu",140).both("Arad","Timisoara",118)
         .both("Zerind","Oradea",71).both("Oradea","Sibiu",151).both("Sibiu","Fagaras",99)
         .both("Sibiu","Rimnicu",80).both("Timisoara","Lugoj",111).both("Lugoj","Mehadia",70)
         .both("Mehadia","Drobeta",75).both("Drobeta","Craiova",120).both("Craiova","Rimnicu",146)
         .both("Craiova","Pitesti",138).both("Rimnicu","Pitesti",97).both("Fagaras","Bucharest",211)
         .both("Pitesti","Bucharest",101).both("Bucharest","Giurgiu",90).both("Bucharest","Urziceni",85)
         .both("Urziceni","Hirsova",98).both("Hirsova","Eforie",86).both("Urziceni","Vaslui",142)
         .both("Vaslui","Iasi",92).both("Iasi","Neamt",87);
        String[] names={"Arad","Zerind","Oradea","Sibiu","Timisoara","Lugoj","Mehadia","Drobeta","Craiova","Rimnicu","Pitesti","Fagaras","Bucharest","Giurgiu","Urziceni","Hirsova","Eforie","Vaslui","Iasi","Neamt"};
        int[] h={366,374,380,253,329,244,241,242,160,193,100,176,0,77,80,151,161,199,226,234};
        double[][] xy={{.08,.28},{.1,.17},{.22,.08},{.32,.3},{.05,.48},{.16,.59},{.17,.72},{.16,.86},{.34,.88},{.4,.51},{.51,.67},{.49,.35},{.67,.8},{.62,.96},{.77,.65},{.89,.67},{.94,.86},{.82,.39},{.8,.21},{.67,.1}};
        for(int i=0;i<names.length;i++)g.h(names[i],h[i]).at(names[i],xy[i][0],xy[i][1]);
        // Orden alfabético salvo Arad; permite replicar la traza de la hoja DLS.
        g.edges.forEach((k,v)->{if(!k.equals("Arad"))v.sort(Comparator.comparing(Problem.Edge::state));});
        return g;
    }
    public static Graph heuristicTrap() {
        Graph g=new Graph("S","G").edge("S","A1",1).edge("S","B",1).edge("B","G",1).h("B",100).at("S",.1,.5).at("B",.5,.84).at("G",.9,.5);
        for(int i=1;i<=7;i++){g.at("A"+i,.12+i*.095,.16).h("A"+i,0);g.edge("A"+i,i==7?"G":"A"+(i+1),1);}return g;
    }
}
