#!/bin/bash
interpreter_dir="$(dirname "$(realpath "$0")")/fikslang-interpreter"
poetry -C "$interpreter_dir" run fikslang $1