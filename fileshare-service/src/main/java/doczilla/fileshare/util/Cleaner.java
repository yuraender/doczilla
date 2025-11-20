package doczilla.fileshare.util;

import doczilla.fileshare.entity.FileMeta;
import doczilla.fileshare.repository.MetadataStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class Cleaner extends Thread {

    private final MetadataStore store;
    private final Path filesDir;
    private final int expiryDays;

    public Cleaner(MetadataStore store, Path filesDir, int expiryDays) {
        this.store = store;
        this.filesDir = filesDir;
        this.expiryDays = expiryDays;
        setDaemon(true);
    }

    @Override
    public void run() {
        Instant expiryDate = Instant.now().minus(Duration.ofDays(expiryDays));
        while (true) {
            try {
                Map<String, FileMeta> files = store.all();
                for (Map.Entry<String, FileMeta> entry : files.entrySet()) {
                    FileMeta m = entry.getValue();
                    if (m.getLastAccessAt().isBefore(expiryDate)) {
                        Path path = filesDir.resolve(m.getStoredName());
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        store.remove(entry.getKey());
                    }
                }
                Thread.sleep(60000L);
            } catch (InterruptedException ex) {
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
