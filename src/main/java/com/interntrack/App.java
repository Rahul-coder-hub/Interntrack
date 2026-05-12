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
            InternshipHttpServer server = new InternshipHttpServer(service, 8080);

            Files.writeString(
                    Path.of("server-startup.log"),
                    "InternTrack started successfully on port 8080\n",
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
}
