# Script de PowerShell para iniciar el entorno de desarrollo
Write-Host "🚀 Iniciando entorno de desarrollo con Docker..." -ForegroundColor Green

# Verificar si Docker está ejecutándose
try {
    docker info | Out-Null
} catch {
    Write-Host "❌ Docker no está ejecutándose. Por favor, inicia Docker Desktop." -ForegroundColor Red
    exit 1
}

# Detener contenedores existentes
Write-Host "🛑 Deteniendo contenedores existentes..." -ForegroundColor Yellow
docker-compose down

# Construir e iniciar los servicios
Write-Host "🔨 Construyendo e iniciando servicios..." -ForegroundColor Yellow
docker-compose up --build -d

# Esperar a que MongoDB esté listo
Write-Host "⏳ Esperando a que MongoDB esté listo..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Verificar estado de los servicios
Write-Host "📊 Estado de los servicios:" -ForegroundColor Cyan
docker-compose ps

Write-Host ""
Write-Host "✅ Entorno de desarrollo iniciado correctamente!" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Servicios disponibles:" -ForegroundColor Cyan
Write-Host "   • Aplicación Spring WebFlux: http://localhost:8080" -ForegroundColor White
Write-Host "   • Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "   • Mongo Express: http://localhost:8081" -ForegroundColor White
Write-Host "   • MongoDB: localhost:27017" -ForegroundColor White
Write-Host ""
Write-Host "🔑 Credenciales por defecto:" -ForegroundColor Cyan
Write-Host "   • Usuario admin: admin" -ForegroundColor White
Write-Host "   • Contraseña: admin123" -ForegroundColor White
Write-Host ""
Write-Host "📝 Comandos útiles:" -ForegroundColor Cyan
Write-Host "   • Ver logs: docker-compose logs -f" -ForegroundColor White
Write-Host "   • Detener: docker-compose down" -ForegroundColor White
Write-Host "   • Reiniciar: docker-compose restart" -ForegroundColor White
Write-Host ""
Write-Host "🎯 Para ver la base de datos en tiempo real, abre Mongo Express en tu navegador!" -ForegroundColor Green 