#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p data
if [ ! -f data/.credentials.env ]; then
  python3 - <<'PY'
from pathlib import Path
import secrets,os
p=Path('data/.credentials.env')
p.write_text('export ROTA_CITIZEN_PASSWORD='+secrets.token_hex(12)+'\nexport ROTA_ADMIN_PASSWORD='+secrets.token_hex(12)+'\n')
os.chmod(p,0o600)
PY
  printf 'Credenciais locais geradas em data/.credentials.env. Não publique esse arquivo.\n'
fi
set -a
source data/.credentials.env
set +a
if [ ! -f target/rota-livre-0.1.0.jar ]; then
  mvn -B package
fi
exec java -Xms32m -Xmx256m -jar target/rota-livre-0.1.0.jar
