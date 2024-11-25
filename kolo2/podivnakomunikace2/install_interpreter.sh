!/bin/bash
set -euo pipefail

interpreter_dir="$(dirname "$(realpath "$0")")/fikslang-interpreter"
git clone https://github.com/fiks-org/fikslang-interpreter $interpreter_dir
poetry -C "$interpreter_dir" install