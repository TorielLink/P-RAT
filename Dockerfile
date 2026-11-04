# ---------- front Angular ----------
FROM node:22-alpine AS front
WORKDIR /front
COPY frontend/package*.json ./
RUN npm install --no-audit --no-fund
COPY frontend/ ./
RUN npm run build
# Toutes les routes sont en RenderMode.Client : Angular produit index.csr.html, renommé en index.html
RUN mkdir /navigateur && cp -r dist/*/browser/. /navigateur/ \
 && if [ -f /navigateur/index.csr.html ]; then mv /navigateur/index.csr.html /navigateur/index.html; fi

# ---------- back avec le front dans ses fichiers statiques ----------
FROM maven:3.9-eclipse-temurin-21 AS back
WORKDIR /back
COPY backend/pom.xml ./
RUN mvn -q -B dependency:go-offline
COPY backend/src ./src
COPY --from=front /navigateur ./src/main/resources/static
# Les tests (dont contextLoads, qui a besoin de MySQL) se lancent en local avec mvn test
RUN mvn -q -B package -DskipTests

# ---------- image finale -> uniquement Java et le jar ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=back /back/target/*.jar app.jar
# Petite machine gratuite : mémoire et démarrage économes
ENV TZ=Europe/Paris
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -XX:TieredStopAtLevel=1"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]