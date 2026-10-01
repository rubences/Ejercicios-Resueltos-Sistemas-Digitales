package lab;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;

/** Abre una ventana real; ejecuta búsquedas en su SwingWorker y guarda capturas. */
public final class GuiSmoke {
    public static void main(String[] args) throws Exception {
        Path out=Path.of(args.length==0?"build/screenshots":args[0]);Files.createDirectories(out);
        final App[] a=new App[1];
        SwingUtilities.invokeAndWait(()->{a[0]=new App();a[0].setVisible(true);});
        try {
            for(App.Module m:App.Module.values()){
                SwingUtilities.invokeAndWait(()->a[0].select(m));
                if(a[0].activeSearch()!=null){
                    SwingUtilities.invokeAndWait(()->a[0].activeSearch().runSearch());
                    long end=System.nanoTime()+25_000_000_000L;
                    while(System.nanoTime()<end){final boolean[] busy={true};SwingUtilities.invokeAndWait(()->busy[0]=a[0].activeSearch().isBusy());if(!busy[0])break;Thread.sleep(40);}
                    SwingUtilities.invokeAndWait(()->{
                        SearchPanel p=a[0].activeSearch();
                        if(p.isBusy()||p.result()==null)throw new AssertionError("Sin resultado GUI: "+m);
                        if(m==App.Module.PING&&p.result().status()!=Search.Status.NO_SOLUTION)throw new AssertionError("Ping original incorrecto");
                        if(m!=App.Module.PING&&!p.result().found())throw new AssertionError("Sin solución GUI: "+m);
                        p.showLast();
                    });
                }
                SwingUtilities.invokeAndWait(()->{
                    BufferedImage image=new BufferedImage(a[0].getWidth(),a[0].getHeight(),BufferedImage.TYPE_INT_RGB);
                    Graphics2D g=image.createGraphics();a[0].paint(g);g.dispose();
                    try{ImageIO.write(image,"png",out.resolve(m.name().toLowerCase()+".png").toFile());}catch(Exception ex){throw new RuntimeException(ex);}
                });
                System.out.println("GUI PASS "+m.name());
            }
            SwingUtilities.invokeAndWait(()->{a[0].select(App.Module.COLUMN);a[0].activeSearch().runSearch();a[0].select(App.Module.LINE);});
            System.out.println("GUI PASS cambio de módulo durante cálculo");
        } finally {SwingUtilities.invokeAndWait(()->a[0].dispose());}
    }
}
