package doczilla.fileshare.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import doczilla.fileshare.entity.FileMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MetadataStore {

    private final File file;
    private final ObjectMapper mapper;
    private Map<String, FileMeta> store;

    public MetadataStore(File file) {
        this.file = file;
        this.mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        load();
    }

    public synchronized Map<String, FileMeta> all() {
        return new HashMap<>(store);
    }

    public synchronized FileMeta get(String token) {
        return store.get(token);
    }

    public synchronized void put(FileMeta meta) {
        store.put(meta.getToken(), meta);
        persist();
    }

    public synchronized void remove(String token) {
        store.remove(token);
        persist();
    }

    public synchronized void updateAccess(String token) {
        FileMeta m = store.get(token);
        if (m != null) {
            m.setLastAccessAt(Instant.now());
            m.increaseDownloads();
            persist();
        }
    }

    private synchronized void load() {
        if (!file.exists()) {
            store = Collections.synchronizedMap(new HashMap<>());
            persist();
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            if (bytes.length == 0) {
                store = Collections.synchronizedMap(new HashMap<>());
            } else {
                Map<String, FileMeta> m = mapper.readValue(bytes, new TypeReference<Map<String, FileMeta>>() {
                });
                store = Collections.synchronizedMap(new HashMap<>(m));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            store = Collections.synchronizedMap(new HashMap<>());
        }
    }

    private synchronized void persist() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, store);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
