#!/bin/bash

echo "🚀 Iniciando entorno de desarrollo con Docker..."

# Verificar si Docker está ejecutándose
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está ejecutándose. Por favor, inicia Docker Desktop."
    exit 1
fi

# Detener contenedores existentes
echo "🛑 Deteniendo contenedores existentes..."
docker-compose down

# Construir e iniciar los servicios
echo "🔨 Construyendo e iniciando servicios..."
docker-compose up --build -d

# Esperar a que MongoDB esté listo
echo "⏳ Esperando a que MongoDB esté listo..."
sleep 10

# Verificar estado de los servicios
echo "📊 Estado de los servicios:"
docker-compose ps

echo ""
echo "✅ Entorno de desarrollo iniciado correctamente!"
echo ""
echo "🌐 Servicios disponibles:"
echo "   • Aplicación Spring WebFlux: http://localhost:8080"
echo "   • Swagger UI: http://localhost:8080/swagger-ui.html"
echo "   • Mongo Express: http://localhost:8081"
echo "   • MongoDB: localhost:27017"
echo ""
echo "🔑 Credenciales por defecto:"
echo "   • Usuario admin: admin"
echo "   • Contraseña: admin123"
echo ""
echo "📝 Comandos útiles:"
echo "   • Ver logs: docker-compose logs -f"
echo "   • Detener: docker-compose down"
echo "   • Reiniciar: docker-compose restart"
echo ""
echo "🎯 Para ver la base de datos en tiempo real, abre Mongo Express en tu navegador!" 