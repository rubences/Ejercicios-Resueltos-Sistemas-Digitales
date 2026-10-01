package lab;

import java.util.*;

/** Formulaciones independientes; ninguna modifica el estado recibido. */
public final class Puzzles {
    private Puzzles() {}
    public static Problem binary(int target,int maximum) {
        if(target<1||maximum<target||maximum>1023)throw new IllegalArgumentException("Árbol: 1 ≤ meta ≤ máximo ≤ 1023.");
        return new Problem() {
            public String initial(){return "1";} public boolean goal(String s){return Integer.parseInt(s)==target;}
            public List<Edge> next(String s){int n=Integer.parseInt(s);List<Edge> out=new ArrayList<>();for(int x=2*n;x<=2*n+1&&x<=maximum;x++)out.add(new Edge(""+x,n+" → "+x,1));return out;}
        };
    }
    public static Problem pingPong(int a,int b,int c) { return pingPong(a,b,c,"AB"); }
    public static Problem pingPong(int a,int b,int c,String first) {
        if(!List.of("AB","AC","BC").contains(first))throw new IllegalArgumentException("Primer partido: AB, AC o BC.");
        int total=a+b+c;
        if(a<0||b<0||c<0||a>40||b>40||c>40||total<2||total%2!=0||Math.max(a,Math.max(b,c))>total/2)
            throw new IllegalArgumentException("Contadores 0–40, suma par y al menos un partido; nadie puede superar el total/2.");
        int[] targets={a,b,c};
        for(char ch:first.toCharArray())if(targets[ch-'A']<1)throw new IllegalArgumentException("Los jugadores iniciales deben jugar al menos un partido.");
        String initial=first+":"+(first.contains("A")?1:0)+","+(first.contains("B")?1:0)+","+(first.contains("C")?1:0);
        return new Problem() {
            public String initial(){return initial;}
            public boolean goal(String s){return Arrays.equals(counts(s),targets);}
            public List<Edge> next(String s){
                List<Edge> out=new ArrayList<>();String pair=s.substring(0,2);int[] counts=counts(s);
                char outsider='A';while(pair.indexOf(outsider)>=0)outsider++;
                for(char winner:pair.toCharArray()){
                    int[] t=counts.clone();t[winner-'A']++;t[outsider-'A']++;
                    if(t[0]>a||t[1]>b||t[2]>c)continue;
                    char[] next={winner,outsider};Arrays.sort(next);
                    out.add(new Edge(new String(next)+":"+t[0]+","+t[1]+","+t[2],"Gana "+winner+"; entra "+outsider,1));
                }return out;
            }
            public String label(String s){return s.replace(":"," · ");}
        };
    }
    public static int[] counts(String ping){return Arrays.stream(ping.substring(3).split(",")).mapToInt(Integer::parseInt).toArray();}
    /** Bits L=1, O=2, C=4, B=8. Uno significa isla derecha. */
    public static boolean safeIsland(int state) {
        boolean l=(state&1)!=0,o=(state&2)!=0,c=(state&4)!=0,b=(state&8)!=0;
        return !((l==o&&b!=o)||(o==c&&b!=o));
    }
    public static Problem islands() {
        return new Problem(){
            public String initial(){return "0";} public boolean goal(String s){return s.equals("15");}
            public List<Edge> next(String s){int n=Integer.parseInt(s);List<Edge> out=new ArrayList<>();
                for(int bit:new int[]{2,1,4,0}){
                    if(bit!=0&&((n&bit)!=0)!=((n&8)!=0))continue;
                    int t=n^8^bit;if(!safeIsland(t))continue;
                    String cargo=switch(bit){case 1->"lobo";case 2->"oveja";case 4->"col";default->"solo";};
                    out.add(new Edge(""+t,"Barquero "+cargo+((n&8)==0?" → B":" → A"),1));
                }return out;
            }
            public String label(String s){int n=Integer.parseInt(s);StringBuilder a=new StringBuilder(),b=new StringBuilder();String[] names={"L","O","C","B"};for(int i=0;i<4;i++)((n&(1<<i))==0?a:b).append(names[i]);return "{"+a+"} | {"+b+"}";}
        };
    }
    public static Problem missionaries() {
        return new Problem(){
            public String initial(){return "3,3,0";} public boolean goal(String s){return s.equals("0,0,1");}
            public List<Edge> next(String s){int[] n=Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();List<Edge> out=new ArrayList<>();
                for(int[] move:new int[][]{{0,2},{1,1},{2,0},{0,1},{1,0}}){
                    int sign=n[2]==0?-1:1,m=n[0]+sign*move[0],c=n[1]+sign*move[1];
                    if(m<0||m>3||c<0||c>3||m>0&&m<c||3-m>0&&3-m<3-c)continue;
                    out.add(new Edge(m+","+c+","+(1-n[2]),move[0]+" M + "+move[1]+" C "+(sign<0?"→ B":"→ A"),1));
                }return out;
            }
            public String label(String s){String[] n=s.split(",");return "A: "+n[0]+"M "+n[1]+"C · bote "+(n[2].equals("0")?"A":"B");}
        };
    }
    public static final class Column implements Problem {
        private final int size,colors;private final String start;
        public Column(String input){
            String[] lines=input.strip().split("\\s+");
            try{
                if(lines.length<3)throw new IllegalArgumentException("Faltan tamaño, colores o filas.");
                size=Integer.parseInt(lines[0]);colors=Integer.parseInt(lines[1]);
                if(size<2||size>7||colors<1||colors>9||lines.length!=size+2)throw new IllegalArgumentException("Tablero: tamaño 2–7, colores 1–9 y exactamente n filas.");
                StringBuilder board=new StringBuilder();
                for(int row=0;row<size;row++){String l=lines[row+2];if(l.length()!=size)throw new IllegalArgumentException("Cada fila debe tener "+size+" dígitos.");for(char ch:l.toCharArray())if(ch<'0'||ch>'0'+colors)throw new IllegalArgumentException("Color fuera de rango.");board.append(l);}
                start=board.toString();if(start.chars().allMatch(ch->ch=='0'))throw new IllegalArgumentException("El tablero debe contener alguna bola.");
            }catch(NumberFormatException ex){throw new IllegalArgumentException("Las dos primeras líneas deben ser números enteros.",ex);}
        }
        public int size(){return size;} public int colors(){return colors;} public String initial(){return start;}
        public boolean goal(String s){return balls(s)==1;}
        public static int balls(String s){return (int)s.chars().filter(c->c!='0').count();}
        public double heuristic(String s){return Math.max(0,s.chars().filter(c->c!='0').distinct().count()-1);}
        public String label(String s){return "Bolas="+balls(s)+" · "+s;}
        public List<Edge> next(String s){
            List<Edge> out=new ArrayList<>();
            for(int r=0;r<size;r++)for(int c=0;c<size;c++){
                char own=s.charAt(r*size+c);if(own=='0')continue;
                for(int[] d:new int[][]{{-1,0},{0,1},{1,0},{0,-1}}){
                    int y=r+d[0],x=c+d[1];if(!inside(y,x))continue;
                    char jumped=s.charAt(y*size+x);if(jumped=='0'||jumped==own)continue;
                    List<Integer> removed=new ArrayList<>();
                    while(inside(y,x)&&s.charAt(y*size+x)==jumped){removed.add(y*size+x);y+=d[0];x+=d[1];}
                    if(!inside(y,x)||s.charAt(y*size+x)!='0')continue;
                    char[] a=s.toCharArray();a[r*size+c]='0';for(int i:removed)a[i]='0';a[y*size+x]=own;
                    out.add(new Edge(new String(a),"("+(r+1)+","+(c+1)+") → ("+(y+1)+","+(x+1)+")",1));
                }
            }return out;
        }
        private boolean inside(int r,int c){return r>=0&&r<size&&c>=0&&c<size;}
        public String move(String s,int r,int c,int y,int x){
            String action="("+(r+1)+","+(c+1)+") → ("+(y+1)+","+(x+1)+")";
            return next(s).stream().filter(e->e.action().equals(action)).findFirst().orElseThrow(()->new IllegalArgumentException("Salto no permitido: "+action)).state();
        }
    }
}
