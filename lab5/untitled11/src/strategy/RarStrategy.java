package strategy;

import model.Archive;
import model.ArchiveFormat;
import model.ArchiveItem;
import model.ArchivedFile;

import java.io.File;
import adapter.RarAdapter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RarStrategy implements IArchiverStrategy {

    @Override
    public Archive create(String archivePath, List<String> files) {
        System.out.println("RarStrategy: створення RAR архіву " + archivePath);
        // адаптер для роботи з Legacy бібліотекою
        RarAdapter adapter = new RarAdapter();

        try {
            // через адаптер створюємо новий архів
            adapter.createNewArchive(archivePath);

            Archive archive = new Archive(archivePath, ArchiveFormat.RAR);
            long totalSize = 0;

            for (String filePath : files) {
                File file = new File(filePath);

                if (!file.exists()) {
                    System.err.println("Файл не знайдено: " + filePath);
                    continue;
                }

                // через адаптер додаємо файл
                adapter.addFileToArchive(filePath);

                int currentIndex = adapter.getFileCount() - 1;
                var fileInfo = adapter.getFileInfo(currentIndex);

                ArchivedFile archivedFile = new ArchivedFile(
                        file.getName(),
                        java.time.LocalDateTime.now(),
                        fileInfo.sizeBytes,
                        fileInfo.packedBytes,
                        "CRC-RAR"
                );

                archive.getRootFolder().addChild(archivedFile);
                totalSize += fileInfo.packedBytes;
            }

            adapter.closeArchive();

            archive.setTotalSize(totalSize);
            return archive;

        } catch (Exception e) {
            throw new RuntimeException("Помилка створення RAR архіву", e);
        }
    }

    @Override
    public Archive open(String archivePath) {
        System.out.println("RarStrategy: відкриття RAR архіву " + archivePath);

        RarAdapter adapter = new RarAdapter();

        try {
            adapter.openArchive(archivePath);

            Archive archive = new Archive(archivePath, ArchiveFormat.RAR);

            // через адаптер отримуємо список всіх файлів
            // адаптер вже перетворив їх з незручного формату у зручний
            List<ArchiveItem> items = adapter.listAllFiles();

            for (ArchiveItem item : items) {
                archive.getRootFolder().addChild(item);
            }

            long size = adapter.getArchiveSize(archivePath);
            archive.setTotalSize(size);


            adapter.closeArchive();

            return archive;

        } catch (Exception e) {
            throw new RuntimeException("Помилка відкриття RAR архіву", e);
        }
    }
    @Override
    public void extract(Archive archive, List<String> items, String destinationFolder) {
        System.out.println("RarStrategy: витягування з RAR " + archive.getFilePath());

        RarAdapter adapter = new RarAdapter();

        try {
            adapter.openArchive(archive.getFilePath());

            int count = adapter.getFileCount();

            for (int i = 0; i < count; i++) {
                var fileInfo = adapter.getFileInfo(i);

                if (fileInfo != null && !fileInfo.isFolder) {

                    if (items.isEmpty() || items.contains(fileInfo.filename)) {
                        adapter.extractFileByIndex(i, destinationFolder);
                        System.out.println("Витягнуто: " + fileInfo.filename);
                    }
                }
            }

            adapter.closeArchive();
            System.out.println("Розпакування завершено в " + destinationFolder);

        } catch (Exception e) {
            throw new RuntimeException("Помилка витягування з RAR", e);
        }
    }

    @Override
    public void add(Archive archive, List<String> files) {
        System.out.println("RarStrategy: додавання файлів до RAR " + archive.getFilePath());

        RarAdapter adapter = new RarAdapter();

        try {
            adapter.openArchive(archive.getFilePath());

            for (String filePath : files) {
                adapter.addFileToArchive(filePath);
                System.out.println("Додано: " + filePath);
            }

            adapter.closeArchive();

        } catch (Exception e) {
            throw new RuntimeException("Помилка додавання файлів до RAR", e);
        }
    }

    @Override
    public void delete(Archive archive, List<String> itemPaths) {
        System.out.println("RarStrategy: видалення з RAR " + archive.getFilePath());

        RarAdapter adapter = new RarAdapter();

        try {
            adapter.openArchive(archive.getFilePath());

            int count = adapter.getFileCount();

            // видалити з кінця щоб індекси не збивались
            for (int i = count - 1; i >= 0; i--) {
                var fileInfo = adapter.getFileInfo(i);

                if (fileInfo != null && itemPaths.contains(fileInfo.filename)) {
                    adapter.deleteFileByIndex(i);
                    System.out.println("Видалено: " + fileInfo.filename);
                }
            }

            adapter.closeArchive();

        } catch (Exception e) {
            throw new RuntimeException("Помилка видалення з RAR", e);
        }
    }

    @Override
    public boolean test(Archive archive) {
        System.out.println("RarStrategy: тест RAR архіву " + archive.getFilePath());

        RarAdapter adapter = new RarAdapter();

        try {
            adapter.openArchive(archive.getFilePath());

            int count = adapter.getFileCount();
            boolean allOk = true;

            // перевіряємо чи можемо прочитати всі файли
            for (int i = 0; i < count; i++) {
                var fileInfo = adapter.getFileInfo(i);

                if (fileInfo != null && !fileInfo.isFolder) {
                    try {
                        byte[] data = adapter.getFileData(i);
                        // якщо дані прочиталися то файл OK
                    } catch (Exception e) {
                        System.err.println("Помилка в файлі: " + fileInfo.filename);
                        allOk = false;
                    }
                }
            }

            adapter.closeArchive();

            System.out.println("Тест завершено: " + (allOk ? "OK" : "Є помилки"));
            return allOk;

        } catch (Exception e) {
            System.err.println("Критична помилка тесту: " + e.getMessage());
            return false;
        }
    }
    @Override
    public Map<String, String> checksum(Archive archive, List<String> itemPaths, String algorithm) {
        System.out.println("RarStrategy: checksum для RAR");

        Map<String, String> checksums = new HashMap<>();
        RarAdapter adapter = new RarAdapter();

        try {
            adapter.openArchive(archive.getFilePath());

            MessageDigest digest = MessageDigest.getInstance(algorithm);
            int count = adapter.getFileCount();

            for (int i = 0; i < count; i++) {
                var fileInfo = adapter.getFileInfo(i);

                if (fileInfo != null && !fileInfo.isFolder) {
                    if (itemPaths.isEmpty() || itemPaths.contains(fileInfo.filename)) {
                        byte[] data = adapter.getFileData(i);
                        digest.update(data);

                        String hash = bytesToHex(digest.digest());
                        checksums.put(fileInfo.filename, hash);
                        digest.reset();
                    }
                }
            }

            adapter.closeArchive();

        } catch (Exception e) {
            System.err.println("Помилка обчислення checksum: " + e.getMessage());
        }

        return checksums;
    }
    @Override
    public List<String> split(Archive archive, int partSizeMB) {
        System.out.println("RarStrategy: розбиття RAR на частини по " + partSizeMB + " MB");

        List<String> parts = new ArrayList<>();
        long totalSize = archive.getTotalSize();
        long partSizeBytes = partSizeMB * 1024L * 1024L;
        int partCount = (int) Math.ceil((double) totalSize / partSizeBytes);

        for (int i = 1; i <= partCount; i++) {
            String partName = archive.getFilePath() + ".part" + i + ".rar";
            parts.add(partName);
            System.out.println("Частина: " + partName);
        }

        return parts;
    }
    @Override
    public String join(String firstPartPath) {
        System.out.println("RarStrategy: з'єднання частин RAR");

        String resultPath = firstPartPath.replaceAll("\\.part\\d+\\.rar$", ".rar");
        System.out.println("Результат: " + resultPath);

        return resultPath;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }


}
