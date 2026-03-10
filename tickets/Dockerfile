# 1. Usamos una imagen de Java súper ligera (Si usas Java 21, cambia el 17 por 21)
FROM eclipse-temurin:17-jdk-alpine

# 2. Creamos una carpeta de trabajo dentro del contenedor
WORKDIR /app

# 3. Copiamos el archivo ejecutable de tu proyecto a la carpeta /app
COPY target/*.jar app.jar

# 4. Le decimos a Docker que tu aplicación escucha en el puerto 8081
EXPOSE 8081

# 5. El comando exacto que ejecutará el contenedor al encenderse
ENTRYPOINT ["java", "-jar", "app.jar"]