package pl.dave.project.webdietserver.service.backup;

import pl.dave.project.webdietserver.entity.User;

public interface BackupService {
    String backup(String backupFolderPath, User user);
}
