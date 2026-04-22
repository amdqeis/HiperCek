#!/usr/bin/env bash
set -e

DOMAIN="yourdomain.com"

log() {
  echo "[deploy] $1"
}

error() {
  echo "[deploy] ERROR: $1" >&2
  exit 1
}

if [[ "${EUID}" -ne 0 ]]; then
  error "Please run this script as root."
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="${SCRIPT_DIR}"
APP_FILE="${APP_DIR}/api.py"
VENV_DIR="${APP_DIR}/.venv"
PIP_BIN="${VENV_DIR}/bin/pip"
GUNICORN_BIN="${VENV_DIR}/bin/gunicorn"
APP_USER="${SUDO_USER:-root}"
APP_GROUP="$(id -gn "${APP_USER}")"
SERVICE_FILE="/etc/systemd/system/fastapi.service"
NGINX_AVAILABLE="/etc/nginx/sites-available/fastapi"
NGINX_ENABLED="/etc/nginx/sites-enabled/fastapi"

if [[ ! -f "${APP_FILE}" ]]; then
  error "api.py not found in ${APP_DIR}."
fi

if [[ "${DOMAIN}" == "yourdomain.com" || -z "${DOMAIN}" ]]; then
  error "Please update DOMAIN at the top of this script before running it."
fi

export DEBIAN_FRONTEND=noninteractive

log "Updating package index"
apt-get update

log "Installing system packages"
apt-get install -y python3-venv python3-pip nginx certbot python3-certbot-nginx

log "Creating virtual environment in ${VENV_DIR}"
if [[ ! -d "${VENV_DIR}" ]]; then
  python3 -m venv "${VENV_DIR}"
fi

log "Activating virtual environment"
# shellcheck disable=SC1091
source "${VENV_DIR}/bin/activate"

log "Upgrading pip tooling"
"${PIP_BIN}" install --upgrade pip setuptools wheel

log "Installing Python dependencies"
"${PIP_BIN}" install --upgrade fastapi uvicorn gunicorn pandas joblib scikit-learn

log "Writing systemd service"
cat > "${SERVICE_FILE}" <<EOF
[Unit]
Description=FastAPI application served by Gunicorn
After=network.target

[Service]
Type=simple
User=${APP_USER}
Group=${APP_GROUP}
WorkingDirectory=${APP_DIR}
Environment="PATH=${VENV_DIR}/bin"
ExecStart=${GUNICORN_BIN} -k uvicorn.workers.UvicornWorker -w 4 -b 127.0.0.1:8000 api:app
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

log "Writing Nginx site configuration"
rm -f /etc/nginx/sites-enabled/default
cat > "${NGINX_AVAILABLE}" <<EOF
server {
    listen 80;
    listen [::]:80;
    server_name ${DOMAIN};

    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_http_version 1.1;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_redirect off;
    }
}
EOF

if [[ ! -L "${NGINX_ENABLED}" ]]; then
  ln -s "${NGINX_AVAILABLE}" "${NGINX_ENABLED}"
fi

log "Validating Nginx configuration"
nginx -t

log "Reloading systemd"
systemctl daemon-reload

log "Enabling and restarting FastAPI service"
systemctl enable fastapi.service
systemctl restart fastapi.service

log "Enabling and restarting Nginx"
systemctl enable nginx
systemctl restart nginx

log "Obtaining or renewing SSL certificate with Certbot"
certbot --nginx --non-interactive --agree-tos --register-unsafely-without-email --redirect -d "${DOMAIN}"

log "Final Nginx restart"
systemctl restart nginx

log "Deployment complete"
