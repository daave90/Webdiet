package pl.dave.project.webdietserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dave.project.webdietserver.service.UserService;
import pl.dave.project.webdietserver.service.backup.BackupService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/backup")
public class BackupController {

    private final BackupService backupService;
    private final UserService userService;

    @PostMapping
    public void backup() {
        backupService.backup("", userService.getCurrentLoginUser());
    }
}
