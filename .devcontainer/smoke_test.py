"""Integración real: autostart, ventana, reentrada, parada y (opcional) noVNC."""
import importlib.util
import os
from pathlib import Path
import socket
import subprocess
import sys
import time
import urllib.request

HERE = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("launcher", HERE / "start.py")
launcher = importlib.util.module_from_spec(spec)
spec.loader.exec_module(launcher)

def command(mode):
    subprocess.run([sys.executable, str(HERE / "start.py"), mode], check=True, timeout=200)


def web_ready():
    # Solo conectamos al puerto del contenedor; no se accede a sitios externos.
    for path in ("vnc.html", "core/rfb.js"):
        with urllib.request.urlopen("http://127.0.0.1:6080/" + path, timeout=10) as response:
            assert response.status == 200 and len(response.read()) > 100, path
    with socket.create_connection(("127.0.0.1", 6080), timeout=10) as conn:
        conn.sendall(b"GET /websockify HTTP/1.1\r\nHost: localhost:6080\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\nSec-WebSocket-Version: 13\r\nSec-WebSocket-Protocol: binary\r\n\r\n")
        data = b""
        while b"RFB 003." not in data and len(data) < 32768:
            block = conn.recv(4096)
            assert block, "WebSocket cerrado antes de RFB"
            data += block
        assert b"101 Switching Protocols" in data, data[:100]
        assert b"RFB 003." in data, "El puente noVNC no llega al escritorio VNC"
    print("PASS noVNC HTTP + WebSocket + RFB reales")


try:
    command("start")
    pid = launcher.live_pid()
    assert pid is not None and launcher.ready(), "No hay supervisor listo"
    command("start")
    assert launcher.live_pid() == pid, "Se ha duplicado el supervisor"
    command("status")
    assert "Laboratorio visual Java" in launcher.probe(["xwininfo", "-root", "-tree"])
    print("PASS ventana Java y autostart idempotente")
    if "--web" in sys.argv:
        web_ready()
    command("stop")
    assert not launcher.ready() and launcher.live_pid() is None
    command("start")
    assert launcher.ready(), "La ventana no se reabre"
    print("PASS parada y reapertura")
    command("stop")
    failed = subprocess.run([sys.executable, str(HERE / "start.py"), "start"],
                            env={k: v for k, v in os.environ.items() if k != "DISPLAY"},
                            capture_output=True, text=True, timeout=30)
    assert failed.returncode != 0 and "No hay DISPLAY" in failed.stderr
    assert launcher.live_pid() is None
    print("PASS falta de DISPLAY: error explícito, sin instancia huérfana")
finally:
    command("stop")
