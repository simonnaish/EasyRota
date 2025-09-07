# ---------- Build stage ----------
FROM eclipse-temurin:24-jdk AS build
LABEL author="littlebizsolutions"
WORKDIR /app

# Copy Maven wrapper & pom first for better layer caching
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Pre-download dependencies (better cache)
RUN ./mvnw -q -B -e -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN ./mvnw -q -B -DskipTests clean package

# ---------- Runtime stage ----------
FROM eclipse-temurin:24-jre
WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# Optional: JVM tuning for containers
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
