# Arranque directo del laboratorio Java

El propietario quiere abrir la aplicación desde el repositorio, tras un bloqueo de Windows al abrir el ejecutable. Se mantiene Java/Swing, los problemas, pruebas y originales.

Se añade un acceso GitHub Codespaces con JDK 21 y escritorio noVNC. Crear/reanudar un Codespace arranca la aplicación; el navegador presenta la misma ventana Java por el puerto 6080. En VS Code local, F5 compila y arranca lab.App con las imágenes del ZIP. No se desactiva ninguna protección de Windows.

El entorno de Codespaces no es GitHub Pages ni una ejecución dentro del README. El usuario inicia el Codespace y autoriza su consumo. No se crea infraestructura facturable durante esta modificación. Mantener 6080 privado (autenticación GitHub); no reenviar VNC 5901 ni compartir públicamente el escritorio. La feature desktop-lite se configura sin contraseña VNC porque la autorización está en el túnel privado, no en una contraseña compartida en Git.

Autostart: arranque acotado, registro de errores, una instancia por repositorio, sin procesos duplicados al reconectar. Mostrar URL usando las variables oficiales del Codespace. Cierre y reapertura sin reiniciar todo el contenedor. Tareas locales usan type=process, sin BAT ni cambio de políticas de PowerShell.

Aceptación: JSON válido, preLaunchTask/cwd/classpath coherentes, arranque y parada reales bajo X, inicio repetido idempotente, 171 comprobaciones Java verdes, contenedor construible y servidor noVNC accesible en CI. No atribuir prueba real en Codespaces si solo se prueba el contenedor equivalente.
