package pl.dave.project.webdietserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.service.UserService;
import pl.dave.project.webdietserver.service.backup.BackupService;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/backup")
public class BackupController {

    private final BackupService backupService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Resource> backup() {
        try {
            return downloadBackupZipFile();
        } catch (MalformedURLException e) {
            throw new RestApiException("Error during download backup zip file: " + e.getMessage());
        }
    }

    private ResponseEntity<Resource> downloadBackupZipFile() throws MalformedURLException {
        String zipFilePath = backupService.backup("", userService.getCurrentLoginUser());
        Path path = Paths.get(zipFilePath);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
