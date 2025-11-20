package doczilla.fileshare.controller;

import doczilla.fileshare.entity.FileMeta;
import doczilla.fileshare.repository.MetadataStore;
import lombok.RequiredArgsConstructor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class DownloadServlet extends HttpServlet {

    private final Path filesDir;
    private final MetadataStore metadataStore;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing token");
            return;
        }
        String token = path.substring(1);

        FileMeta meta = metadataStore.get(token);
        if (meta == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }
        Path file = filesDir.resolve(meta.getStoredName());
        if (!Files.exists(file)) {
            metadataStore.remove(token);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Missing file");
            return;
        }

        metadataStore.updateAccess(token);
        String probeContentType = Files.probeContentType(file);
        resp.setContentType(probeContentType == null ? "application/octet-stream" : probeContentType);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + meta.getOriginalName() + "\"");
        resp.setContentLengthLong(meta.getSize());
        Files.copy(file, resp.getOutputStream());
    }
}
