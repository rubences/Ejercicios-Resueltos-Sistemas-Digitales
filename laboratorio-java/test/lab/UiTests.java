package lab;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.nio.file.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/** Pruebas de interacción sobre controles Swing reales, sin mocks. */
public final class UiTests {
    private static App app;
    private static List<Component> components(Container parent){List<Component> out=new ArrayList<>();for(Component c:parent.getComponents()){out.add(c);if(c instanceof Container child)out.addAll(components(child));}return out;}
    private static void edt(Runnable r)throws Exception{SwingUtilities.invokeAndWait(r);}
    private static void click(String label){components(app).stream().filter(c->c instanceof JButton b&&b.getText().equals(label)).map(c->(JButton)c).findFirst().orElseThrow().doClick();}
    private static void waitResult()throws Exception{long until=System.nanoTime()+30_000_000_000L;while(System.nanoTime()<until){boolean[] busy={true};edt(()->busy[0]=app.activeSearch().isBusy());if(!busy[0])return;Thread.sleep(50);}throw new AssertionError("GUI no termina");}
    private static void photo(String file)throws Exception{edt(()->{BufferedImage image=new BufferedImage(app.getWidth(),app.getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D g=image.createGraphics();app.paint(g);g.dispose();try{ImageIO.write(image,"png",Path.of("build/screenshots",file).toFile());}catch(Exception e){throw new RuntimeException(e);}});}
    public static void main(String[] args)throws Exception{
        Files.createDirectories(Path.of("build/screenshots"));edt(()->{app=new App();app.setVisible(true);});
        try{
            edt(()->{
                JTabbedPane tabs=components(app).stream().filter(c->c instanceof JTabbedPane).map(c->(JTabbedPane)c).findFirst().orElseThrow();tabs.setSelectedIndex(1);
                JComboBox<?> combo=components(app).stream().filter(c->c instanceof JComboBox<?> b&&b.getItemCount()==2&&"Normal".equals(b.getItemAt(0))).map(c->(JComboBox<?>)c).findFirst().orElseThrow();
                int photos=0;
                for(int group=0;group<2;group++){combo.setSelectedIndex(group);JSlider slider=components(tabs.getComponentAt(1) instanceof Container p?p:app).stream().filter(c->c instanceof JSlider).map(c->(JSlider)c).findFirst().orElseThrow();for(int i=0;i<=slider.getMaximum();i++){slider.setValue(i);long count=components(app).stream().filter(c->c instanceof JLabel l&&l.getIcon() instanceof ImageIcon).count();if(count!=1)throw new AssertionError("Falta captura "+i);photos++;}}
                if(photos!=29)throw new AssertionError("29 fotografías esperadas");
            });photo("gallery.png");System.out.println("UI PASS 29 capturas originales decodificadas");
            edt(()->{app.select(App.Module.PING);JComboBox<?> combo=components(app).stream().filter(c->c instanceof JComboBox<?> b&&b.getItemCount()==3&&"AB".equals(b.getItemAt(0))).map(c->(JComboBox<?>)c).findFirst().orElseThrow();combo.setSelectedItem("BC");app.activeSearch().runSearch();});waitResult();
            edt(()->{if(app.activeSearch().result()==null||app.activeSearch().result().cost()!=20)throw new AssertionError("Variante BC incorrecta");app.activeSearch().showLast();});photo("ping-variant.png");System.out.println("UI PASS variante BC: 21 partidos");
            edt(()->{app.select(App.Module.DLS_ROMANIA);components(app).stream().filter(c->c instanceof JCheckBox b&&b.getText().startsWith("Poda booleana")).map(c->(JCheckBox)c).findFirst().orElseThrow().setSelected(true);app.activeSearch().runSearch();});waitResult();edt(()->{if(app.activeSearch().result()==null||app.activeSearch().result().found())throw new AssertionError("La poda errónea no reproduce el fallo");});System.out.println("UI PASS demostración DLS errónea separada");
            edt(()->{app.select(App.Module.GRAFO2);click("Comparar estrategias");});waitResult();edt(()->{boolean exists=components(app).stream().filter(c->c instanceof JTable).map(c->(JTable)c).anyMatch(t->t.getColumnCount()==7&&t.getRowCount()==7);if(!exists)throw new AssertionError("Comparativa de siete estrategias ausente");app.activeSearch().showLast();});photo("comparison.png");System.out.println("UI PASS comparación de siete estrategias");
            edt(()->{app.select(App.Module.COLUMN);click("Resolver");click("Cancelar");});waitResult();System.out.println("UI PASS cancelar desde botón");
        }finally{edt(()->app.dispose());}
    }
}
