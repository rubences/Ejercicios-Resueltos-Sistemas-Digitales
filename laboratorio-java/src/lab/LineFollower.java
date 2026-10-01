package lab;

import java.awt.Shape;
import java.awt.BasicStroke;
import java.awt.geom.*;
import java.util.*;

/** Simulación cinemática didáctica; no reproduce el motor físico de los AVI. */
public final class LineFollower {
    public record Decision(double speed,double turn,String rule) {}
    public record Sensor(double x,double y,boolean black) {}
    private final Path2D track=new Path2D.Double();
    private final Shape detection;
    private final List<Point2D.Double> trail=new ArrayList<>();
    private double x,y,heading,lastTurn;private int ticks,lost;
    private Decision decision=new Decision(0,0,"Inicio");
    public LineFollower(){
        track.moveTo(330,60);track.curveTo(600,30,650,90,650,250);track.curveTo(650,420,590,450,390,440);
        track.curveTo(180,440,160,340,400,300);track.curveTo(640,260,500,205,350,210);
        track.curveTo(100,210,130,55,330,60);track.closePath();
        detection=new BasicStroke(18,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND).createStrokedShape(track);reset();
    }
    public static Decision decide(boolean left,boolean center,boolean right,double last,boolean memory){
        if(!left&&!center&&!right)return memory?new Decision(0,last<0?-1.9:1.9,"000: buscar hacia la última detección"):new Decision(62,0,"000 sin memoria: avanzar recto (fallo didáctico)");
        if(left&&!right)return new Decision(center?48:32,center?-.75:-1.6,"Línea a la izquierda: corregir izquierda");
        if(right&&!left)return new Decision(center?48:32,center?.75:1.6,"Línea a la derecha: corregir derecha");
        return new Decision(62,0,center?"Línea centrada: avanzar":"Sensores laterales: mantener dirección");
    }
    public List<Sensor> sensors(){List<Sensor> out=new ArrayList<>();for(int offset:new int[]{-11,0,11}){
        double sx=x+Math.cos(heading)*17-Math.sin(heading)*offset,sy=y+Math.sin(heading)*17+Math.cos(heading)*offset;
        out.add(new Sensor(sx,sy,detection.contains(sx,sy)));}return out;}
    public void step(boolean memory,double rate){
        if(!Double.isFinite(rate)||rate<=0||rate>5)throw new IllegalArgumentException("Velocidad 0–5.");
        var sensors=sensors();decision=decide(sensors.get(0).black(),sensors.get(1).black(),sensors.get(2).black(),lastTurn,memory);
        if(sensors.stream().anyMatch(Sensor::black)&&decision.turn()!=0)lastTurn=decision.turn();
        if(sensors.stream().noneMatch(Sensor::black))lost++;
        double dt=.02*rate;heading=Math.IEEEremainder(heading+decision.turn()*dt,2*Math.PI);
        x+=Math.cos(heading)*decision.speed()*dt;y+=Math.sin(heading)*decision.speed()*dt;ticks++;
        if(ticks%5==0){trail.add(new Point2D.Double(x,y));if(trail.size()>1500)trail.remove(0);}
    }
    public void reset(){x=650;y=250;heading=Math.PI/2;lastTurn=1;ticks=0;lost=0;trail.clear();decision=new Decision(0,0,"Inicio");}
    public void perturb(){x+=Math.cos(heading)*8-Math.sin(heading)*12;y+=Math.sin(heading)*8+Math.cos(heading)*12;}
    public Shape track(){return new Path2D.Double(track);} public double x(){return x;}public double y(){return y;}public double heading(){return heading;}
    public int ticks(){return ticks;}public int lost(){return lost;}public Decision decision(){return decision;}
    public List<Point2D.Double> trail(){return trail.stream().map(p->new Point2D.Double(p.x,p.y)).toList();}
}
