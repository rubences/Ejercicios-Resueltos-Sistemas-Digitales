package lab;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/** Punto de entrada del laboratorio de escritorio. */
@SuppressWarnings("serial")
public final class App extends JFrame {
    public enum Module {
        LINE("01","Seguidor de línea","Tema 1 · agentes reactivos"),
        GRAFO2("02","Grafo 2","Tema 2 · estrategias de búsqueda"),
        CLASE("03","Grafo de clase","Tema 2 · costes y heurística"),
        DLS_ROMANIA("04","DLS: la poda importa","Tema 2 · Arad → Bucarest"),
        ASTAR_ROMANIA("05","A* en Rumanía","Tema 2 · Oradea → Bucarest"),
        PING("06","Ping-pong","Tema 2 · problema sin solución"),
        ISLANDS("07","Lobo, oveja y col","Tema 2 · restricciones"),
        MISSIONARIES("08","Misioneros y caníbales","hw1 · representación de estados"),
        BINARY("09","Árbol binario","hw1 · BFS, DLS e IDS"),
        TRAP("10","Una mala heurística","hw1 · Greedy frente a BFS"),
        COLUMN("11","Column Jump","hw1 · búsqueda en tableros"),
        GUIDE("12","Guía y soluciones","Respuestas · supuestos · fuentes");
        final String number,title,subtitle;
        Module(String number,String title,String subtitle){this.number=number;this.title=title;this.subtitle=subtitle;}
        public String toString(){return title;}
    }
    private final JPanel content=new JPanel(new BorderLayout());
    private final JList<Module> menu=new JList<>(Module.values());
    private SearchPanel search;private LinePanel line;
    public App(){
        super("Laboratorio visual Java · Temas 1 y 2 · rubences");
        Ui.setup();setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);setSize(1400,960);setMinimumSize(new Dimension(1120,780));setLocationRelativeTo(null);
        JPanel sidebar=new JPanel(new BorderLayout(0,18));sidebar.setBackground(Ui.NAVY);sidebar.setPreferredSize(new Dimension(246,800));sidebar.setBorder(new EmptyBorder(24,16,18,14));
        JLabel brand=new JLabel("<html><span style='font-size:12px;color:#85cbd0'>JAVA / ESCRITORIO</span><br><span style='font-size:24px;color:white'>Laboratorio</span><br><span style='font-size:12px;color:#bdcddc'>Agentes y búsqueda · T1–T2</span></html>");sidebar.add(brand,BorderLayout.NORTH);
        menu.setBackground(Ui.NAVY);menu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);menu.setFixedCellHeight(57);menu.setBorder(null);
        menu.setCellRenderer((list,value,index,selected,focus)->{
            JLabel l=new JLabel("<html><b>"+value.number+"&nbsp; "+Ui.esc(value.title)+"</b><br><span style='font-size:10px'>"+Ui.esc(value.subtitle)+"</span></html>");
            l.setOpaque(true);l.setBackground(selected?Ui.TEAL:Ui.NAVY);l.setForeground(selected?Color.WHITE:new Color(208,221,232));l.setBorder(new EmptyBorder(5,9,5,4));return l;
        });
        JScrollPane nav=new JScrollPane(menu);nav.setBorder(null);nav.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);sidebar.add(nav,BorderLayout.CENTER);
        JLabel foot=new JLabel("<html><span style='color:#bdcddc;font-size:10px'>rubences<br>Datos originales + soluciones verificables<br>Sin servicios externos al ejecutar</span></html>");sidebar.add(foot,BorderLayout.SOUTH);
        content.setBorder(new EmptyBorder(18,20,16,20));add(sidebar,BorderLayout.WEST);add(content,BorderLayout.CENTER);
        menu.addListSelectionListener(e->{if(!e.getValueIsAdjusting()&&menu.getSelectedValue()!=null)show(menu.getSelectedValue());});
        addWindowListener(new WindowAdapter(){public void windowClosed(WindowEvent e){stopActive();}});menu.setSelectedIndex(0);
    }
    public void select(Module m){menu.setSelectedValue(m,true);}
    private void stopActive(){if(search!=null)search.deactivate();if(line!=null)line.deactivate();}
    private void show(Module m){
        stopActive();content.removeAll();search=null;line=null;
        if(m==Module.LINE){line=new LinePanel();content.add(line);}
        else if(m==Module.GUIDE)content.add(Ui.scroll(Ui.html(Lessons.guide())));
        else{search=new SearchPanel(m);content.add(search);}
        content.revalidate();content.repaint();
    }
    public SearchPanel activeSearch(){return search;}
    public static void main(String[] args){
        Ui.setup();
        if(GraphicsEnvironment.isHeadless()){System.err.println("Se necesita un escritorio gráfico. Para las pruebas: java Build.java test");System.exit(2);}
        SwingUtilities.invokeLater(()->new App().setVisible(true));
    }
}
