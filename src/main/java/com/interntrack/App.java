package com.interntrack;

import com.interntrack.repository.Database;
import com.interntrack.repository.JdbcInternRepository;
import com.interntrack.service.InternService;
import com.interntrack.web.InternshipHttpServer;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            Database database = new Database("jdbc:h2:./data/interntrack;MODE=MySQL;DATABASE_TO_UPPER=false");
            database.initialize();

            JdbcInternRepository repository = new JdbcInternRepository(database);
            InternService service = new InternService(repository);
            int port = resolvePort();
            InternshipHttpServer server = new InternshipHttpServer(service, port);

            Files.writeString(
                    Path.of("server-startup.log"),
                    "InternTrack started successfully on port " + port + "\n",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            server.start();
        } catch (Exception ex) {
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(
                    Path.of("server-startup.log"),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            ))) {
                ex.printStackTrace(writer);
            }
            throw ex;
        }
    }

    private static int resolvePort() {
        String port = System.getenv("PORT");
        if (port == null || port.isBlank()) {
            return 8080;
        }
        try {
            return Integer.parseInt(port.trim());
        } catch (NumberFormatException ex) {
            return 8080;
        }
    }
}
