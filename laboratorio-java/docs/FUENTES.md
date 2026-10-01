# Procedencia y correcciones

Repositorio rubences/Ejercicios-Resueltos-Sistemas-Digitales, main inspeccionada el 1 de octubre de 2026. Se conservan los siete ficheros originales de los temas, con SHA-256 en `originales-sha256.txt`.

| Material | Implementación | Tratamiento |
|---|---|---|
| SeguidorLinea.zip | LineFollower, LinePanel | Dos AVI y 29 JPEG. Sin código: adaptación didáctica, galería literal. |
| Grafo2.zip | GraphProblems.grafo2 | Seis PDF, arcos/h transcritos. Sucesores numéricos y desempate FIFO explícitos. |
| grafoclase.pdf | GraphProblems.clase | Diagrama rotado para leer flechas, costes y h. |
| ExampleDLSProblemPruning.pdf | Search, DLS_ROMANIA | Zerind primero; poda booleana frente a reapertura por profundidad. |
| Pinpong.pdf | Puzzles.pingPong | Pareja y contadores; original imposible, variante BC separada. |
| espacioestadoIslas.pdf | Puzzles.islands | Se filtran transiciones inseguras del dibujo. |
| hw1soln.pdf | Puzzles, GraphProblems, Lessons | 15-381 Spring 2007: seis cuestiones y subapartados, teoría y simuladores. |

La lista de misioneros repite un estado: se verifica un conjunto de 16 distintos. La salida inicial del barquero solo es insegura. Algunas h sugeridas para Column Jump valen 1 en la meta y no son admisibles tal como están escritas. El contraejemplo de Greedy se hace preciso con una ruta corta de dos aristas y h(meta)=0.

No están testExample1.txt, testExample2.txt ni testExample3.txt. Sus longitudes 11/8/9 son afirmaciones del PDF, no pruebas ejecutadas por este proyecto. Se resuelve el tablero de cuatro filas visible y se añaden entradas límite propias para las pruebas.

Mapa de Rumanía: ejemplo académico utilizado en las hojas; referencia de los autores https://github.com/aimacode/aima-python . No es cartografía actual para viajar.

SwingWorker Java SE 17: https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/swing/SwingWorker.html . Cálculo fuera del EDT; actualización de componentes en el EDT.

La publicación del repositorio no implica una licencia abierta de todos los materiales. Se conservan atribuciones y derechos de los titulares originales.
