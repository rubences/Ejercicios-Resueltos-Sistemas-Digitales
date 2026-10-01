package lab;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

/** Agente reactivo didáctico y visor de las capturas originales del ZIP. */
@SuppressWarnings("serial")
final class LinePanel extends JPanel {
    private final LineFollower robot=new LineFollower();
    private final JCheckBox memory=new JCheckBox("Memoria de última corrección",true);
    private final JSlider speed=new JSlider(25,200,100);
    private final JLabel telemetry=Ui.label("",14,false);
    private final World world=new World();
    private final javax.swing.Timer timer;
    private final JButton play;
    private final List<String[]> gallery=new ArrayList<>();
    private final JLabel picture=new JLabel("Galería: compila con java Build.java package",SwingConstants.CENTER);
    private final JLabel caption=Ui.label("",13,true);
    private final JComboBox<String> group=new JComboBox<>(new String[]{"Normal","Error"});
    private final JSlider photo=new JSlider(0,14,0);
    LinePanel(){
        super(new BorderLayout(0,12));
        JPanel heading=new JPanel(new BorderLayout(0,8));heading.add(Ui.label("Seguidor de línea",27,true),BorderLayout.NORTH);heading.add(Ui.label("<html>Percepción → regla → acción. Simulación Java 2D inspirada en los vídeos originales; no reproduce su motor físico.</html>",14,false),BorderLayout.CENTER);add(heading,BorderLayout.NORTH);
        timer=new javax.swing.Timer(20,e->{robot.step(memory.isSelected(),speed.getValue()/100.0);refresh();});
        play=Ui.primary("Iniciar",()->{if(timer.isRunning()){timer.stop();playText("Iniciar");}else{timer.start();playText("Pausar");}});
        JPanel controls=Ui.row(play,Ui.button("Un paso",()->{timer.stop();playText("Iniciar");robot.step(memory.isSelected(),speed.getValue()/100.0);refresh();}),Ui.button("Reiniciar",()->{timer.stop();playText("Iniciar");robot.reset();refresh();}),Ui.button("Desplazar robot",()->{robot.perturb();refresh();}),memory);
        JPanel config=new JPanel(new BorderLayout());config.add(controls,BorderLayout.NORTH);speed.setPreferredSize(new Dimension(210,30));config.add(Ui.row(Ui.label("Velocidad de simulación",13,true),speed,Ui.label("El desplazamiento permite provocar una pérdida de línea.",12,false)),BorderLayout.SOUTH);
        JPanel simulation=new JPanel(new BorderLayout(0,8));simulation.add(config,BorderLayout.NORTH);simulation.add(world,BorderLayout.CENTER);telemetry.setBorder(BorderFactory.createEmptyBorder(9,8,9,8));simulation.add(telemetry,BorderLayout.SOUTH);
        JTabbedPane tabs=new JTabbedPane();tabs.addTab("Simulación interactiva",simulation);tabs.addTab("Capturas originales (29)",galleryPanel());tabs.addTab("Cómo funciona",Ui.scroll(Ui.html(explanation())));add(tabs,BorderLayout.CENTER);refresh();
    }
    private void playText(String text){play.setText(text);}
    private void refresh(){
        List<LineFollower.Sensor> s=robot.sensors();String mask=""+(s.get(0).black()?1:0)+(s.get(1).black()?1:0)+(s.get(2).black()?1:0);
        telemetry.setText("<html><b>Sensores L / C / R: "+mask+"</b> &nbsp; · &nbsp; pasos "+robot.ticks()+" &nbsp; · &nbsp; lecturas sin línea "+robot.lost()+"<br>"+Ui.esc(robot.decision().rule())+"<br><span style='color:#586c7c'>Velocidad = "+Search.fmt(robot.decision().speed())+" u/s · giro = "+Search.fmt(robot.decision().turn())+" rad/s. Unidades de simulación, no medidas del robot original.</span></html>");world.repaint();
    }
    void deactivate(){timer.stop();}
    private JPanel galleryPanel(){
        JPanel p=new JPanel(new BorderLayout(8,10));
        try(InputStream stream=LinePanel.class.getResourceAsStream("/gallery/index.txt")){
            if(stream!=null)try(BufferedReader r=new BufferedReader(new InputStreamReader(stream,StandardCharsets.UTF_8))){String line;while((line=r.readLine())!=null)gallery.add(line.split("\\|"));}
        }catch(IOException ex){caption.setText("No se pudo leer la galería: "+ex.getMessage());}
        p.add(Ui.row(Ui.label("Secuencia original",14,true),group,Ui.button("Anterior",()->photo.setValue(Math.max(0,photo.getValue()-1))),Ui.button("Siguiente",()->photo.setValue(Math.min(photo.getMaximum(),photo.getValue()+1)))),BorderLayout.NORTH);
        picture.setOpaque(true);picture.setBackground(Color.WHITE);p.add(Ui.scroll(picture),BorderLayout.CENTER);JPanel bottom=new JPanel(new BorderLayout());bottom.add(photo,BorderLayout.NORTH);bottom.add(caption,BorderLayout.SOUTH);p.add(bottom,BorderLayout.SOUTH);
        group.addActionListener(e->{photo.setValue(0);updatePhoto();});photo.addChangeListener(e->updatePhoto());updatePhoto();return p;
    }
    private void updatePhoto(){
        List<String[]> selected=gallery.stream().filter(x->x[0].equals(group.getSelectedItem())).toList();if(selected.isEmpty())return;
        photo.setMaximum(selected.size()-1);String[] item=selected.get(Math.min(photo.getValue(),selected.size()-1));
        try(InputStream in=LinePanel.class.getResourceAsStream("/gallery/"+item[1])){
            if(in==null)throw new IOException("Captura no encontrada");BufferedImage image=ImageIO.read(in);if(image==null)throw new IOException("JPEG inválido");double scale=Math.min(1,850.0/image.getWidth());picture.setIcon(new ImageIcon(image.getScaledInstance((int)(image.getWidth()*scale),(int)(image.getHeight()*scale),Image.SCALE_SMOOTH)));picture.setText("");caption.setText(item[0]+" · "+item[2]+" · "+(photo.getValue()+1)+" / "+selected.size()+" · imagen original, sin alterar");
        }catch(IOException ex){picture.setIcon(null);picture.setText(ex.getMessage());}
    }
    private static String explanation(){return """
        <h1>Un agente que percibe y actúa</h1><p>El ZIP original contiene dos vídeos AVI y 29 capturas de un robot con tres sensores. No incluye código ni parámetros físicos. Este módulo reconstruye un escenario docente en Java 2D, con un circuito aproximado y parámetros propios.</p>
        <h2>Estado, percepción y política</h2><p>El entorno conserva posición (x,y), orientación, circuito y sensores. El controlador recibe solo tres detecciones binarias y, en el modo con memoria, el sentido de la última corrección. No recibe la posición ideal sobre el circuito ni un camino pregrabado.</p>
        <table><tr><th>Lectura</th><th>Acción</th></tr><tr><td>Centro sobre la línea</td><td>Avanzar.</td></tr><tr><td>Izquierda sin derecha</td><td>Corregir a la izquierda; reducir velocidad.</td></tr><tr><td>Derecha sin izquierda</td><td>Corregir a la derecha; reducir velocidad.</td></tr><tr><td>000, sin memoria</td><td>Continuar recto: fallo deliberado para discutir pérdida de percepción.</td></tr><tr><td>000, con memoria</td><td>Girar hacia la última detección, sin traslación.</td></tr></table>
        <h2>Experimento para el aula</h2><p>Inicia el robot, pausa y observa L/C/R. Avanza por pasos. Usa «Desplazar robot» y compara las dos políticas tras reiniciar. Explica cuándo una memoria mínima ayuda y cuándo no basta: no garantiza recuperar cualquier desplazamiento arbitrario ni completar un circuito desconocido.</p>
        <h2>Actualización del movimiento</h2><p>θ ← θ + ω·Δt; x ← x + v·cos(θ)·Δt; y ← y + v·sin(θ)·Δt. El eje y de la pantalla crece hacia abajo. La lectura se obtiene por intersección de los sensores con la línea, no por una secuencia de respuestas fija.</p>
        <p>La pestaña de capturas muestra las series Normal y Error conservadas en SeguidorLinea.zip. Los vídeos se mantienen en ese ZIP para abrirlos con un reproductor compatible; la aplicación Java no incorpora un decodificador AVI.</p>
        """;}
    private final class World extends JPanel {
        World(){setBackground(Color.WHITE);setPreferredSize(new Dimension(1000,560));}
        @Override protected void paintComponent(Graphics graphics){
            super.paintComponent(graphics);Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            double scale=Math.min(getWidth()/1000.0,getHeight()/540.0);g.translate((getWidth()-1000*scale)/2,(getHeight()-540*scale)/2);g.scale(scale,scale);
            g.setColor(Ui.LIGHT);g.fillRoundRect(15,15,785,510,24,24);g.setColor(new Color(223,231,236));g.setStroke(new BasicStroke(1));for(int x=40;x<795;x+=40)g.drawLine(x,25,x,510);for(int y=40;y<515;y+=40)g.drawLine(25,y,790,y);
            g.setColor(Ui.INK);g.setStroke(new BasicStroke(18,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));g.draw(robot.track());
            List<Point2D.Double> points=robot.trail();g.setColor(new Color(101,174,180));g.setStroke(new BasicStroke(2));for(int i=1;i<points.size();i++)g.draw(new Line2D.Double(points.get(i-1),points.get(i)));
            AffineTransform original=g.getTransform();g.translate(robot.x(),robot.y());g.rotate(robot.heading());
            g.setColor(Ui.NAVY);g.fillRoundRect(-17,-17,36,34,9,9);g.setColor(Ui.MUTED);g.fillRect(-12,-22,21,6);g.fillRect(-12,16,21,6);g.setColor(Color.WHITE);Path2D nose=new Path2D.Double();nose.moveTo(11,0);nose.lineTo(-4,-7);nose.lineTo(-4,7);nose.closePath();g.fill(nose);g.setTransform(original);
            List<LineFollower.Sensor> sensors=robot.sensors();String[] names={"L","C","R"};for(int i=0;i<sensors.size();i++){LineFollower.Sensor s=sensors.get(i);g.setColor(s.black()?Ui.AMBER:Color.WHITE);g.fill(new Ellipse2D.Double(s.x()-4,s.y()-4,8,8));g.setColor(Ui.TEAL);g.draw(new Ellipse2D.Double(s.x()-4,s.y()-4,8,8));}
            Scene.text(g,"PERCEPCIÓN",826,67,14,Ui.INK,true);for(int i=0;i<3;i++){int y=110+i*62;g.setColor(sensors.get(i).black()?Ui.TEAL:Ui.LIGHT);g.fillRoundRect(825,y-25,148,46,12,12);Scene.text(g,names[i]+"  "+(sensors.get(i).black()?"1 · línea":"0 · fondo"),838,y+3,15,sensors.get(i).black()?Color.WHITE:Ui.INK,true);}
            Scene.text(g,"POSICIÓN",826,336,14,Ui.INK,true);Scene.text(g,"x = "+Search.fmt(robot.x()),826,365,14,Ui.MUTED,false);Scene.text(g,"y = "+Search.fmt(robot.y()),826,391,14,Ui.MUTED,false);Scene.text(g,"θ = "+Search.fmt(Math.toDegrees(robot.heading()))+"°",826,417,14,Ui.MUTED,false);
            Scene.text(g,"Adaptación didáctica 2D",40,510,13,Ui.MUTED,true);g.dispose();
        }
    }
}
