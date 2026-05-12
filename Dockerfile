FROM eclipse-temurin:23-jdk AS builder

WORKDIR /app

COPY . .

RUN mkdir -p out \
    && find src/main/java -name "*.java" > sources.txt \
    && javac -encoding UTF-8 -d out @sources.txt

FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=builder /app/out ./out
COPY lib ./lib

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

CMD ["java", "-cp", "out:lib/h2-2.3.232.jar", "com.interntrack.App"]
