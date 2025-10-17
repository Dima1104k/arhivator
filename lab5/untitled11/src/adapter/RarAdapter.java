package adapter;

import model.*;

import adapter.LegacyRarEngine.RarRecord;
import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


public class RarAdapter {
    private LegacyRarEngine legacyEngine;
    public RarAdapter() {
        this.legacyEngine = new LegacyRarEngine();
    }


    public void createNewArchive(String path) {
        int result = legacyEngine.createRarArchive(path);

        // АДАПТАЦІЯ замість коду помилки кидаємо виняток
        if (result != 0) {
            throw new RuntimeException("Не вдалося створити RAR архів: " + path);
        }
    }
    public void addFileToArchive(String filePath) {
        int result = legacyEngine.addFileToRar(filePath);

        // АДАПТАЦІЯ замість коду помилки кидаємо виняток з поясненням
        if (result != 0) {
            throw new RuntimeException("Не вдалося додати файл до архіву: " + filePath);
        }
    }
    public void extractFileByIndex(int index, String destinationFolder) {
        int result = legacyEngine.extractFileByIndex(index, destinationFolder);

        if (result != 0) {
            throw new RuntimeException("Не вдалося витягти файл з індексом " + index);
        }
    }
    public void deleteFileByIndex(int index) {
        int result = legacyEngine.deleteRecordByIndex(index);

        if (result != 0) {
            throw new RuntimeException("Не вдалося видалити файл з індексом " + index);
        }
    }
    // дані файлу за його номером для перевірки цілісності.
    public byte[] getFileData(int index) {
        byte[] data = legacyEngine.getFileDataByIndex(index);

        if (data == null) {
            throw new RuntimeException("Не вдалося прочитати дані файлу з індексом " + index);
        }

        return data;
    }
    public void closeArchive() {
        legacyEngine.closeRarFile();
    }
    public RarRecord getFileInfo(int index) {
        return legacyEngine.getRecordByIndex(index);
    }
    public int getFileCount() {
        return legacyEngine.getRecordCount();
    }
    public List<ArchiveItem> listAllFiles() {
        List<ArchiveItem> items = new ArrayList<>();


        int count = legacyEngine.getRecordCount();

        // АДАПТАЦІЯ проходимо по індексам і збираємо у список
        for (int i = 0; i < count; i++) {

            RarRecord record = legacyEngine.getRecordByIndex(i);

            if (record != null) {
                // АДАПТАЦІЯ перетворюємо Legacy формат у наш формат
                ArchiveItem item = convertRarRecordToArchiveItem(record);
                items.add(item);
            }
        }

        return items;
    }
    public void openArchive(String path) {
        // Legacy повертає код помилки 0 (успіх) або -1 (помилка)
        int result = legacyEngine.openRarFile(path);

        // АДАПТАЦІЯ: замість коду помилки кидаємо виняток
        if (result != 0) {
            throw new RuntimeException("Не вдалося відкрити RAR архів: " + path);
        }
    }

    public long getArchiveSize(String archivePath) {
        File metaFile = new File(archivePath + ".meta");

        if (metaFile.exists()) {
            return metaFile.length();
        } else {
            // якщо мета файлу немає, повертаємо приблизний розмір
            return getFileCount() * 1000L;
        }
    }
    private ArchiveItem convertRarRecordToArchiveItem(RarRecord record) {
        String name = record.filename;

        // АДАПТАЦІЯ ДАТИ Unix timestamp в LocalDateTime
        LocalDateTime date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(record.timeStamp),
                ZoneId.systemDefault()
        );

        if (record.isFolder) {
            return new ArchivedFolder(name, date);
        } else {
            return new ArchivedFile(
                    name,
                    date,
                    record.sizeBytes,
                    record.packedBytes,
                    "CRC-RAR"
            );
        }
    }
}