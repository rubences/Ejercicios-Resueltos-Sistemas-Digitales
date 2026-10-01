# Resultados reproducidos localmente

1 de octubre de 2026. JDK 21.0.11, fuentes con `--release 17`. Los tiempos dependen del equipo y no constituyen un benchmark estadístico.

| Problema | Algoritmo | Resultado | Acciones | Coste | Expandidos | Generados |
|---|---|---|---:|---:|---:|---:|
| Grafo 2 | UCS | FOUND | 4 | 16 | 8 | 20 |
| Grafo de clase | A* | FOUND | 6 | 23 | 9 | 25 |
| DLS Arad correcto | DLS(4) | FOUND | 3 | 450 | 6 | 18 |
| Oradea | A* | FOUND | 4 | 429 | 5 | 15 |
| Ping-pong AB original | BFS | NO_SOLUTION | — | — | 562 | 934 |
| Ping-pong BC variante | BFS | FOUND | 20 | 20 | 624 | 1053 |
| Islas | BFS | FOUND | 7 | 7 | 9 | 20 |
| Misioneros | BFS | FOUND | 11 | 11 | 14 | 31 |
| Árbol binario | BFS | FOUND | 3 | 3 | 12 | 15 |
| Mala heurística | Greedy | FOUND | 8 | 8 | 8 | 10 |
| Column Jump | A* | FOUND | 8 | 8 | 617 | 1691 |

**171 comprobaciones del núcleo: PASS.** Incluyen invariantes, transiciones, rutas, costes, cortes, reaperturas, cancelación y 10.000 pasos normales del robot sin perder detección.

Pruebas gráficas locales bajo Xvfb: apertura y renderizado de 12 apartados, 10 resoluciones desde SwingWorker y navegación durante cálculo. Interacciones adicionales: 29 JPEG originales, variante BC, DLS erróneo, comparativa de siete algoritmos y botón Cancelar.

No se han probado equipos Windows/macOS físicos. La revisión en esta sesión fue propia, no de un revisor independiente. No hay validación del motor físico del robot original ni resultados inventados para los testExample ausentes. El CI adjunta nuevos registros y capturas correspondientes a cada commit probado.
