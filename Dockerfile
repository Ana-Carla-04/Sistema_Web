# Dockerfile unico do monorepo SIPA (backend + frontend).
# Cada servico do docker-compose escolhe seu estagio final com "target":
#   - backend  -> API Spring Boot (porta 8080)
#   - frontend -> React buildado servido pelo Nginx (porta 80)

# ==============================================
# BACKEND - build (Maven)
# ==============================================
FROM maven:3.9.9-eclipse-temurin-21 AS backend-build

# Define o diretorio de trabalho dentro do container de compilacao.
WORKDIR /app

# Copia primeiro o pom para aproveitar o cache das dependencias Maven.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o codigo-fonte (agora em backend/) e gera o JAR executavel do Spring Boot.
COPY backend ./backend
RUN mvn -B clean package -DskipTests

# ==============================================
# BACKEND - execucao
# ==============================================
FROM eclipse-temurin:21-jre AS backend

# Define o diretorio de trabalho do container de execucao.
WORKDIR /app

# Copia somente o artefato gerado para a imagem final.
COPY --from=backend-build /app/target/*.jar app.jar

# Documenta a porta HTTP usada pelo Spring Boot.
EXPOSE 8080

# Inicia a aplicacao Spring Boot quando o container for executado.
ENTRYPOINT ["java", "-jar", "app.jar"]

# ==============================================
# FRONTEND - build (Vite)
# ==============================================
FROM node:22-alpine AS frontend-build

WORKDIR /app

# Copia primeiro os manifests para aproveitar o cache do npm.
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm ci || npm install

COPY frontend/ ./

# Com Nginx fazendo proxy reverso, a API fica na mesma origem em /SIPA.
ARG VITE_API_BASE_URL=/SIPA
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

# ==============================================
# FRONTEND - execucao (Nginx)
# ==============================================
FROM nginx:alpine AS frontend

COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=frontend-build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
