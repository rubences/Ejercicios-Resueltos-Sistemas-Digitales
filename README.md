# Ejercicios-Resueltos-Sistemas-Digitales


## Ejecutar directamente desde el repositorio

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/rubences/Ejercicios-Resueltos-Sistemas-Digitales?quickstart=1)

**Navegador, sin instalar Java en Windows:** pulsa el botón, crea/reanuda el Codespace y abre **Puertos → 6080 → Abrir en navegador → Conectar**. La aplicación Java arranca automáticamente. El puerto debe permanecer **privado**. Codespaces usa la cuota de tu cuenta; detenlo al terminar.

**VS Code local:** abre la raíz del repositorio y pulsa **F5**. La configuración incluida compila y abre la ventana, sin ejecutar un BAT. Requiere JDK 17+ y Extension Pack for Java. También: **Terminal → Run Task → Laboratorio: ejecutar sin depurador**.

[Guía de arranque, seguridad y solución de problemas](docs/ARRANQUE_DIRECTO.md). No es GitHub Pages ni ejecución dentro del README: en navegador se utiliza un Codespace personal con escritorio remoto.

## Laboratorio visual en Java — Temas 1 y 2

Los materiales originales permanecen en `Tema1/` y `Tema2/`. La aplicación de escritorio está en [`laboratorio-java/`](laboratorio-java/README.md).

```text
cd laboratorio-java
java Build.java package
java -jar dist/laboratorio-visual.jar
```

Requiere JDK 17+ para compilar, o Java 17+ para ejecutar el JAR. También puedes abrir `laboratorio-java/ejecutar.bat` en Windows o `sh laboratorio-java/ejecutar.sh` en Linux/macOS.

Incluye seguidor de línea, grafos, DLS/A*, ping-pong, islas, misioneros y caníbales, árbol binario, mala heurística y Column Jump. Soluciones animadas, datos editables, trazas, comparativa y guía teórica.

**Resultado importante:** el ping-pong original empieza por AB y pide 10/15/17 partidos por jugador: es imposible. La aplicación explica la contradicción y permite una variante BC expresamente identificada.

[Instrucciones completas](laboratorio-java/README.md) · [Resultados verificables](laboratorio-java/docs/RESULTADOS.md) · [Fuentes y correcciones](laboratorio-java/docs/FUENTES.md)
