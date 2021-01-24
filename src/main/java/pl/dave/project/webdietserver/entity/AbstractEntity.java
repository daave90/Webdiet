package pl.dave.project.webdietserver.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

@Data
public class AbstractEntity {

    @Id
    protected String guid;
    protected long creationTimestamp;

    public AbstractEntity(String guid) {
        this.guid = guid;
        this.creationTimestamp = Instant.now().toEpochMilli();
    }

    public AbstractEntity() {
        this(UUID.randomUUID().toString());
    }
}
