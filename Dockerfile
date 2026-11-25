# ==============================================
# 1. BACKEND - Build (Maven + Cache Mount .m2)
# ==============================================
FROM maven:3.9.9-eclipse-temurin-21 AS backend-build

WORKDIR /app


COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B dependency:go-offline


COPY backend ./backend
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B package -DskipTests

# ==============================================
# 2. BACKEND - Runtime (JRE 21 Non-root + Flags JVM Container)
# ==============================================
FROM eclipse-temurin:21-jre AS backend

WORKDIR /app

# Cria grupo e usuario de sistema sem privilegios de root (OWASP A02)
RUN groupadd --system sipa && useradd --system --gid sipa --no-create-home sipa

COPY --from=backend-build --chown=sipa:sipa /app/target/*.jar app.jar

USER sipa

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:TieredStopAtLevel=1"

ENTRYPOINT ["java", "-jar", "app.jar"]

# ==============================================
# 3. FRONTEND - Build (Node 22 + Cache Mount .npm)
# ==============================================
FROM node:22-alpine AS frontend-build

WORKDIR /app

# Copia apenas os manifests para cachear a camada de node_modules
COPY frontend/package.json frontend/package-lock.json ./
RUN --mount=type=cache,target=/root/.npm \
    npm ci --prefer-offline --no-audit

# Copia o codigo-fonte do frontend e gera o bundle estatico do Vite
COPY frontend/ ./

ARG VITE_API_BASE_URL=/SIPA
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

# ==============================================
# 4. FRONTEND - Runtime (Nginx 1.27 Alpine)
# ==============================================
FROM nginx:1.27-alpine AS frontend

COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=frontend-build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
