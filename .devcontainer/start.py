#!/usr/bin/env python3
"""Autostart Linux/Codespaces: una ventana Java por repositorio, sin librerías extra.

El escritorio/noVNC lo inicia la feature desktop-lite. Este módulo no publica
puertos ni cambia su visibilidad. Solo debe reenviarse 6080 de forma PRIVADA.
"""
from __future__ import annotations
import fcntl
import hashlib
import os
from pathlib import Path
import re
import signal
import subprocess
import sys
import time

SCRIPT = Path(__file__).resolve()
ROOT = SCRIPT.parent.parent
LAB = ROOT / "laboratorio-java"
STATE = Path.home() / ".cache" / ("laboratorio-java-" + hashlib.sha256(str(ROOT).encode()).hexdigest()[:12])


def desktop_url(env: dict[str, str]) -> str:
    name = env.get("CODESPACE_NAME", "")
    base = "http://localhost:6080"
    if name:
        domain = env.get("GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN", "")
        if not re.fullmatch(r"[a-zA-Z0-9-]+", name) or not re.fullmatch(r"[a-zA-Z0-9.-]+", domain):
            raise ValueError("Faltan variables válidas del puerto de Codespaces; abre Puertos → 6080.")
        base = f"https://{name}-6080.{domain}"
    return base + "/vnc.html?autoconnect=true&resize=scale"


def live_pid() -> int | None:
    """No señalizar un PID reciclado o que no sea este supervisor."""
    try:
        pid = int((STATE / "pid").read_text())
        args = Path(f"/proc/{pid}/cmdline").read_bytes().split(b"\0")
        if str(SCRIPT).encode() in args and b"serve" in args:
            return pid
    except (OSError, ValueError):
        pass
    return None


def ready() -> bool:
    pid = live_pid()
    try:
        return pid is not None and (STATE / "ready").read_text() == str(pid)
    except OSError:
        return False


def probe(command: list[str]) -> str:
    result = subprocess.run(command, capture_output=True, text=True, errors="replace", timeout=4, check=False)
    return result.stdout if result.returncode == 0 else ""


def serve() -> int:
    with (STATE / "instance.lock").open("w") as lock:
        try:
            fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except BlockingIOError:
            return 0
        child = None
        (STATE / "ready").unlink(missing_ok=True)
        (STATE / "pid").write_text(str(os.getpid()))
        def halt(_signum, _frame):
            raise SystemExit(0)
        signal.signal(signal.SIGTERM, halt)
        signal.signal(signal.SIGINT, halt)
        try:
            if not os.environ.get("DISPLAY"):
                raise RuntimeError("No hay DISPLAY. Abre el repositorio en Codespaces; en Windows usa F5.")
            for _ in range(60):
                if probe(["xdpyinfo", "-display", os.environ["DISPLAY"]]):
                    break
                time.sleep(0.5)
            else:
                raise RuntimeError("El escritorio no responde: revisa el registro de creación del contenedor.")
            child = subprocess.Popen(["java", "-Dfile.encoding=UTF-8", "Build.java", "package"], cwd=LAB)
            if child.wait(timeout=90) != 0:
                raise RuntimeError("Falló la compilación. Consulta los errores anteriores.")
            child = subprocess.Popen(["java", "-Xmx768m", "-Dfile.encoding=UTF-8", "-jar", "dist/laboratorio-visual.jar"], cwd=LAB)
            for _ in range(60):
                if child.poll() is not None:
                    raise RuntimeError(f"Java terminó antes de abrir la ventana (código {child.returncode}).")
                if "Laboratorio visual Java" in probe(["xwininfo", "-root", "-tree"]):
                    break
                time.sleep(0.5)
            else:
                raise RuntimeError("Java arrancó, pero no se detectó la ventana del laboratorio.")
            (STATE / "ready").write_text(str(os.getpid()))
            print("Ventana Java lista.", flush=True)
            return child.wait()
        finally:
            (STATE / "ready").unlink(missing_ok=True)
            if child is not None and child.poll() is None:
                child.terminate()
                try:
                    child.wait(timeout=8)
                except subprocess.TimeoutExpired:
                    child.kill()
                    child.wait()
            (STATE / "pid").unlink(missing_ok=True)


def start() -> int:
    with (STATE / "start.lock").open("w") as lock:
        fcntl.flock(lock, fcntl.LOCK_EX)
        process = None
        if not live_pid():
            with (STATE / "application.log").open("w") as log:
                process = subprocess.Popen([sys.executable, str(SCRIPT), "serve"], stdin=subprocess.DEVNULL,
                                           stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
        for _ in range(360):
            if ready():
                print("Laboratorio abierto. Puerto 6080: mantener PRIVADO.")
                print(desktop_url(dict(os.environ)))
                return 0
            if process is not None and process.poll() is not None:
                break
            if process is None and not live_pid():
                break
            time.sleep(0.5)
        if process is not None and process.poll() is None:
            process.terminate()
            process.wait(timeout=15)
        print(f"No se completó el arranque. Registro: {STATE / 'application.log'}", file=sys.stderr)
        if (STATE / "application.log").exists():
            print((STATE / "application.log").read_text()[-7000:], file=sys.stderr)
        return 1


def stop() -> int:
    pid = live_pid()
    if pid is not None:
        os.kill(pid, signal.SIGTERM)
        for _ in range(100):
            if live_pid() is None:
                break
            time.sleep(0.1)
        else:
            raise RuntimeError("No se cerró la instancia. Revisa su registro antes de reiniciar.")
    print("Ventana cerrada. El Codespace sigue consumiendo recursos hasta detenerlo.")
    return 0


def main() -> int:
    STATE.mkdir(parents=True, exist_ok=True, mode=0o700)
    command = sys.argv[1] if len(sys.argv) > 1 else "start"
    if command == "serve":
        return serve()
    if command == "start":
        return start()
    if command == "stop":
        return stop()
    if command == "url":
        print(desktop_url(dict(os.environ)))
        return 0
    if command == "status":
        print("READY" if ready() else "STOPPED")
        return 0 if ready() else 1
    raise ValueError("Uso: python3 .devcontainer/start.py [start|stop|status|url]")


if __name__ == "__main__":
    try:
        sys.exit(main())
    except (OSError, ValueError, RuntimeError, subprocess.SubprocessError) as exc:
        print(str(exc), file=sys.stderr)
        sys.exit(1)
