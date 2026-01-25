Set-Location "d:\Repos\Nexus_InterBank"

Write-Host "=== Deteniendo containers ===" -ForegroundColor Yellow
docker-compose down

Write-Host "`n=== Limpiando imágenes antiguas ===" -ForegroundColor Yellow
docker image rm nexus-ms-transacciones nexus-web-backend -f 2>$null
docker image prune -f

Write-Host "`n=== Reconstruyendo imágenes ===" -ForegroundColor Yellow
docker-compose build --no-cache

Write-Host "`n=== Iniciando containers ===" -ForegroundColor Yellow
docker-compose up -d

Write-Host "`n=== Status de containers ===" -ForegroundColor Green
docker-compose ps
