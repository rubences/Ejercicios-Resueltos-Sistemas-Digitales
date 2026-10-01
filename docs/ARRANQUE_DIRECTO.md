# Abrir el laboratorio desde el repositorio

## En el navegador: sin instalar Java en Windows

[**ABRIR LABORATORIO EN GITHUB CODESPACES**](https://codespaces.new/rubences/Ejercicios-Resueltos-Sistemas-Digitales?quickstart=1)

1. Inicia sesión en GitHub y pulsa el botón. Elige **Create codespace** o **Resume this codespace**. Se utiliza la rama por defecto, `main`.
2. La configuración del repositorio prepara Java 21, un escritorio y el laboratorio. **No hay que escribir comandos ni ejecutar BAT/JAR en Windows.**
3. Abre la pestaña **Ports / Puertos** y el icono del globo del puerto **6080 · Abrir laboratorio Java**. El editor también intenta abrirlo automáticamente. Cuando aparezca noVNC, pulsa **Connect / Conectar**. No pide contraseña VNC; el acceso lo controla tu sesión GitHub en un puerto **privado**.

Ves y manejas la misma ventana Java, con todos los ejercicios; no es una simulación reescrita en JavaScript. NoVNC transmite el escritorio del contenedor al navegador.

El enlace que imprime el arranque termina en `/vnc.html?autoconnect=true&resize=scale` y conecta directamente. Abre siempre la dirección reenviada por Codespaces, no `localhost:6080` en tu ordenador cuando estés en VS Code web.

**Importante:** GitHub no ejecuta Swing dentro de la página de ficheros ni del README. El botón crea/reanuda un entorno Codespaces personal. El editor `github.dev` (tecla `.` en GitHub) no sustituye a Codespaces porque no incluye un entorno de ejecución.

### Seguridad y consumo

Mantén **6080 privado**. No lo cambies a público ni compartas el escritorio. Nunca reenvíes el puerto 5901: es el protocolo VNC interno y escucha en localhost. No hay contraseñas comunes ni tokens añadidos al repositorio. No se desactiva Defender, SmartScreen ni la confianza de VS Code.

Codespaces requiere cuenta, permiso y cuota disponible; puede consumir cómputo y almacenamiento según tu plan. Crear el repositorio o pulsar un enlace no autoriza aquí ningún cambio de facturación. No se han creado Codespaces ni modificado presupuestos para preparar esta configuración.

Al terminar, usa **Codespaces: Stop Current Codespace**. Cerrar solo la pestaña o la ventana Java no detiene el Codespace. El almacenamiento permanece mientras el Codespace exista; elimina los que ya no necesites.

### Si ya tenías un Codespace

Actualiza `main` desde **Source Control → Pull** y ejecuta **Codespaces: Rebuild Container** desde la paleta. Una instancia antigua no obtiene un contenedor nuevo solo por existir cambios en GitHub.

Si cierras la ventana Java, usa **Terminal → Run Task → Codespaces: reabrir laboratorio**. No duplica una instancia que ya está abierta. **Codespaces: cerrar laboratorio** cierra la aplicación, pero no detiene el Codespace.

### Diagnóstico del arranque

El lanzador muestra un error si falla la compilación o no aparece la ventana. Los registros están en `~/.cache/laboratorio-java-<identificador>/application.log`. Comandos opcionales para diagnóstico, no necesarios para el arranque normal:

```bash
python3 .devcontainer/start.py status
python3 .devcontainer/start.py url
python3 .devcontainer/start.py stop
python3 .devcontainer/start.py start
```

## En Visual Studio Code de Windows: F5

Abre la **raíz completa del repositorio**, no solamente `src` ni `App.java`. Instala el JDK 17+ y la extensión recomendada **Extension Pack for Java** si no los tienes. Después pulsa **F5**, o **Run → Start Debugging → Laboratorio Java — abrir ventana (F5)**.

El repositorio ya incluye `launch.json`, `tasks.json`, `settings.json` y `extensions.json`. F5 compila, prepara las 29 imágenes y abre `lab.App`. Las tareas utilizan `java` directamente como proceso, sin scripts BAT ni cambios de la política de PowerShell. El compilador del editor tiene una carpeta de salida distinta para no borrar las imágenes.

También está **Terminal → Run Task → Laboratorio: ejecutar sin depurador**. Esta tarea solo necesita `java` en PATH; no requiere usar el depurador. Usa **Laboratorio: pruebas** para ejecutar las comprobaciones.

En un equipo gestionado, respeta sus políticas: el IDE no elimina una detección de malware ni autoriza saltarse restricciones. El modo Codespaces evita ejecutar el laboratorio en el Windows local, pero también debe estar permitido por tu organización.

## Alcance de la verificación

Los ocho tests de configuración/utilidades y las pruebas de arranque reales se ejecutan con `.devcontainer/test_launch.py` y `.devcontainer/smoke_test.py`. El CI construye la configuración de Dev Containers y comprueba la ventana, reinicio, idempotencia, HTTP de noVNC y el puente WebSocket→VNC. También conserva las 171 comprobaciones originales de Java.

Construir/probar ese contenedor en CI no equivale a haber creado un Codespace de tu cuenta ni verifica su cuota, permisos o restricciones de navegador. Las ejecuciones y su estado están en **Actions → Direct repository launch**. F5 está configurado según el depurador oficial; no se afirma una prueba en un Windows físico del usuario.

## Referencias oficiales

- [Creación y reanudación rápida de Codespaces](https://docs.github.com/en/codespaces/setting-up-your-project-for-codespaces/setting-up-your-repository/facilitating-quick-creation-and-resumption-of-codespaces)
- [Seguridad y puertos privados](https://docs.github.com/en/codespaces/reference/security-in-github-codespaces)
- [Uso incluido y consumo de Codespaces](https://docs.github.com/en/codespaces/troubleshooting/troubleshooting-included-usage)
- [Depuración Java y F5 en VS Code](https://code.visualstudio.com/docs/java/java-debugging)
- [Escritorio de Dev Containers](https://github.com/devcontainers/features/tree/main/src/desktop-lite)
