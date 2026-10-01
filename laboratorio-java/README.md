# Laboratorio visual Java — Temas 1 y 2

Aplicación de escritorio Java/Swing, sin librerías externas ni servicios web en ejecución. Los ZIP/PDF originales no se sustituyen.

## Abrir

Windows: abre `ejecutar.bat`. Linux/macOS: `sh ejecutar.sh`. Necesitas JDK 17+ en PATH para compilar.

```text
java Build.java package
java -jar dist/laboratorio-visual.jar
```

El JAR generado incluye las 29 capturas y solo necesita Java 17+ con escritorio gráfico. Los scripts deben ejecutarse desde esta carpeta o abriéndolos directamente. Para compilar conserva `../Tema1/SeguidorLinea.zip`.

## Usar en clase

Selecciona un módulo, revisa los datos y pulsa **Resolver**. Alterna **Camino solución** y **Traza de búsqueda**, usa el deslizador y los controles de reproducción. La tabla muestra g, h, f, profundidad, cortes, descartes y reaperturas. La frontera muestra los próximos 16 nodos en orden de prioridad.

**Comparar estrategias** ejecuta BFS, DFS, DLS, IDS, UCS, Greedy y A* sobre los mismos datos. **Exportar resultado** guarda configuración, camino y traza en TXT UTF-8. Los cambios de controles se aplican al resolver de nuevo. Otra meta utiliza h=0.

Los 12 apartados incluyen seguidor de línea; Grafo 2; grafo de clase; DLS con poda correcta/errónea; A* Oradea; ping-pong; islas; misioneros; árbol binario; mala heurística; Column Jump y guía con las respuestas teóricas de hw1.

La guía integrada contiene todas las formulaciones, las soluciones y los matices de las fuentes. El ZIP entregable añade una versión HTML para lectura fuera de la aplicación.

## Verificar

```text
java Build.java test
java Build.java gui-test
java -cp build/classes lab.UiTests
```

Linux sin escritorio: antepone `xvfb-run -a` a las dos últimas órdenes. `java Build.java clean` elimina solo build y dist.

171 comprobaciones locales del núcleo; apertura de los 12 apartados y pruebas de interacciones. [Resultados](docs/RESULTADOS.md). [Fuentes](docs/FUENTES.md).

## Límites y advertencias

150.000 nodos generados, 15 segundos por resolución, 1.200 muestras de traza. Comparativa: 2,5 segundos y 150.000 nodos por estrategia; profundidad DLS/IDS configurable. Cancelación disponible. Un `LIMIT` o `CUTOFF` no demuestra imposibilidad. La demostración DLS errónea está identificada y no sirve para certificar inexistencia de solución.

El seguidor es una adaptación 2D: el original contiene vídeos e imágenes, no código. Los tres testExample mencionados en hw1 no están presentes y no se inventan resultados. El ping-pong original AB es imposible; BC es una variante explícita.

Árbol: motor limitado al recorte elegido, hasta 1.023 nodos; la vista dibuja hasta 31. Oculta arcos secundarios en grafos densos o consulta la tabla Datos. Ajusta el separador horizontal para ampliar gráfico o tabla.

Código: Problem/GraphProblems/Puzzles (modelos), Search (algoritmos), LineFollower (robot), App/SearchPanel/Scene/LinePanel/Ui (interfaz), Lessons (respuestas), test/lab (pruebas), Build.java (compilación y JAR).

«java no se reconoce»: revisa PATH. «UnsupportedClassVersionError»: Java anterior a 17. «Se necesita un escritorio gráfico»: ejecuta en un escritorio o usa test/Xvfb. No se afirma haber probado Windows/macOS físicos; consulta el CI para las ejecuciones alojadas.
