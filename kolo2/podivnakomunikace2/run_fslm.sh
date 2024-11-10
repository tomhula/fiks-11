#!/bin/bash
set -euo pipefail

# Relative to this script
output_file="out/output.fsl"

script_dir="$(dirname "$(realpath "$0")")"
output_file="$script_dir/$output_file"
output_dir="$(dirname "$output_file")"
mkdir -p "$output_dir"

python compiler.py $1 > $output_file
"$script_dir/run_fsl.sh" $output_file