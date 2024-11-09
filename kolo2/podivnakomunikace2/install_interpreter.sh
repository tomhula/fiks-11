!/bin/bash
set -euo pipefail

git clone https://github.com/fiks-org/fikslang-interpreter $(dirname "$(realpath "$0")")/fikslang-interpreter
interpreter_dir="$(dirname "$(realpath "$0")")/fikslang-interpreter"
poetry -C "$interpreter_dir" install