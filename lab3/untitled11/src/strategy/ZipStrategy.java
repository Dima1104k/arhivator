package strategy;

import model.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipStrategy implements IArchiverStrategy {

    @Override
    public Archive create(String archivePath, List<String> files) {
        System.out.println("Створення ZIP архіву: " + archivePath);
        Archive archive = new Archive(archivePath, ArchiveFormat.ZIP);
        long totalCompressedSize = 0;

        try (FileOutputStream fos = new FileOutputStream(archivePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {


            for (String filePath : files) {
                File file = new File(filePath);

                if (!file.exists()) {
                    System.err.println("Файл не знайдено: " + filePath);
                    continue;
                }

                ZipEntry entry = new ZipEntry(file.getName());
                entry.setTime(file.lastModified());
                entry.setSize(file.length());
                zos.putNextEntry(entry);

                Files.copy(file.toPath(), zos);
                zos.closeEntry();

                ArchivedFile archivedFile = new ArchivedFile(
                        file.getName(),
                        LocalDateTime.now(),
                        file.length(),
                        entry.getCompressedSize(),
                        file.getAbsolutePath()
                );

                archive.getRootFolder().addChild(archivedFile);
                totalCompressedSize += file.length();
            }

        } catch (IOException e) {
            throw new RuntimeException("Помилка створення ZIP: " + e.getMessage(), e);
        }

        archive.setTotalSize(totalCompressedSize);
        return archive;
    }
    @Override
    public Archive open(String archivePath) {
        System.out.println("Відкриття ZIP архіву: " + archivePath);
        Archive archive = new Archive(archivePath, ArchiveFormat.ZIP);
        ArchivedFile file1 = new ArchivedFile("document.txt", LocalDateTime.now(), 1024, 450, "crc32-a1b2c3d4");
        archive.getRootFolder().addChild(file1);
        archive.setTotalSize(450);
        return archive;
    }

    @Override
    public void extract(Archive archive, List<String> items, String destinationFolder) {
        System.out.println("Розпакування з " + archive.getFilePath() + " до " + destinationFolder);
    }

    @Override
    public void add(Archive archive, List<String> files) {
        System.out.println("Додавання файлів до " + archive.getFilePath());
    }

    @Override
    public void delete(Archive archive, List<String> itemPaths) {
        System.out.println("Видалення з " + archive.getFilePath());
    }

    @Override
    public boolean test(Archive archive) {
        System.out.println("Тестування архіву " + archive.getFilePath());
        return true;
    }

    @Override
    public Map<String, String> checksum(Archive archive, List<String> itemPaths, String algorithm) {
        System.out.println("Розрахунок checksum для " + archive.getFilePath());
        return new HashMap<>();
    }

    @Override
    public List<String> split(Archive archive, int partSizeMB) {
        System.out.println("Розбиття на частини " + archive.getFilePath());
        return new ArrayList<>();
    }

    @Override
    public String join(String firstPartPath) {
        System.out.println("З'єднання частин, починаючи з " + firstPartPath);
        return firstPartPath.replace(".part1.zip", ".zip");
    }
}