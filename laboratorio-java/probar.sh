#!/bin/sh
set -eu
cd "$(dirname "$0")"
exec java -Dfile.encoding=UTF-8 Build.java test
