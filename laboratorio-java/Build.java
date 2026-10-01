import javax.tools.ToolProvider;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

/** Compilación reproducible con el JDK: no requiere Maven, Gradle ni conexión. */
public final class Build {
    public static void main(String[] args) throws Exception {
        String command=args.length==0?"package":args[0];
        if(!Set.of("package","test","gui-test","run","clean").contains(command))throw new IllegalArgumentException("Uso: java Build.java [package|test|gui-test|run|clean]");
        Path root=Path.of("").toAbsolutePath().normalize(),classes=root.resolve("build/classes");
        if(!Files.isDirectory(root.resolve("src/lab")))throw new IllegalStateException("Ejecuta desde la carpeta laboratorio-java.");
        if(command.equals("clean")){remove(root.resolve("build"));remove(root.resolve("dist"));return;}
        if(Runtime.version().feature()<17)throw new IllegalStateException("Se necesita JDK 17 o posterior.");
        var compiler=ToolProvider.getSystemJavaCompiler();if(compiler==null)throw new IllegalStateException("Instala un JDK, no solo un JRE, para compilar.");
        remove(classes);Files.createDirectories(classes);
        List<String> options=new ArrayList<>(List.of("--release","17","-encoding","UTF-8","-Xlint:all","-d",classes.toString()));
        for(String folder:List.of("src","test"))try(var files=Files.walk(root.resolve(folder))){files.filter(p->p.toString().endsWith(".java")).sorted().forEach(p->options.add(p.toString()));}
        int result=compiler.run(null,System.out,System.err,options.toArray(String[]::new));if(result!=0)throw new IllegalStateException("La compilación ha fallado: "+result);
        copyResources(root.resolve("resources"),classes);gallery(root,classes);
        if(command.equals("test"))launch(classes,"lab.AllTests");
        else if(command.equals("gui-test"))launch(classes,"lab.GuiSmoke");
        else if(command.equals("run"))launch(classes,"lab.App");
        else {
            Path dist=root.resolve("dist");Files.createDirectories(dist);Path jar=dist.resolve("laboratorio-visual.jar");
            Manifest manifest=new Manifest();manifest.getMainAttributes().putValue("Manifest-Version","1.0");manifest.getMainAttributes().putValue("Main-Class","lab.App");manifest.getMainAttributes().putValue("Implementation-Version","1.0.0");
            try(JarOutputStream out=new JarOutputStream(Files.newOutputStream(jar),manifest);var files=Files.walk(classes)){
                for(Path file:files.filter(Files::isRegularFile).sorted().toList()){
                    String name=classes.relativize(file).toString().replace(File.separatorChar,'/');
                    if(name.startsWith("lab/AllTests")||name.startsWith("lab/GuiSmoke")||name.startsWith("lab/UiTests"))continue;
                    JarEntry entry=new JarEntry(name);entry.setTime(0);out.putNextEntry(entry);Files.copy(file,out);out.closeEntry();
                }
            }
            Files.writeString(dist.resolve("LEEME.txt"),"Laboratorio visual Java - Temas 1 y 2\nRequiere Java 17+ y un escritorio gráfico.\nEjecutar: java -jar laboratorio-visual.jar\nNo requiere conexión a internet. Galería original incluida.\n",StandardCharsets.UTF_8);
            System.out.println("JAR creado: "+jar+" ("+Files.size(jar)+" bytes)");
        }
    }
    private static void launch(Path classes,String main) throws Exception {
        String exe=Path.of(System.getProperty("java.home"),"bin",System.getProperty("os.name").startsWith("Windows")?"java.exe":"java").toString();
        int code=new ProcessBuilder(exe,"-Dfile.encoding=UTF-8","-Dstdout.encoding=UTF-8","-Dstderr.encoding=UTF-8","-Xmx768m","-cp",classes.toString(),main).inheritIO().start().waitFor();
        if(code!=0)throw new IllegalStateException(main+" terminó con código "+code);
    }
    private static void remove(Path dir) throws IOException {if(Files.exists(dir))try(var files=Files.walk(dir)){for(Path file:files.sorted(Comparator.reverseOrder()).toList())Files.delete(file);}}
    private static void copyResources(Path source,Path dest) throws IOException {if(Files.isDirectory(source))try(var files=Files.walk(source)){for(Path p:files.filter(Files::isRegularFile).toList()){Path out=dest.resolve(source.relativize(p));Files.createDirectories(out.getParent());Files.copy(p,out,StandardCopyOption.REPLACE_EXISTING);}}}
    private static void gallery(Path root,Path classes) throws IOException {
        Path source=root.getParent().resolve("Tema1/SeguidorLinea.zip");Path dest=classes.resolve("gallery");
        if(!Files.exists(source)){if(Files.exists(dest.resolve("index.txt")))return;throw new IOException("Falta ../Tema1/SeguidorLinea.zip; usa el ZIP completo del repositorio.");}
        Files.createDirectories(dest);List<String[]> entries=new ArrayList<>();
        try(ZipFile zip=new ZipFile(source.toFile())){
            for(ZipEntry entry:Collections.list(zip.entries())){
                String name=entry.getName().replace('\\','/');if(!name.toLowerCase(Locale.ROOT).endsWith(".jpg")||entry.getSize()>2_000_000)continue;
                String group=name.contains("/Normal/")?"Normal":name.contains("/Error/")?"Error":null;if(group==null)continue;
                String base=name.substring(name.lastIndexOf('/')+1);if(!base.matches("Pictures[0-9]+\\.jpg"))continue;
                String output=group.toLowerCase(Locale.ROOT)+"-"+base;
                try(InputStream in=zip.getInputStream(entry)){Files.copy(in,dest.resolve(output),StandardCopyOption.REPLACE_EXISTING);}
                entries.add(new String[]{group,output,base});
            }
        }
        entries.sort(Comparator.<String[],String>comparing(a->a[0]).thenComparingInt(a->Integer.parseInt(a[2].replaceAll("[^0-9]",""))));
        if(entries.size()!=29)throw new IOException("Se esperaban 29 capturas originales; encontradas "+entries.size());
        StringBuilder index=new StringBuilder();for(String[] e:entries)index.append(String.join("|",e)).append('\n');Files.writeString(dest.resolve("index.txt"),index,StandardCharsets.UTF_8);
    }
}
