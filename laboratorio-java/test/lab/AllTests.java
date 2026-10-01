package lab;

import java.util.*;
import static lab.Search.Strategy.*;

/** Ejecutable de pruebas independiente de JUnit; cada fallo termina con código no cero. */
public final class AllTests {
    private static int count;
    private static void check(boolean ok, String name) {
        count++;
        if (!ok) throw new AssertionError(name);
        System.out.println("PASS " + name);
    }
    private static Search.Result solve(Problem p, Search.Strategy s, int depth) {
        return Search.solve(p, s, new Search.Options(depth, 200000, 3000, 20000, false), () -> false);
    }
    private static void cost(Problem p, Search.Strategy s, int depth, double expected, String name) {
        var r=solve(p,s,depth);
        check(r.found() && r.cost()==expected, name+" ["+r.status()+", coste="+r.cost()+"]");
        checkRoute(p,r);
    }
    private static void checkRoute(Problem p, Search.Result r) {
        check(r.route().get(0).equals(p.initial()), "inicio del camino");
        double sum=0;
        for(int i=1;i<r.route().size();i++) {
            String from=r.route().get(i-1), to=r.route().get(i);
            String action=r.actions().get(i-1);
            var edge=p.next(from).stream().filter(e->e.state().equals(to)&&e.action().equals(action)).findFirst();
            check(edge.isPresent(), "transición válida "+from+" → "+to);
            sum+=edge.orElseThrow().cost();
        }
        check(p.goal(r.route().get(r.route().size()-1))&&sum==r.cost(),"meta y coste reconstruidos");
    }
    private static void rejects(Runnable r,String name) {
        boolean bad=false; try {r.run();} catch(IllegalArgumentException e){bad=true;}
        check(bad,name);
    }
    public static void main(String[] args) {
        cost(GraphProblems.grafo2(),UCS,7,16,"Grafo2 UCS=16");
        cost(GraphProblems.grafo2(),ASTAR,7,16,"Grafo2 A*=16");
        cost(GraphProblems.clase(),UCS,8,23,"Grafo clase UCS=23");
        cost(GraphProblems.clase(),ASTAR,8,23,"Grafo clase A*=23");
        cost(GraphProblems.romania("Oradea"),ASTAR,8,429,"Oradea A*=429");
        var correct=solve(GraphProblems.romania("Arad"),DLS,4);
        check(correct.found()&&correct.route().equals(List.of("Arad","Sibiu","Fagaras","Bucharest")),"DLS reabre Sibiu a menor profundidad");
        var wrong=Search.solve(GraphProblems.romania("Arad"),DLS,new Search.Options(4,20000,3000,10000,true),()->false);
        check(!wrong.found(),"DLS booleano reproduce el fallo del PDF");
        var tree=Puzzles.binary(13,15);
        cost(tree,DLS,3,3,"meta aceptada exactamente en límite 3");
        check(solve(tree,DLS,2).status()==Search.Status.CUTOFF,"corte no equivale a no solución");
        var bfs=solve(tree,BFS,3);
        check(bfs.trace().stream().map(Search.Frame::state).toList().equals(List.of("1","2","3","4","5","6","7","8","9","10","11","12","13")),"orden BFS árbol");
        var dls=solve(tree,DLS,3);
        check(dls.trace().stream().map(Search.Frame::state).toList().equals(List.of("1","2","4","8","9","5","10","11","3","6","12","13")),"orden DLS árbol");
        var ids=solve(tree,IDS,3);
        check(ids.trace().stream().map(Search.Frame::state).toList().equals(List.of("1","1","2","3","1","2","4","5","3","6","7","1","2","4","8","9","5","10","11","3","6","12","13")),"orden IDS árbol");
        check(solve(Puzzles.pingPong(10,15,17),BFS,30).status()==Search.Status.NO_SOLUTION,"ping-pong original AB es imposible: BC=11 obliga a empezar por BC");
        cost(Puzzles.pingPong(10,15,17,"BC"),BFS,30,20,"variante explícita BC: 20 cambios, 21 partidos");
        cost(Puzzles.islands(),BFS,12,7,"islas 7 cruces");
        cost(Puzzles.missionaries(),BFS,20,11,"misioneros 11 cruces");
        check(reachable(Puzzles.missionaries()).size()==16,"misioneros 16 estados alcanzables");
        check(reachable(Puzzles.islands()).size()==10,"islas 10 estados seguros alcanzables");
        var col=new Puzzles.Column("4\n3\n1221\n2132\n3132\n0230");
        cost(col,ASTAR,20,8,"Column Jump A* ocho saltos");
        var dfs=solve(col,DFS,20); check(dfs.found(),"Column Jump DFS encuentra solución"); checkRoute(col,dfs);
        String s=col.initial();
        int[][] moves={{2,1,4,1},{1,4,4,4},{1,1,1,4},{4,2,1,2},{4,4,4,2},{4,1,4,3},{4,3,1,3},{1,4,1,1}};
        for(int[] m:moves) s=col.move(s,m[0]-1,m[1]-1,m[2]-1,m[3]-1);
        check(col.goal(s),"ocho movimientos exactos del PDF dejan una bola");
        check(col.initial().equals("1221213231320230"),"sucesores no mutan el tablero original");
        check(col.heuristic(s)==0,"h(meta)=0");
        check(col.heuristic(col.initial())<=8,"heurística admisible en ejemplo");
        rejects(()->new Puzzles.Column("3\n2\n123\n000\n000"),"rechaza color fuera de rango");
        rejects(()->new Puzzles.Column("2\n1\n00\n00"),"rechaza tablero vacío");
        rejects(()->Puzzles.pingPong(10,15,16),"rechaza total impar");
        rejects(()->Puzzles.pingPong(1,1,8),"rechaza contador imposible");
        var stop=Search.solve(tree,BFS,new Search.Options(3,2,10,1000,false),()->false);
        check(stop.status()==Search.Status.LIMIT,"límite de recursos explícito");
        var cancel=Search.solve(tree,BFS,new Search.Options(3,100,10,1000,false),()->true);
        check(cancel.status()==Search.Status.CANCELLED,"cancelación explícita");
        var tinyTrace=Search.solve(tree,BFS,new Search.Options(3,1000,2,1000,false),()->false);
        check(tinyTrace.found()&&tinyTrace.traceTruncated(),"traza truncada conserva solución");
        var reopen=new GraphProblems.Graph("S","G").edge("S","A",3).edge("S","B",1).edge("B","A",1).edge("A","G",2).h("A",0).h("B",3);
        cost(reopen,ASTAR,9,4,"A* reabre un nodo cerrado con mejor g");
        var noGoal=new GraphProblems.Graph("S","G").edge("S","A",1).edge("A","S",1).node("G");
        check(solve(noGoal,BFS,8).status()==Search.Status.NO_SOLUTION,"grafo finito agotado sin solución");
        cost(Puzzles.binary(1,15),DLS,0,0,"inicio igual a meta con límite cero");
        check(LineFollower.decide(false,false,false,1,true).turn()>0,"memoria conserva último giro");
        check(LineFollower.decide(false,false,false,-1,true).turn()<0,"memoria conserva giro izquierdo");
        check(LineFollower.decide(false,false,false,1,false).turn()==0,"sin memoria pierde línea y sigue recto");
        check(LineFollower.decide(true,false,false,0,true).turn()<0,"sensor izquierdo gira izquierda");
        check(LineFollower.decide(false,false,true,0,true).turn()>0,"sensor derecho gira derecha");
        var follower=new LineFollower();
        for(int i=0;i<10000;i++) follower.step(true,1);
        check(Double.isFinite(follower.x())&&Double.isFinite(follower.y()),"simulación estable, posiciones finitas");
        check(follower.lost()==0,"10.000 pasos en circuito normal sin perder detección");
        check(GraphProblems.grafo2().withEndpoints("0","4").nodes().stream().allMatch(n->GraphProblems.grafo2().withEndpoints("0","4").heuristic(n)==0),"cambiar meta descarta heurística anterior");
        cost(GraphProblems.heuristicTrap(),BFS,10,2,"contraejemplo: BFS coste 2");
        cost(GraphProblems.heuristicTrap(),GREEDY,10,8,"contraejemplo: Greedy coste 8");
        follower.reset(); check(follower.ticks()==0,"reinicio del seguidor");
        System.out.println("\nTOTAL: "+count+" comprobaciones PASS");
    }
    private static Set<String> reachable(Problem p) {
        Set<String> seen=new HashSet<>(); ArrayDeque<String> q=new ArrayDeque<>(); q.add(p.initial());seen.add(p.initial());
        while(!q.isEmpty()) for(var e:p.next(q.remove())) if(seen.add(e.state())) q.add(e.state());
        return seen;
    }
}
