package strategy;

import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
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
    public Archive open(String archivePath)  {
        System.out.println("Відкриття ZIP архіву: " + archivePath);
        Archive archive = new Archive(archivePath, ArchiveFormat.ZIP);
        try {
            ZipFile zipFile = new ZipFile(archivePath);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    ArchivedFolder archivedFolder = new ArchivedFolder(
                            entry.getName(),
                            LocalDateTime.ofInstant(entry.getLastModifiedTime().toInstant(), ZoneId.systemDefault())
                    );
                    archive.getRootFolder().addChild(archivedFolder);
                } else {
                    ArchivedFile archivedFile = new ArchivedFile(
                            entry.getName(),
                            LocalDateTime.ofInstant(entry.getLastModifiedTime().toInstant(), ZoneId.systemDefault()),
                            entry.getSize(),
                            entry.getCompressedSize(),
                            String.valueOf(entry.getCrc())
                    );
                    archive.getRootFolder().addChild(archivedFile);

                }
            }
            zipFile.close();
            File file = new File(archivePath);
            archive.setTotalSize(file.length());

        }
        catch (IOException e) {
            throw new RuntimeException("Не вдалося відкрити архів", e);
        }
        return archive;
    }

    @Override
    public void extract(Archive archive, List<String> items, String destinationFolder) {
        System.out.println("Витягування файлів з " + archive.getFilePath());
        Path destinationPath = Paths.get(destinationFolder);
        try{
            Files.createDirectories(destinationPath);
        } catch (IOException e){
            throw new RuntimeException("Не вдалося створити директорію", e);
        }
        try (ZipFile zipFile = new ZipFile(archive.getFilePath())){
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if (items.isEmpty() || items.contains(entry.getName())) {
                    Path fullPath = destinationPath.resolve(entry.getName()).normalize();
                    if (!fullPath.startsWith(destinationPath)) {
                        throw new IOException("Небезпечний шлях у файлі: " + entry.getName());
                    }
                    if (entry.isDirectory()) {
                        Files.createDirectories(fullPath);
                    }else{
                        Path parentDir = fullPath.getParent();
                        if (parentDir != null) {
                            Files.createDirectories(parentDir);
                        }
                        try(InputStream inputStream = zipFile.getInputStream(entry)){
                            Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);

                        }
                    }
                }
                }
        } catch (IOException e){
            throw new RuntimeException("Не вдалося витягнути файл", e);
        }
        System.out.println("Розпакування в " + destinationFolder + " завершено");
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