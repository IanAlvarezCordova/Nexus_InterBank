# 🏦 NEXUS Bank - Guía Maestra de Despliegue y Entrega

¡Todo el sistema está operativo! Aquí tienes las consideraciones finales para enviar el código a tu compañero y para el despliegue final en la nube.

---

## � 1. Instrucciones para Zipear y Enviar
Antes de comprimir la carpeta para tu compañero, asegúrate de limpiar los archivos temporales para que el archivo sea liviano:

1. **Borrar carpetas `target`**: Ejecuta este comando en la raíz para borrar los binarios de Java que pesan mucho:
   ```bash
   # En Windows (PowerShell)
   Get-ChildItem -Path . -Include target, node_modules -Recurse | Remove-Item -Recurse -Force
   ```
2. **Excluir Docker Volumes**: No incluyas las carpetas que Docker crea para persistir datos (si las hay fuera de los contenedores).
3. **Zipear**: Comprime la carpeta raíz `Microservicios_Nexus-main`.

---

## 🚀 2. Consideraciones para el Despliegue en la Nube (GCP/AWS)
Cuando subas el proyecto a un servidor real, ten en cuenta estos puntos:

### ⚙️ Variables de Entorno
En el servidor de producción, ya NO usarás `ngrok`. Deberás usar la IP pública o el dominio del servidor:

1. **En `docker-compose.yml`**:
   - Asegúrate de que `APP_SWITCH_URL` apunte a la IP final del Switch (`34.44.123.236:9080`).
   - El código banco debe ser exactamente `NEXUS`.

2. **Registro en el Switch**:
   - Una vez desplegado, debes entrar al servidor del Switch y actualizar el `Endpoint` del banco NEXUS con la IP de tu servidor real:
   ```sql
   UPDATE "Bancos" SET "Endpoint" = 'http://TU-IP-SERVIDOR:9080/api/transacciones/webhook' WHERE "Codigo" = 'NEXUS';
   ```

### 🔓 Puertos y Firewall
El servidor de la nube debe tener abiertos los siguientes puertos en su Firewall:
- **8080**: Frontend Banca Web
- **81**: Frontend Ventanilla
- **9080**: API Gateway (Muy importante para el webhook de entrada)

---

## 🛠️ 3. Resumen de Fixes Aplicados (Ya en el código)
Le puedes decir a tu compañero que los siguientes errores críticos ya fueron resueltos:

1. **Webhook de Entrada**: Corregido el formato de respuesta a `status: ACK` e `instructionId` para que el Switch de ARCBANK pueda enviarte dinero.
2. **Transferencias Internas**: Corregida la lógica para que las transferencias entre cuentas propias de NEXUS no se intenten enviar al Switch.
3. **Switch IP**: Configurada la IP real del ecosistema (`34.44.123.236`).
4. **Cuentas Duplicadas**: Eliminado el conflicto de controladores en el CBS que impedía ver las cuentas.

---

## 🏃 4. Cómo Correr el Proyecto (para tu compañero)

1. **Descargar e Instalar Docker Desktop**.
2. **Abrir terminal** en la carpeta zipeada.
3. **Ejecutar**:
   ```bash
   docker-compose up -d --build
   ```
4. **Verificar**: Ir a `http://localhost:8080` y probar:
   - Login: `stephi` / `kuki123`
   - Realizar una transferencia interna.
   - Realizar una transferencia externa a ARCBANK (usando ngrok si está en local).

---

*Documentación finalizada con éxito.*
*Fecha: 22 de Diciembre, 2024*
