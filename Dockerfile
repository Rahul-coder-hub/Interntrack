FROM eclipse-temurin:23-jdk

WORKDIR /app

COPY . .

RUN mkdir -p out \
    && find src/main/java -name "*.java" > sources.txt \
    && javac -encoding UTF-8 -d out @sources.txt

EXPOSE 8080

CMD ["java", "-cp", "out:lib/h2-2.3.232.jar", "com.interntrack.App"]
