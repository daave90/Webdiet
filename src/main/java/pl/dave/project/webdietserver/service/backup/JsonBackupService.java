package pl.dave.project.webdietserver.service.backup;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.entity.enums.UserRole;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.service.ProductService;
import pl.dave.project.webdietserver.service.RecipeService;
import pl.dave.project.webdietserver.service.UserService;
import pl.dave.project.webdietserver.utils.ZipFileCreator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class JsonBackupService implements BackupService {

    private static final String DEFAULT_BACKUP_FILEPATH = "src/main/resources/backups";
    private final UserService userService;
    private final ProductService productService;
    private final RecipeService recipeService;

    @Override
    public String backup(String backupFolderPath, User user) {
        log.info("**************************************************************************************************");
        log.info("Creating database json backup");
        log.info("Login user: " + user);
        backupFolderPath = checkIfBackupFolderPathNotEmpty(backupFolderPath);
        try {
            return createZipFile(backupFolderPath, user);
        } catch (IOException e) {
            throw new RestApiException("Exception during backup: " + e.getMessage());
        }
    }

    private String createZipFile(String backupFolderPath, User user) throws IOException {
        List<String> backupFilePaths = new ArrayList<>();
        backupFilePaths.add(backupProducts(backupFolderPath, user));
        backupFilePaths.add(backupRecipes(backupFolderPath, user));
        if (user.getRole() == UserRole.ADMIN) {
            backupFilePaths.add(backupUsers(backupFolderPath));
        }
        return ZipFileCreator.createZipFile(backupFolderPath, backupFilePaths);
    }

    private String checkIfBackupFolderPathNotEmpty(String backupFolderPath) {
        if (StringUtils.isEmpty(backupFolderPath)) {
            return DEFAULT_BACKUP_FILEPATH;
        }
        return backupFolderPath;
    }

    private String backupRecipes(String backupFolderPath, User user) throws IOException {
        String filePath = String.format("%s/recipes.json", backupFolderPath);
        FileWriter jsonFile = new FileWriter(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        String recipesJson = objectMapper.writeValueAsString(recipeService.mapToRequestList(user));
        jsonFile.write(recipesJson);
        jsonFile.close();
        return filePath;
    }

    private String backupProducts(String backupFolderPath, User user) throws IOException {
        String filePath = String.format("%s/products.json", backupFolderPath);
        FileWriter jsonFile = new FileWriter(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        String productsJson = objectMapper.writeValueAsString(productService.mapToRequestList(user));
        jsonFile.write(productsJson);
        jsonFile.close();
        return filePath;
    }

    private String backupUsers(String backupFolderPath) throws IOException {
        String filePath = String.format("%s/users.json", backupFolderPath);
        FileWriter jsonFile = new FileWriter(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(userService.mapToRequestList());
        jsonFile.write(userJson);
        jsonFile.close();
        return filePath;
    }
}
