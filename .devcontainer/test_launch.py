"""Contratos de configuración y utilidades del lanzador; sin dependencias externas."""
import importlib.util
import json
import sys
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parent.parent


class ConfigTests(unittest.TestCase):
    def read(self, name):
        path = ROOT / name
        self.assertTrue(path.is_file(), f"Falta configuración: {name}")
        return json.loads(path.read_text(encoding="utf-8"))

    def test_devcontainer_starts_java_automatically(self):
        config = self.read(".devcontainer/devcontainer.json")
        self.assertEqual(config["build"]["dockerfile"], "Dockerfile")
        self.assertIn("java:1-21-bookworm@sha256:", (ROOT / ".devcontainer/Dockerfile").read_text())
        self.assertEqual(config["postStartCommand"], ["python3", ".devcontainer/start.py", "start"])
        self.assertEqual(config["forwardPorts"], [6080])
        self.assertEqual(config["portsAttributes"]["6080"]["onAutoForward"], "openBrowser")
        self.assertNotIn("appPort", config)
        self.assertNotIn("privileged", config)

    def test_f5_builds_with_correct_resources_and_paths(self):
        launch = self.read(".vscode/launch.json")["configurations"][0]
        tasks = self.read(".vscode/tasks.json")["tasks"]
        task = next(t for t in tasks if t["label"] == launch["preLaunchTask"])
        self.assertEqual(launch["mainClass"], "lab.App")
        self.assertEqual(launch["classPaths"], ["${workspaceFolder}/laboratorio-java/build/classes"])
        self.assertEqual(task["options"]["cwd"], "${workspaceFolder}/laboratorio-java")
        self.assertEqual(task["type"], "process")
        self.assertEqual(task["args"], ["-Dfile.encoding=UTF-8", "Build.java", "package"])
        self.assertTrue(all(t.get("runOptions", {}).get("runOn") != "folderOpen" for t in tasks))

    def test_extension_and_distinct_editor_output(self):
        extensions = self.read(".vscode/extensions.json")
        settings = self.read(".vscode/settings.json")
        self.assertIn("vscjava.vscode-java-pack", extensions["recommendations"])
        self.assertEqual(settings["java.project.sourcePaths"], ["laboratorio-java/src", "laboratorio-java/test"])
        self.assertNotEqual(settings["java.project.outputPath"], "laboratorio-java/build/classes")


class LauncherTests(unittest.TestCase):
    def setUp(self):
        path = ROOT / ".devcontainer/start.py"
        self.assertTrue(path.is_file(), "Falta lanzador de autostart")
        spec = importlib.util.spec_from_file_location("launcher", path)
        self.module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(self.module)

    def test_probe_accepts_legacy_x11_window_labels(self):
        output = self.module.probe([sys.executable, "-c", "import sys; sys.stdout.buffer.write(b'Laboratorio visual Java \\xB7')"])
        self.assertIn("Laboratorio visual Java", output)

    def test_local_url(self):
        self.assertEqual(self.module.desktop_url({}), "http://localhost:6080/vnc.html?autoconnect=true&resize=scale")

    def test_codespaces_url_uses_forwarding_domain(self):
        env = {"CODESPACE_NAME": "my-lab-123", "GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN": "app.github.dev"}
        self.assertEqual(self.module.desktop_url(env), "https://my-lab-123-6080.app.github.dev/vnc.html?autoconnect=true&resize=scale")

    def test_url_rejects_malformed_environment(self):
        with self.assertRaises(ValueError):
            self.module.desktop_url({"CODESPACE_NAME": "bad/path", "GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN": "app.github.dev"})

    def test_missing_domain_does_not_invent_host(self):
        with self.assertRaises(ValueError):
            self.module.desktop_url({"CODESPACE_NAME": "my-lab"})


if __name__ == "__main__":
    unittest.main(verbosity=2)
