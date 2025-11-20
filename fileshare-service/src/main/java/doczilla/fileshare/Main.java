package doczilla.fileshare;

import doczilla.fileshare.controller.DownloadServlet;
import doczilla.fileshare.controller.UploadServlet;
import doczilla.fileshare.repository.MetadataStore;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("FILESHARE_PORT", "8080"));
        String storagePath = System.getenv().getOrDefault("FILESHARE_STORAGE", "storage");

        Path storage = Paths.get(storagePath).toAbsolutePath();
        File filesDir = storage.resolve("files").toFile();
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }
        File metaFile = storage.resolve("metadata.json").toFile();
        MetadataStore metadataStore = new MetadataStore(metaFile);

        Server server = new Server(port);
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");

        ServletHolder staticHolder = new ServletHolder("default", DefaultServlet.class);
        staticHolder.setInitParameter("resourceBase", Main.class.getResource("/static").toExternalForm());
        staticHolder.setInitParameter("dirAllowed", "false");
        ctx.addServlet(staticHolder, "/");

        ctx.addServlet(new ServletHolder(new UploadServlet(filesDir.toPath(), metadataStore)), "/upload");
        ctx.addServlet(new ServletHolder(new DownloadServlet(filesDir.toPath(), metadataStore)), "/download/*");

        server.setHandler(ctx);
        server.start();
        server.join();
    }
}
