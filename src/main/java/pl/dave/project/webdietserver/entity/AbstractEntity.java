package pl.dave.project.webdietserver.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Data
@MappedSuperclass
public class AbstractEntity {
    @Id
    protected String guid = UUID.randomUUID().toString();

    @Version
    protected Long ver = 0L;

    protected long creationTimestamp = Instant.now().toEpochMilli();
}
