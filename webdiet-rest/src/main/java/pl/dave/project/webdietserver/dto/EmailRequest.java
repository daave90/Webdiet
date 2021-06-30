package pl.dave.project.webdietserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailRequest {
    private String recipient;
    private String subject;
}
