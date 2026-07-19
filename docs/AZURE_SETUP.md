# Guía de Despliegue en Azure for Students

> Tiempo estimado: 30–45 minutos la primera vez.  
> Costo: **$0** con créditos de Azure for Students.

---

## Prerequisitos

- Cuenta de **Azure for Students** activa (`portal.azure.com`)
- **Azure CLI** instalado en tu PC → [instalar](https://docs.microsoft.com/cli/azure/install-azure-cli)
- Acceso al repositorio de GitHub

---

## Paso 1 — Crear la VM en Azure

Abre una terminal y ejecuta:

```bash
# Login con tu cuenta Azure for Students
az login

# Crear grupo de recursos (solo una vez)
az group create \
  --name buganvilla-rg \
  --location chilecentral

# Crear VM Ubuntu 22.04 — Standard_B2s (2 vCPU / 4 GB RAM)
# --generate-ssh-keys crea ~/.ssh/id_rsa y ~/.ssh/id_rsa.pub automáticamente
az vm create \
  --resource-group buganvilla-rg \
  --name buganvilla-vm \
  --image Ubuntu2204 \
  --size Standard_B2s_v2 \
  --admin-username azureuser \
  --generate-ssh-keys \
  --public-ip-sku Standard

# Abrir puertos necesarios
az vm open-port --resource-group buganvilla-rg --name buganvilla-vm --port 22  --priority 100
az vm open-port --resource-group buganvilla-rg --name buganvilla-vm --port 80  --priority 200
az vm open-port --resource-group buganvilla-rg --name buganvilla-vm --port 443 --priority 300

# Asignar un DNS label (dominio GRATUITO de Azure)
# Cambiar "buganvillatours" por el nombre que quieras (debe ser único)
az network public-ip update \
  --resource-group buganvilla-rg \
  --name buganvilla-vmPublicIP \
  --dns-name buganvillatours

# Ver la IP y dominio asignado
az vm show \
  --resource-group buganvilla-rg \
  --name buganvilla-vm \
  --show-details \
  --query "[publicIps, fqdns]" \
  -o tsv
```

El dominio quedará como: `buganvillatours.chilecentral.cloudapp.azure.com`

> **Anota el dominio** — lo necesitarás para los siguientes pasos.

---

## Paso 2 — Instalar Docker en la VM

```bash
# Conectarse a la VM por SSH
ssh azureuser@buganvillatours.chilecentral.cloudapp.azure.com

# Instalar Docker (instalación oficial en una línea)
curl -fsSL https://get.docker.com | sh

# Agregar el usuario al grupo docker (evita usar sudo)
sudo usermod -aG docker azureuser

# Aplicar el nuevo grupo sin reconectarse
newgrp docker

# Verificar que Docker funciona
docker run hello-world
```

---

## Paso 3 — Clonar el repositorio en la VM

```bash
# Todavía conectado por SSH a la VM:
git clone https://github.com/mMusashIi/DesarrolloWebIntegrado.git /opt/buganvilla
cd /opt/buganvilla

# Verificar que el Caddyfile y docker-compose.yml están presentes
ls -la
```

---

## Paso 4 — Configurar GitHub Secrets

Ve a tu repositorio en GitHub:  
**Settings → Secrets and variables → Actions → New repository secret**

Crea **cada uno** de los siguientes secretos:

| Nombre del Secret | Valor |
|---|---|
| `AZURE_VM_HOST` | `buganvillatours.chilecentral.cloudapp.azure.com` |
| `AZURE_VM_USER` | `azureuser` |
| `AZURE_VM_SSH_KEY` | Contenido de `~/.ssh/id_rsa` (clave privada generada en el paso 1) |
| `DEPLOY_PATH` | `/opt/buganvilla` |
| `CADDY_DOMAIN` | `buganvillatours.chilecentral.cloudapp.azure.com` |
| `DB_PASSWORD` | Contraseña fuerte (ej: `BuganvillaTours_Prod2024!`) |
| `JWT_SECRET` | Cadena aleatoria larga (ej: correr `openssl rand -hex 64` en tu PC) |
| `INTERNAL_SERVICE_TOKEN` | Cadena aleatoria (ej: `openssl rand -hex 32`) |
| `MP_ACCESS_TOKEN` | Tu token de MercadoPago sandbox |
| `MP_PUBLIC_KEY` | Tu public key de MercadoPago sandbox |
| `WHATSAPP_ACCESS_TOKEN` | Token de WhatsApp Cloud API |
| `WHATSAPP_PHONE_NUMBER_ID` | ID del número de WhatsApp |
| `WHATSAPP_BUSINESS_ACCOUNT_ID` | ID de la cuenta de negocio |
| `APIS_NET_TOKEN` | Token de apis.net.pe |

### Cómo obtener la clave SSH

En tu PC (Windows PowerShell o Git Bash):
```bash
# Muestra el contenido de la clave privada
cat ~/.ssh/id_rsa
```
Copia **todo el contenido** (incluyendo `-----BEGIN OPENSSH PRIVATE KEY-----` y `-----END OPENSSH PRIVATE KEY-----`).

---

## Paso 5 — Primer deploy

El pipeline se activa automáticamente en cada `push` a `main`.

Para hacer el primer deploy:
```bash
# En tu PC, en la carpeta del proyecto
git add .
git commit -m "feat: add Caddy HTTPS + Azure deploy pipeline"
git push origin main
```

Ve a **GitHub → Actions** para ver el pipeline en ejecución.

---

## Paso 6 — Verificar que todo funciona

Una vez que el pipeline termine (~10 min la primera vez, el build de Docker lleva tiempo):

```bash
# Verificar HTTPS (debe responder 200)
curl https://buganvillatours.chilecentral.cloudapp.azure.com/api/auth/check

# Ver estado de los contenedores
ssh azureuser@buganvillatours.chilecentral.cloudapp.azure.com
cd /opt/buganvilla
docker compose ps
```

Abre en el navegador: `https://buganvillatours.chilecentral.cloudapp.azure.com`

> El candado verde de HTTPS puede tardar 1–2 minutos en aparecer la primera vez
> (Caddy necesita solicitar el certificado a Let's Encrypt).

---

## Comandos útiles post-deploy

```bash
# Ver logs de un servicio
docker compose logs --tail=50 pago-service
docker compose logs --tail=50 caddy
docker compose logs -f api-gateway

# Reiniciar un servicio sin reconstruir
docker compose restart pago-service

# Ver todos los contenedores
docker compose ps

# Actualizar manualmente (el pipeline lo hace automático)
git pull origin main
docker compose up --build -d
```

---

## Solución de problemas

### Caddy no consigue el certificado SSL
- Verificar que el puerto 80 y 443 están abiertos en Azure (Paso 1)
- Verificar que el DNS resuelve correctamente:  
  `nslookup buganvillatours.eastus.cloudapp.azure.com`
- Revisar logs: `docker compose logs caddy`

### Los contenedores de Java no inician
- SQL Server puede tardar hasta 2 minutos en estar listo
- Revisar: `docker compose logs sqlserver`

### Error de SSH en el pipeline
- Verificar que el secret `AZURE_VM_SSH_KEY` contiene la clave privada completa
- Verificar que el usuario puede conectarse: `ssh azureuser@[IP]`
