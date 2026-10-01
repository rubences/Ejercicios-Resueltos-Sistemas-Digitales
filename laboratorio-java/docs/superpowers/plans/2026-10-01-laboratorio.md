# Laboratorio visual Java Implementation Plan

**Goal:** resolver el catálogo con ventanas Java y pruebas reproducibles.
**Architecture:** problemas puros → motor → resultados inmutables → SwingWorker → lienzo y tablas.
**Tech Stack:** Java 17+, Swing/Java2D, JavaCompiler, JarOutputStream.
**Spec:** ../specs/2026-10-01-laboratorio-design.md

## Global Constraints

Preservar Tema1/Tema2. Español. Compilación offline. No confundir corte con imposibilidad. Identificar reconstrucciones y fuentes ausentes.

## Review Focus

Meta en el límite; A* con h inconsistente; navegación durante cálculo; entradas inválidas; trazas y resultados truncados; precisión de los enunciados.

## Tareas

- [x] Escribir pruebas antes del motor y observar fallo de compilación por clases ausentes; implementar Problem, Search, GraphProblems.
- [x] Puzzles: contadores, islas, misioneros, árbol y Column; verificar costes, invariantes, transiciones y entradas inválidas.
- [x] LineFollower: sensores, política, memoria, pérdida y reinicio; galería del ZIP original.
- [x] App, SearchPanel, Scene, LinePanel, Lessons; pruebas de ventanas e interacciones bajo Xvfb.
- [x] Build.java y scripts; compilación --release 17; JAR con 29 JPEG.
- [x] Revisión propia de resultados y capturas; suite local 171 PASS y pruebas GUI PASS.
- [ ] Verificar CI alojado, abrir PR sin fusionar main y entregar ZIP final.

## Registro

Inspección original de solo lectura mediante GitHub Actions; primer intento sin pdftotext corregido instalando poppler-utils. No se ejecutó código de los ZIP. Desarrollo local aislado; publicación en rama feat/laboratorio-java-visual.

Decisión: corregir la expectativa inicial de ping-pong. BFS agota el espacio; el argumento de 11 partidos BC entre 21 obliga a empezar por BC y demuestra la incompatibilidad con AB. Mantener el original imposible y separar la variante.

Revisión propia, no independiente. Los registros de pruebas, capturas y JAR se conservan en el entregable y artefactos de CI.
