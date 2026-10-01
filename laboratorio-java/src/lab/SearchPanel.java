package lab;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CancellationException;

/** Controlador Swing: cálculos fuera del EDT y reproducción de instantáneas inmutables. */
@SuppressWarnings("serial")
public final class SearchPanel extends JPanel {
    private final App.Module module;
    private final Scene scene=new Scene();
    private final JComboBox<Search.Strategy> strategy=new JComboBox<>(Search.Strategy.values());
    private final JSpinner depth=new JSpinner(new SpinnerNumberModel(8,0,200,1));
    private final JCheckBox unsafe=new JCheckBox("Poda booleana INCORRECTA (demostración)");
    private final JComboBox<String> first=new JComboBox<>(new String[]{"AB","AC","BC"});
    private final JSpinner ca=spinner(10,0,40),cb=spinner(15,0,40),cc=spinner(17,0,40),target=spinner(13,1,1023),maximum=spinner(15,1,1023);
    private JComboBox<String> from,to;
    private String board="4\n3\n1221\n2132\n3132\n0230\n";
    private final JPanel settings=new JPanel();
    private final JLabel status=Ui.label("Preparado · pulsa Resolver",14,true),step=Ui.label("Estado inicial",13,true);
    private final JTextArea frontier=new JTextArea(2,50),data=new JTextArea();
    private final JTabbedPane tabs=new JTabbedPane();
    private final DefaultTableModel traceModel=model("Paso","Límite IDS","Estado","d","g","h","f","Evento");
    private final DefaultTableModel comparison=model("Estrategia","Resultado","Pasos","Coste","Expandidos","Generados","ms");
    private final JTable traceTable=new JTable(traceModel);
    private final JComboBox<String> playback=new JComboBox<>(new String[]{"Camino solución","Traza de búsqueda"});
    private final JSlider cursor=new JSlider(0,0,0);
    private final JButton solve,compare,cancel;
    private JButton play;
    private javax.swing.Timer timer;
    private SwingWorker<Payload,Void> worker;
    private boolean busy,active=true,adjusting;
    private int generation;
    private Problem problem;
    private Search.Result result;
    private String usedSettings="";
    private record Row(Search.Strategy strategy,Search.Result result){}
    private record Payload(Search.Result main,List<Row> rows){}
    public SearchPanel(App.Module module){
        super(new BorderLayout(0,10));this.module=module;
        JPanel top=new JPanel();top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));
        JPanel title=new JPanel(new BorderLayout());title.add(Ui.label(module.title,27,true),BorderLayout.WEST);title.add(Ui.label(module.subtitle,12,false),BorderLayout.EAST);title.setAlignmentX(LEFT_ALIGNMENT);top.add(title);
        JLabel intro=Ui.label("<html>"+Ui.esc(Lessons.intro(module))+"</html>",14,false);intro.setBorder(new EmptyBorder(7,0,6,0));intro.setAlignmentX(LEFT_ALIGNMENT);top.add(intro);
        settings.setAlignmentX(LEFT_ALIGNMENT);settings.setLayout(new BoxLayout(settings,BoxLayout.Y_AXIS));
        settings.add(Ui.row(Ui.label("Algoritmo",13,true),strategy,Ui.label("Límite DLS / IDS",13,false),depth));
        configure();top.add(settings);
        solve=Ui.primary("Resolver",this::runSearch);compare=Ui.button("Comparar estrategias",()->start(true));cancel=Ui.button("Cancelar",this::cancel);cancel.setEnabled(false);
        top.add(Ui.row(solve,compare,cancel,Ui.button("Exportar resultado",this::export)));status.setBorder(new EmptyBorder(3,9,4,0));status.setAlignmentX(LEFT_ALIGNMENT);top.add(status);add(top,BorderLayout.NORTH);
        JPanel visual=new JPanel(new BorderLayout(0,6));visual.add(scene,BorderLayout.CENTER);
        play=Ui.button("Reproducir",()->{if(timer.isRunning()){timer.stop();play.setText("Reproducir");}else if(result!=null){if(cursor.getValue()==cursor.getMaximum())cursor.setValue(0);timer.start();play.setText("Pausar");}});
        timer=new javax.swing.Timer(650,e->{if(cursor.getValue()<cursor.getMaximum())cursor.setValue(cursor.getValue()+1);else{timer.stop();play.setText("Reproducir");}});
        JPanel replay=new JPanel(new BorderLayout(6,0));replay.add(Ui.row(playback,Ui.button("|<",()->cursor.setValue(0)),Ui.button("<",()->cursor.setValue(Math.max(0,cursor.getValue()-1))),play,Ui.button(">",()->cursor.setValue(Math.min(cursor.getMaximum(),cursor.getValue()+1)))),BorderLayout.WEST);replay.add(cursor,BorderLayout.CENTER);
        JCheckBox edges=new JCheckBox("Todos los arcos",true);edges.addActionListener(e->scene.allEdges(edges.isSelected()));replay.add(edges,BorderLayout.EAST);
        JPanel beneath=new JPanel(new BorderLayout());beneath.add(replay,BorderLayout.NORTH);beneath.add(step,BorderLayout.SOUTH);visual.add(beneath,BorderLayout.SOUTH);
        traceTable.setRowHeight(25);traceTable.setAutoCreateRowSorter(false);traceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        traceTable.getColumnModel().getColumn(2).setPreferredWidth(200);traceTable.getColumnModel().getColumn(7).setPreferredWidth(260);
        frontier.setEditable(false);frontier.setLineWrap(true);frontier.setWrapStyleWord(true);frontier.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
        JPanel traceTab=new JPanel(new BorderLayout(0,5));traceTab.add(Ui.scroll(traceTable),BorderLayout.CENTER);JPanel f=new JPanel(new BorderLayout());f.add(Ui.label("Frontera tras extraer / expandir: hasta 16 nodos en orden de prioridad",12,true),BorderLayout.NORTH);f.add(Ui.scroll(frontier),BorderLayout.CENTER);traceTab.add(f,BorderLayout.SOUTH);
        tabs.addTab("Traza y frontera",traceTab);tabs.addTab("Explicación y solución",Ui.scroll(Ui.html(Lessons.detail(module))));
        data.setEditable(false);data.setFont(new Font(Font.MONOSPACED,Font.PLAIN,13));tabs.addTab("Datos del problema",Ui.scroll(data));
        JTable comp=new JTable(comparison);comp.setRowHeight(28);comp.getColumnModel().getColumn(1).setPreferredWidth(380);tabs.addTab("Comparativa",Ui.scroll(comp));
        JSplitPane split=new JSplitPane(JSplitPane.VERTICAL_SPLIT,visual,tabs);split.setBorder(null);split.setResizeWeight(.66);split.setDividerLocation(470);split.setContinuousLayout(true);visual.setMinimumSize(new Dimension(400,310));tabs.setMinimumSize(new Dimension(400,150));add(split,BorderLayout.CENTER);
        cursor.addChangeListener(e->{if(!adjusting)render();});playback.addActionListener(e->resetPlayback());
        traceTable.getSelectionModel().addListSelectionListener(e->{if(!adjusting&&!e.getValueIsAdjusting()&&traceTable.getSelectedRow()>=0){int row=traceTable.getSelectedRow();adjusting=true;playback.setSelectedIndex(1);cursor.setMaximum(Math.max(0,result==null?0:result.trace().size()-1));cursor.setValue(row);adjusting=false;render();}});
        try{problem=createProblem();scene.view(problem,module,problem.initial(),List.of(problem.initial()),Set.of(),"Estado inicial · no se ha ejecutado la búsqueda");refreshData();}catch(IllegalArgumentException ex){Ui.error(this,ex);}
    }
    private static JSpinner spinner(int value,int min,int max){JSpinner s=new JSpinner(new SpinnerNumberModel(value,min,max,1));s.setPreferredSize(new Dimension(66,30));return s;}
    private static int number(JSpinner s){try{s.commitEdit();}catch(java.text.ParseException ex){throw new IllegalArgumentException("Introduce un número entero válido.");}return ((Number)s.getValue()).intValue();}
    private static DefaultTableModel model(String... columns){return new DefaultTableModel(columns,0){@Override public boolean isCellEditable(int r,int c){return false;}};}
    private void configure(){
        Search.Strategy defaultStrategy=switch(module){case GRAFO2->Search.Strategy.UCS;case CLASE,ASTAR_ROMANIA,COLUMN->Search.Strategy.ASTAR;case DLS_ROMANIA->Search.Strategy.DLS;case TRAP->Search.Strategy.GREEDY;default->Search.Strategy.BFS;};strategy.setSelectedItem(defaultStrategy);
        depth.setValue(module==App.Module.DLS_ROMANIA?4:module==App.Module.BINARY?3:module==App.Module.COLUMN?14:module==App.Module.GRAFO2?7:8);
        if(module==App.Module.GRAFO2||module==App.Module.CLASE||module==App.Module.DLS_ROMANIA||module==App.Module.ASTAR_ROMANIA){
            GraphProblems.Graph g=baseGraph();String[] names=g.nodes().toArray(String[]::new);from=new JComboBox<>(names);to=new JComboBox<>(names);from.setSelectedItem(g.initial());to.setSelectedItem(g.target());settings.add(Ui.row(Ui.label("Inicio",13,true),from,Ui.label("Meta",13,true),to,Ui.label("Otra meta utiliza h=0.",12,false)));
        }
        if(module==App.Module.DLS_ROMANIA){unsafe.setForeground(new Color(143,74,20));settings.add(Ui.row(unsafe));}
        if(module==App.Module.PING)settings.add(Ui.row(Ui.label("Partidos A / B / C",13,true),ca,cb,cc,Ui.label("Primera pareja",13,true),first,Ui.label("AB: original · AC / BC: variantes",12,false)));
        if(module==App.Module.BINARY)settings.add(Ui.row(Ui.label("Meta",13,true),target,Ui.label("Máximo nodo (recorte finito)",13,true),maximum));
        if(module==App.Module.COLUMN)settings.add(Ui.row(Ui.button("Editar / importar tablero",this::editBoard),Ui.label("Ejemplo visible del PDF: 4×4, tres colores, 14 bolas.",12,false)));
    }
    private GraphProblems.Graph baseGraph(){return switch(module){case GRAFO2->GraphProblems.grafo2();case CLASE->GraphProblems.clase();case DLS_ROMANIA->GraphProblems.romania("Arad");case ASTAR_ROMANIA->GraphProblems.romania("Oradea");default->throw new IllegalStateException("No es un grafo configurable");};}
    private Problem createProblem(){return switch(module){
        case GRAFO2,CLASE,DLS_ROMANIA,ASTAR_ROMANIA->baseGraph().withEndpoints((String)from.getSelectedItem(),(String)to.getSelectedItem());
        case PING->Puzzles.pingPong(number(ca),number(cb),number(cc),(String)first.getSelectedItem());case ISLANDS->Puzzles.islands();case MISSIONARIES->Puzzles.missionaries();case BINARY->Puzzles.binary(number(target),number(maximum));case TRAP->GraphProblems.heuristicTrap();case COLUMN->new Puzzles.Column(board);default->throw new IllegalArgumentException("Módulo sin búsqueda");};}
    private String settingsText(){return module.title+" | "+strategy.getSelectedItem()+" | profundidad="+number(depth)+(module==App.Module.PING?" | objetivos="+number(ca)+","+number(cb)+","+number(cc)+" | primera pareja="+first.getSelectedItem():"")+(unsafe.isSelected()?" | DEMOSTRACIÓN CON PODA INCORRECTA":"");}
    public void runSearch(){start(false);}
    private void start(boolean comparisons){
        if(busy||!active)return;
        final Problem task;final Search.Strategy chosen=(Search.Strategy)strategy.getSelectedItem();final Search.Options options;
        try{task=createProblem();options=new Search.Options(number(depth),150000,1200,15000,unsafe.isSelected());if(unsafe.isSelected()&&chosen!=Search.Strategy.DLS)throw new IllegalArgumentException("La demostración de poda incorrecta solo se permite con DLS.");usedSettings=settingsText();}catch(IllegalArgumentException ex){Ui.error(this,ex);return;}
        timer.stop();play.setText("Reproducir");result=null;problem=task;traceModel.setRowCount(0);comparison.setRowCount(0);frontier.setText("");refreshData();
        busy=true;setControls(false);status.setText("Calculando… puedes cancelar");scene.view(problem,module,problem.initial(),List.of(problem.initial()),Set.of(),"Estado inicial · cálculo en curso");int epoch=++generation;
        worker=new SwingWorker<>(){
            @Override protected Payload doInBackground(){
                Search.Result main=Search.solve(task,chosen,options,()->isCancelled());List<Row> rows=new ArrayList<>();
                if(comparisons)for(Search.Strategy s:Search.Strategy.values()){
                    if(isCancelled())break;
                    Search.Result r=Search.solve(task,s,new Search.Options(options.depthLimit(),150000,80,2500,false),()->isCancelled());rows.add(new Row(s,r));
                }
                return new Payload(main,rows);
            }
            @Override protected void done(){
                if(!active||epoch!=generation)return;busy=false;setControls(true);
                try{Payload p=get();result=p.main();
                    for(Search.Frame f:result.trace())traceModel.addRow(new Object[]{f.number(),f.iteration()<0?"—":f.iteration(),problem.label(f.state()),f.depth(),Search.fmt(f.g()),Search.fmt(f.h()),Search.fmt(f.g()+f.h()),f.event()});
                    for(Row row:p.rows()){Search.Result r=row.result();comparison.addRow(new Object[]{row.strategy(),Search.statusText(r.status()),r.found()?r.actions().size():"—",Search.fmt(r.cost()),r.expanded(),r.generated(),Search.fmt(r.millis())});}
                    String message=(options.unsafeClosed()?"PODA INCORRECTA · ":"")+Search.statusText(result.status())+(result.found()?" | coste "+Search.fmt(result.cost())+" | "+result.actions().size()+" pasos":"")+" | exp. "+result.expanded();
                    status.setText("<html>"+Ui.esc(message)+"</html>");playback.setSelectedIndex(result.found()?0:1);resetPlayback();if(comparisons)tabs.setSelectedIndex(3);
                }catch(CancellationException ex){status.setText("Cálculo cancelado");}catch(Exception ex){status.setText("Error: revisa los datos");Ui.error(SearchPanel.this,ex.getCause()==null?ex:ex.getCause());}
            }
        };worker.execute();
    }
    private void setControls(boolean enabled){enableTree(settings,enabled);solve.setEnabled(enabled);compare.setEnabled(enabled);cancel.setEnabled(!enabled);}
    private static void enableTree(Container c,boolean enabled){for(Component x:c.getComponents()){x.setEnabled(enabled);if(x instanceof Container nested)enableTree(nested,enabled);}}
    private void cancel(){if(worker!=null&&busy)worker.cancel(true);}
    public boolean isBusy(){return busy;}
    public Search.Result result(){return result;}
    public void deactivate(){active=false;++generation;timer.stop();if(worker!=null)worker.cancel(true);busy=false;}
    private void resetPlayback(){if(adjusting||result==null)return;timer.stop();play.setText("Reproducir");adjusting=true;cursor.setMaximum(Math.max(0,(playback.getSelectedIndex()==0?result.route().size():result.trace().size())-1));cursor.setValue(0);adjusting=false;render();}
    public void showLast(){if(result==null)return;playback.setSelectedIndex(result.found()?0:1);cursor.setValue(cursor.getMaximum());render();}
    private void render(){
        if(result==null)return;int i=cursor.getValue();List<String> route;String state,caption;Set<String> seen=new LinkedHashSet<>();
        if(playback.getSelectedIndex()==0&&result.found()){
            i=Math.min(i,result.route().size()-1);route=result.route().subList(0,i+1);state=route.get(route.size()-1);caption="Paso "+i+" / "+result.actions().size()+" · "+(i==0?"Inicio":result.actions().get(i-1));frontier.setText("En modo camino se reproduce la solución. Selecciona Traza de búsqueda para inspeccionar la frontera real.");
        }else{
            if(result.trace().isEmpty())return;i=Math.min(i,result.trace().size()-1);Search.Frame f=result.trace().get(i);route=f.path();state=f.state();caption="Extracción "+f.number()+" · "+f.event()+" · g="+Search.fmt(f.g())+" · h="+Search.fmt(f.h());frontier.setText(f.frontier().isEmpty()?"Frontera vacía":f.frontier());
            for(int j=0;j<=i;j++)seen.add(result.trace().get(j).state());adjusting=true;traceTable.setRowSelectionInterval(i,i);traceTable.scrollRectToVisible(traceTable.getCellRect(i,0,true));adjusting=false;
        }
        scene.view(problem,module,state,route,seen,caption);step.setText("<html>"+Ui.esc(caption)+(result.traceTruncated()?" · Traza visual limitada a 1.200 muestras; la búsqueda sí continúa.":"")+"</html>");
    }
    private void refreshData(){
        if(problem==null)return;StringBuilder s=new StringBuilder("DATOS EJECUTADOS / MODELO\n\n").append(usedSettings).append("\nEstado inicial: ").append(problem.label(problem.initial())).append("\n\n");
        if(problem instanceof GraphProblems.Graph g){s.append("Arcos dirigidos (origen, destino, coste). h corresponde a la meta.\n\n");for(String n:g.nodes()){s.append(n).append(" [h=").append(Search.fmt(g.heuristic(n))).append("]\n");for(Problem.Edge e:g.next(n))s.append("  -> ").append(e.state()).append("  coste=").append(Search.fmt(e.cost())).append('\n');}}
        else if(module==App.Module.COLUMN)s.append("Tablero (n, colores, filas):\n").append(board);
        else{s.append("Sucesores válidos desde el inicio:\n");for(Problem.Edge e:problem.next(problem.initial()))s.append(e.action()).append(" => ").append(problem.label(e.state())).append('\n');}
        s.append("\nLa explicación recoge los datos originales. Cambiar los controles crea una variante.\nLímites: 150.000 nodos generados, 15 s por resolución, 1.200 muestras de traza.\nComparativa: mismos datos, poda correcta, 150.000 nodos y 2,5 s por estrategia; no usa la poda errónea.\nLIMIT y CUTOFF no significan que el problema sea imposible.\n");data.setText(s.toString());data.setCaretPosition(0);
    }
    private void editBoard(){
        JTextArea editor=new JTextArea(board,10,24);editor.setFont(new Font(Font.MONOSPACED,Font.PLAIN,18));JPanel p=new JPanel(new BorderLayout(8,8));p.add(Ui.label("Primera línea n; segunda colores; después n filas (0 = vacío).",13,false),BorderLayout.NORTH);p.add(Ui.scroll(editor),BorderLayout.CENTER);
        p.add(Ui.button("Importar TXT",()->{JFileChooser chooser=new JFileChooser();if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)try{Path file=chooser.getSelectedFile().toPath();if(Files.size(file)>65536)throw new IllegalArgumentException("El TXT supera 64 KB.");editor.setText(Files.readString(file,StandardCharsets.UTF_8));}catch(Exception ex){Ui.error(this,ex);}}),BorderLayout.SOUTH);
        if(JOptionPane.showConfirmDialog(this,p,"Tablero Column Jump",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION)try{new Puzzles.Column(editor.getText());board=editor.getText();status.setText("Tablero modificado · pulsa Resolver para aplicarlo");}catch(IllegalArgumentException ex){Ui.error(this,ex);}
    }
    private void export(){
        if(result==null){Ui.error(this,new IllegalArgumentException("Resuelve el problema antes de exportar."));return;}
        JFileChooser chooser=new JFileChooser();chooser.setSelectedFile(new java.io.File(module.name().toLowerCase(Locale.ROOT)+"-resultado.txt"));if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        Path dest=chooser.getSelectedFile().toPath();if(Files.exists(dest)&&JOptionPane.showConfirmDialog(this,"¿Sustituir el fichero existente?","Exportar",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        StringBuilder s=new StringBuilder(usedSettings).append("\n").append(Search.statusText(result.status())).append("\nCoste: ").append(Search.fmt(result.cost())).append("\nExpandidos: ").append(result.expanded()).append("\nGenerados: ").append(result.generated()).append("\nPico de frontera: ").append(result.peak()).append("\nTiempo ms: ").append(result.millis()).append("\nTraza truncada: ").append(result.traceTruncated()).append("\n\n");
        for(int i=0;i<result.route().size();i++)s.append(i).append(" | ").append(problem.label(result.route().get(i))).append(" | ").append(i==0?"Inicio":result.actions().get(i-1)).append('\n');
        s.append("\nTRAZA (muestras retenidas)\n");for(Search.Frame f:result.trace())s.append(f.number()).append(" | ").append(f.event()).append(" | ").append(problem.label(f.state())).append(" | g=").append(f.g()).append(" | h=").append(f.h()).append(" | frontera=").append(f.frontier()).append('\n');
        try{Files.writeString(dest,s,StandardCharsets.UTF_8);status.setText("Resultado exportado: "+dest.getFileName());}catch(Exception ex){Ui.error(this,ex);}
    }
}
