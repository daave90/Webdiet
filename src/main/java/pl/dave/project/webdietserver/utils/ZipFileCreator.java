package pl.dave.project.webdietserver.utils;

import lombok.extern.log4j.Log4j2;
import pl.dave.project.webdietserver.exeption.RestApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
public class ZipFileCreator {
    public static String createZipFile(String pathToZipFileFolder, List<String> filePathsToBeAdded) {
        try {
            return getZipFilePath(pathToZipFileFolder, filePathsToBeAdded);
        } catch (IOException e) {
            throw new RestApiException("Exception during create zip file: " + e.getMessage());
        }
    }

    private static String getZipFilePath(String pathToZipFileFolder, List<String> filePathsToBeAdded) throws IOException {
        String zipFilePath = String.format("%s/%s.zip", pathToZipFileFolder, "WebdietJsonBackup");
        FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        for (String filePath : filePathsToBeAdded) {
            insertFileIntoZip(zipOutputStream, filePath);
        }
        zipOutputStream.close();
        fileOutputStream.close();
        return zipFilePath;
    }

    private static void insertFileIntoZip(ZipOutputStream zipOutputStream, String filePathToAdd) throws IOException {
        File fileToZip = new File(filePathToAdd);
        FileInputStream fileInputStream = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOutputStream.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fileInputStream.read(bytes)) >= 0) {
            zipOutputStream.write(bytes, 0, length);
        }
        fileInputStream.close();
    }
}
