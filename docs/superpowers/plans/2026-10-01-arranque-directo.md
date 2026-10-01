# Arranque directo Implementation Plan

> Ejecutar con superpowers:executing-plans, sin subagentes ficticios.

**Goal:** abrir la aplicación Java desde GitHub/Codespaces o F5 en VS Code.
**Architecture:** conservar el motor Java; configurar el JDK/escritorio remoto y un supervisor de autostart; F5 utiliza el compilador existente.
**Tech Stack:** Java 21 compatible con release 17, Swing, Dev Containers, desktop-lite, Python estándar.
**Spec:** ../specs/2026-10-01-arranque-directo.md

## Global Constraints
No modificar los problemas ni originales. No desactivar protección local. Puerto 6080 privado; 5901 no reenviado. No crear Codespaces facturables. Abrir la raíz del repositorio.

## Review Focus
Rutas con espacios; dos inicios simultáneos; fallo de compilación; escritorio aún no preparado; cierre de la ventana y reinicio.

## Tareas
- [x] Escribir test_launch.py: contratos de devcontainer/F5, URL y ficheros. Observar fallo con configuración ausente.
- [x] Añadir .vscode y .devcontainer/start.py: compilar con Build.java package, lanzar lab.App, detectar ventana, lock de instancia, stop/status/url.
- [x] Probar autostart real, idempotencia, reapertura y falta de DISPLAY; 8 tests y 171 comprobaciones Java.
- [ ] Verificar CI del contenedor con devcontainer CLI y prueba noVNC antes de integrar.
- [x] README con botón Codespaces y F5, límites/coste y guía. Mantener ficheros ajenos.

## Registro
Base remota inspeccionada: cd15c026c70de1d7e85a849ec068809867926549. Copia de trabajo aislada extraída de artefacto CI verificado; clonación directa bloqueada por DNS local. Baseline Java: 171 PASS.

Se observó un fallo de codificación en xwininfo al reabrir: algunos WM_NAME usan bytes no UTF-8. Prueba de regresión primero (fallo reproducido), después decodificación tolerante solo de la sonda X11. 8 tests PASS y arranque/cierre/reapertura reales PASS. No se cambia el texto ni el algoritmo Java. Revisión propia, sin revisor independiente.
