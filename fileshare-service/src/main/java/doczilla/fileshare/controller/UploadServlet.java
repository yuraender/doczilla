package doczilla.fileshare.controller;

import doczilla.fileshare.entity.FileMeta;
import doczilla.fileshare.repository.MetadataStore;
import lombok.RequiredArgsConstructor;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class UploadServlet extends HttpServlet {

    public static final long MAX_FILE_SIZE = 1024L * 1024 * 1024;

    private final Path filesDir;
    private final MetadataStore metadataStore;

    @Override
    public void init() {
        MultipartConfigElement config = new MultipartConfigElement(
                filesDir.toFile().getAbsolutePath(),
                MAX_FILE_SIZE, MAX_FILE_SIZE,
                0
        );
        ServletRegistration registration = getServletConfig().getServletContext().getServletRegistration(getServletName());
        if (registration instanceof ServletRegistration.Dynamic) {
            ((ServletRegistration.Dynamic) registration).setMultipartConfig(config);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getContentType().startsWith("multipart/form-data")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Only multipart/form-data supported");
            return;
        }
        try {
            Part part = req.getPart("file");
            if (part == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing file part");
                return;
            }

            String originalName = part.getSubmittedFileName();
            String token = UUID.randomUUID().toString();
            String storedName = token + "_" + originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path target = filesDir.resolve(storedName);

            try (InputStream is = part.getInputStream()) {
                Files.copy(is, target);
            }
            long size = Files.size(target);

            FileMeta meta = new FileMeta(token, originalName, storedName, size, Instant.now());
            metadataStore.put(meta);

            resp.setContentType("application/json");
            resp.getWriter().write("{\"link\":\"" + "/download/" + token + "\"}");
        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Upload failed");
        }
    }
}
