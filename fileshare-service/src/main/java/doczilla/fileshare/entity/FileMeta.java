package doczilla.fileshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileMeta {

    @JsonProperty("token")
    private String token;
    @JsonProperty("originalName")
    private String originalName;
    @JsonProperty("storedName")
    private String storedName;
    @JsonProperty("size")
    private long size;
    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("lastAccessAt")
    private Instant lastAccessAt;
    @JsonProperty("downloads")
    private int downloads;

    public void increaseDownloads() {
        this.downloads++;
    }
}
