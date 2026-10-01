package lab;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

final class Ui {
    static final Color NAVY=new Color(19,40,62), INK=new Color(33,51,69), MUTED=new Color(88,108,124),
        TEAL=new Color(0,120,128), LIGHT=new Color(240,245,248), LINE=new Color(216,226,233),
        AMBER=new Color(223,139,35), PALE=new Color(224,241,242), BLUE=new Color(66,111,164);
    private Ui() {}
    static void setup(){
        System.setProperty("awt.useSystemAAFontSettings","on");System.setProperty("swing.aatext","true");
        try{UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());}catch(Exception ignored){}
        Font font=new Font(Font.SANS_SERIF,Font.PLAIN,14);
        for(var key:java.util.Collections.list(UIManager.getDefaults().keys()))if(UIManager.get(key) instanceof javax.swing.plaf.FontUIResource)UIManager.put(key,new javax.swing.plaf.FontUIResource(font));
        UIManager.put("Panel.background",LIGHT);UIManager.put("Table.rowHeight",28);UIManager.put("Table.gridColor",LINE);
        UIManager.put("TextArea.font",font);UIManager.put("TabbedPane.selected",Color.WHITE);
        UIManager.put("ToolTip.background",Color.WHITE);
    }
    static JLabel label(String text,int size,boolean bold){JLabel l=new JLabel(text);l.setFont(new Font(Font.SANS_SERIF,bold?Font.BOLD:Font.PLAIN,size));l.setForeground(INK);return l;}
    static JButton button(String text,Runnable action){JButton b=new JButton(text);b.setFocusPainted(false);b.setBackground(Color.WHITE);b.setForeground(INK);b.setBorder(new CompoundBorder(new LineBorder(LINE),new EmptyBorder(8,12,8,12)));b.addActionListener(e->action.run());return b;}
    static JButton primary(String text,Runnable action){JButton b=button(text,action);b.setBackground(TEAL);b.setForeground(Color.WHITE);return b;}
    static JPanel row(Component... cs){JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,9,5));p.setAlignmentX(Component.LEFT_ALIGNMENT);p.setOpaque(false);for(Component c:cs)p.add(c);return p;}
    static JEditorPane html(String body){JEditorPane p=new JEditorPane("text/html","<html><head><style>body{font-family:sans-serif;font-size:12pt;color:#213345;margin:16px} h1{font-size:23pt;color:#007880}h2{font-size:17pt;color:#13283e}h3{font-size:14pt;color:#007880}p{margin-top:8px;margin-bottom:12px}td,th{padding:8px;border-bottom:1px solid #d8e2e9}code{font-family:monospace} </style></head><body>"+body+"</body></html>");p.setEditable(false);p.setCaretPosition(0);return p;}
    static JScrollPane scroll(Component c){JScrollPane s=new JScrollPane(c);s.setBorder(new LineBorder(LINE));s.getVerticalScrollBar().setUnitIncrement(18);return s;}
    static String esc(String text){return text.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");}
    static void error(Component parent,Throwable e){JOptionPane.showMessageDialog(parent,e.getMessage()==null?e.toString():e.getMessage(),"Revisa los datos",JOptionPane.WARNING_MESSAGE);}
}
