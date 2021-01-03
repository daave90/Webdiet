package pl.dave.project.webdietserver.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

@Data
public class AbstractEntity {

    @Id
    protected String guid = UUID.randomUUID().toString();
    protected long creationTimestamp = Instant.now().toEpochMilli();
}
