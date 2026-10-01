package lab;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.*;

/** Dibujo vectorial: la vista recibe estados del motor, nunca calcula soluciones. */
@SuppressWarnings("serial")
final class Scene extends JPanel {
    private Problem problem; private App.Module module;
    private String state="",caption="Selecciona Resolver para comenzar";
    private List<String> path=List.of(); private Set<String> seen=Set.of();
    private boolean allEdges=true;
    Scene(){setBackground(Color.WHITE);setPreferredSize(new Dimension(1000,460));setMinimumSize(new Dimension(300,260));}
    void view(Problem p,App.Module m,String s,List<String> route,Set<String> explored,String title){problem=p;module=m;state=s;path=List.copyOf(route);seen=Set.copyOf(explored);caption=title;repaint();}
    void allEdges(boolean value){allEdges=value;repaint();}
    @Override protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics); Graphics2D g=(Graphics2D)graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        double scale=Math.min(getWidth()/1000.0,getHeight()/460.0);
        g.translate((getWidth()-1000*scale)/2,(getHeight()-460*scale)/2);g.scale(scale,scale);
        text(g,caption,22,27,16,Ui.INK,true);
        if(problem!=null){
            if(problem instanceof GraphProblems.Graph graph)graph(g,graph);
            else switch(module){case COLUMN->column(g,(Puzzles.Column)problem);case ISLANDS,MISSIONARIES->river(g);case PING->ping(g);case BINARY->binary(g);default->{}}
        }
        g.dispose();
    }
    private Point2D.Double pos(GraphProblems.Graph graph,String s){double[] a=graph.position(s);if(a==null){int i=new ArrayList<>(graph.nodes()).indexOf(s);return new Point2D.Double(500+350*Math.cos(i*2*Math.PI/graph.nodes().size()),235+160*Math.sin(i*2*Math.PI/graph.nodes().size()));}return new Point2D.Double(45+a[0]*905,53+a[1]*340);}
    private boolean routeEdge(String a,String b){for(int i=1;i<path.size();i++)if(path.get(i-1).equals(a)&&path.get(i).equals(b))return true;return false;}
    private void graph(Graphics2D g,GraphProblems.Graph graph){
        boolean cities=module==App.Module.DLS_ROMANIA||module==App.Module.ASTAR_ROMANIA;
        for(boolean highlight:new boolean[]{false,true})for(String a:graph.nodes())for(Problem.Edge e:graph.next(a)){
            boolean chosen=routeEdge(a,e.state());if(chosen!=highlight||!chosen&&!allEdges)continue;
            Point2D.Double p=pos(graph,a),q=pos(graph,e.state());
            double dx=q.x-p.x,dy=q.y-p.y,length=Math.hypot(dx,dy);if(length<1)continue;
            boolean reverse=graph.next(e.state()).stream().anyMatch(x->x.state().equals(a));
            // En Rumanía se dibuja una sola carretera; las flechas solo en el camino dirigido.
            if(cities&&!chosen&&a.compareTo(e.state())>0)continue;
            double curve=reverse&&!cities?22:0;
            double cx=(p.x+q.x)/2-dy/length*curve,cy=(p.y+q.y)/2+dx/length*curve;
            double angle0=Math.atan2(cy-p.y,cx-p.x),angle1=Math.atan2(q.y-cy,q.x-cx),r=cities?10:20;
            double x0=p.x+r*Math.cos(angle0),y0=p.y+r*Math.sin(angle0),x1=q.x-r*Math.cos(angle1),y1=q.y-r*Math.sin(angle1);
            g.setColor(chosen?Ui.TEAL:new Color(198,209,219));g.setStroke(new BasicStroke(chosen?3.5f:1.3f));
            g.draw(new QuadCurve2D.Double(x0,y0,cx,cy,x1,y1));
            if(!cities||chosen)arrow(g,x1,y1,angle1,chosen?9:7);
            if(chosen||allEdges){
                double tx=.25*x0+.5*cx+.25*x1,ty=.25*y0+.5*cy+.25*y1;
                String cost=Search.fmt(e.cost());g.setFont(new Font(Font.SANS_SERIF,chosen?Font.BOLD:Font.PLAIN,cities?11:12));int w=g.getFontMetrics().stringWidth(cost);
                g.setColor(Color.WHITE);g.fillRoundRect((int)tx-w/2-3,(int)ty-11,w+6,16,5,5);g.setColor(chosen?Ui.TEAL:Ui.MUTED);g.drawString(cost,(float)(tx-w/2.0),(float)ty+1);
            }
        }
        for(String s:graph.nodes()){
            Point2D.Double p=pos(graph,s);boolean current=s.equals(state),onPath=path.contains(s),visited=seen.contains(s);int r=cities?10:21;
            g.setColor(current?Ui.AMBER:onPath?Ui.TEAL:visited?Ui.BLUE:Ui.LIGHT);g.fillOval((int)p.x-r,(int)p.y-r,2*r,2*r);
            g.setStroke(new BasicStroke(graph.goal(s)?3:1.3f));g.setColor(graph.goal(s)?Ui.TEAL:Ui.MUTED);g.drawOval((int)p.x-r,(int)p.y-r,2*r,2*r);
            if(cities){text(g,s,(int)p.x+15,(int)p.y-2,12,Ui.INK,true);text(g,"h="+Search.fmt(graph.heuristic(s)),(int)p.x+15,(int)p.y+13,10,Ui.MUTED,false);}
            else{center(g,s,(int)p.x,(int)p.y+5,15,current||onPath||visited?Color.WHITE:Ui.INK,true);center(g,"h="+Search.fmt(graph.heuristic(s)),(int)p.x,(int)p.y+35,11,Ui.MUTED,false);}
        }
        text(g,"● actual   — camino actual   ○ borde doble: meta   |   Etiquetas de arco = coste",22,445,12,Ui.MUTED,false);
    }
    private void column(Graphics2D g,Puzzles.Column p){
        int n=p.size();double cell=Math.min(68,340.0/n),left=95,top=75;String previous=path.size()>1?path.get(path.size()-2):state;
        for(int i=0;i<n;i++){center(g,""+(i+1),(int)(left+i*cell+cell/2),62,13,Ui.MUTED,true);center(g,""+(i+1),76,(int)(top+i*cell+cell/2+5),13,Ui.MUTED,true);}
        Color[] colors={Ui.LIGHT,Ui.TEAL,new Color(193,91,69),Ui.BLUE,new Color(139,90,174),new Color(174,130,20),new Color(43,130,84),new Color(158,83,114),new Color(80,130,145),Ui.INK};
        for(int r=0;r<n;r++)for(int c=0;c<n;c++){
            int index=r*n+c,value=state.charAt(index)-'0';double x=left+c*cell,y=top+r*cell;
            g.setColor(Ui.LIGHT);g.fill(new RoundRectangle2D.Double(x+2,y+2,cell-4,cell-4,10,10));
            if(value!=0){g.setColor(colors[value]);g.fill(new Ellipse2D.Double(x+9,y+9,cell-18,cell-18));center(g,""+value,(int)(x+cell/2),(int)(y+cell/2+6),19,Color.WHITE,true);}
            if(previous.length()==state.length()&&previous.charAt(index)!=state.charAt(index)){g.setColor(Ui.AMBER);g.setStroke(new BasicStroke(3));g.draw(new RoundRectangle2D.Double(x+3,y+3,cell-6,cell-6,10,10));}
        }
        int x=650;text(g,"BOLAS RESTANTES",x,107,13,Ui.MUTED,true);text(g,""+Puzzles.Column.balls(state),x,165,52,Ui.TEAL,true);
        text(g,"h = colores distintos − 1",x,215,16,Ui.INK,true);text(g,"h(estado) = "+Search.fmt(p.heuristic(state)),x,245,17,Ui.MUTED,false);
        text(g,"El número identifica el color.",x,306,14,Ui.MUTED,false);text(g,"0 / celda vacía: posición libre.",x,334,14,Ui.MUTED,false);text(g,"Borde ámbar: celdas modificadas.",x,362,14,Ui.MUTED,false);
        text(g,"Un salto puede retirar varias bolas consecutivas del mismo color.",25,438,14,Ui.MUTED,false);
    }
    private void river(Graphics2D g){
        g.setColor(new Color(222,237,247));g.fillRoundRect(325,85,350,310,30,30);
        g.setColor(new Color(232,240,227));g.fillRoundRect(35,85,275,310,25,25);g.fillRoundRect(690,85,275,310,25,25);
        text(g,"ORILLA A",62,119,18,Ui.INK,true);text(g,"ORILLA B",718,119,18,Ui.INK,true);
        boolean right;List<String> a=new ArrayList<>(),b=new ArrayList<>();
        if(module==App.Module.ISLANDS){int v=Integer.parseInt(state);right=(v&8)!=0;String[] names={"Lobo","Oveja","Col","Barquero"};for(int i=0;i<4;i++)((v&(1<<i))==0?a:b).add(names[i]);}
        else{String[] parts=state.split(",");int m=Integer.parseInt(parts[0]),c=Integer.parseInt(parts[1]);right=parts[2].equals("1");for(int i=0;i<3;i++){(i<m?a:b).add("Misionero "+(i+1));(i<c?a:b).add("Caníbal "+(i+1));}}
        bank(g,a,60);bank(g,b,715);
        int boatX=right?562:350;g.setColor(Ui.NAVY);Path2D boat=new Path2D.Double();boat.moveTo(boatX,290);boat.lineTo(boatX+90,290);boat.lineTo(boatX+70,324);boat.lineTo(boatX+20,324);boat.closePath();g.fill(boat);center(g,"BOTE",boatX+45,282,14,Ui.NAVY,true);
        center(g,"Cruce "+Math.max(0,path.size()-1),500,185,25,Ui.TEAL,true);center(g,"Estado seguro",500,223,16,Ui.INK,false);
        text(g,module==App.Module.ISLANDS?"Nunca dejar lobo + oveja u oveja + col sin el barquero.":"En cada orilla: M = 0 o M ≥ C. El bote transporta una o dos personas.",35,437,15,Ui.MUTED,false);
    }
    private static void bank(Graphics2D g,List<String> names,int x){int y=150;for(String name:names){g.setColor(Color.WHITE);g.fillRoundRect(x,y,225,30,12,12);text(g,name,x+12,y+21,14,Ui.INK,true);y+=36;}}
    private void ping(Graphics2D g){
        int[] counts=Puzzles.counts(state);String pair=state.substring(0,2);
        for(int i=0;i<3;i++){int x=70+310*i;boolean playing=pair.indexOf('A'+i)>=0;g.setColor(playing?Ui.PALE:Ui.LIGHT);g.fillRoundRect(x,95,240,245,24,24);center(g,"JUGADOR "+(char)('A'+i),x+120,135,18,Ui.INK,true);center(g,""+counts[i],x+120,220,63,playing?Ui.TEAL:Ui.MUTED,true);center(g,playing?"Juega este partido":"Descansa",x+120,285,16,Ui.INK,false);}
        center(g,"Partido "+Arrays.stream(counts).sum()/2+"  ·  pareja "+pair,500,382,22,Ui.INK,true);
        center(g,"Cada partido suma 1 a dos contadores. El perdedor sale; el ganador permanece.",500,424,14,Ui.MUTED,false);
    }
    private void binary(Graphics2D g){
        Map<Integer,Point2D.Double> positions=new LinkedHashMap<>();ArrayDeque<String> queue=new ArrayDeque<>();queue.add(problem.initial());
        while(!queue.isEmpty()){String s=queue.remove();int v=Integer.parseInt(s);if(v>31)continue;int d=31-Integer.numberOfLeadingZeros(v),index=v-(1<<d);positions.put(v,new Point2D.Double(40+(index+.5)*920/(1<<d),80+d*71));for(Problem.Edge e:problem.next(s))queue.add(e.state());}
        for(var e:positions.entrySet()){int v=e.getKey();if(v==1)continue;Point2D.Double p=positions.get(v/2),q=e.getValue();boolean chosen=routeEdge(""+(v/2),""+v);g.setStroke(new BasicStroke(chosen?3:1.5f));g.setColor(chosen?Ui.TEAL:Ui.LINE);g.draw(new Line2D.Double(p,q));}
        for(var e:positions.entrySet()){String s=""+e.getKey();Point2D.Double p=e.getValue();boolean selected=path.contains(s);g.setColor(s.equals(state)?Ui.AMBER:selected?Ui.TEAL:seen.contains(s)?Ui.BLUE:Ui.LIGHT);g.fillOval((int)p.x-17,(int)p.y-17,34,34);g.setColor(Ui.MUTED);g.setStroke(new BasicStroke(problem.goal(s)?3:1));g.drawOval((int)p.x-17,(int)p.y-17,34,34);center(g,s,(int)p.x,(int)p.y+5,13,selected||seen.contains(s)?Color.WHITE:Ui.INK,true);}
        text(g,"Raíz: profundidad 0. Sucesores: 2n, 2n+1. La vista muestra, como máximo, los nodos 1…31.",25,438,14,Ui.MUTED,false);
    }
    private static void arrow(Graphics2D g,double x,double y,double angle,int size){Path2D p=new Path2D.Double();p.moveTo(x,y);p.lineTo(x-size*Math.cos(angle-.4),y-size*Math.sin(angle-.4));p.lineTo(x-size*Math.cos(angle+.4),y-size*Math.sin(angle+.4));p.closePath();g.fill(p);}
    static void text(Graphics2D g,String s,int x,int y,int size,Color color,boolean bold){g.setFont(new Font(Font.SANS_SERIF,bold?Font.BOLD:Font.PLAIN,size));g.setColor(color);g.drawString(s,x,y);}
    static void center(Graphics2D g,String s,int x,int y,int size,Color color,boolean bold){g.setFont(new Font(Font.SANS_SERIF,bold?Font.BOLD:Font.PLAIN,size));g.setColor(color);g.drawString(s,x-g.getFontMetrics().stringWidth(s)/2,y);}
}
