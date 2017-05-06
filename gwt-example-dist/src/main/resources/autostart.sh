#!/bin/bash

set -Ee
set -o pipefail

if which authbind; then
    exec authbind --deep "$(dirname "$0")/bin/daemon" "$@"
else
    exec "$(dirname "$0")/bin/daemon" "$@"
fi

