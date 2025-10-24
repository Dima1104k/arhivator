package adapter;


import model.*;
import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


public class UniversalArchiveAdapter {

    private ArchiveFormat format;

    private LegacyRarEngine rarEngine;
    private LegacySevenZipEngine sevenZipEngine;


    public UniversalArchiveAdapter(ArchiveFormat format) {
        this.format = format;

        // АДАПТЕР ВИРІШУЄ яку з legacy бібліотеку використовувати
        if (format == ArchiveFormat.RAR) {
            // для RAR формату
            this.rarEngine = new LegacyRarEngine();
            System.out.println("обрано RAR");
        } else if (format == ArchiveFormat.SevenZ) {
            // Для 7Z формату створюємо 7Z
            this.sevenZipEngine = new LegacySevenZipEngine();
            System.out.println("обрано 7Z");
        } else {
            throw new IllegalArgumentException("Формат не підтримується: " + format);
        }
    }


    public void openArchive(String path) {
        int result;

        if (format == ArchiveFormat.RAR) {
            result = rarEngine.openRarFile(path);
        } else {
            result = sevenZipEngine.open7zFile(path);
        }

        if (result != 0) {
            throw new RuntimeException("Не вдалося відкрити архів: " + path +
                    " (формат: " + format + ")");
        }
    }

    public void createArchive(String path) {
        int result;

        if (format == ArchiveFormat.RAR) {
            result = rarEngine.createRarArchive(path);
        } else {
            result = sevenZipEngine.create7zArchive(path);
        }

        if (result != 0) {
            throw new RuntimeException("Не вдалося створити архів: " + path);
        }
    }


    public void addFile(String filePath) {
        int result;

        if (format == ArchiveFormat.RAR) {
            result = rarEngine.addFileToRar(filePath);
        } else {
            result = sevenZipEngine.addFileTo7z(filePath);
        }

        if (result != 0) {
            throw new RuntimeException("Не вдалося додати файл: " + filePath);
        }
    }

    public void extractFile(int index, String destination) {
        int result;

        if (format == ArchiveFormat.RAR) {
            result = rarEngine.extractFileByIndex(index, destination);
        } else {
            result = sevenZipEngine.extractFileByIndex(index, destination);
        }

        if (result != 0) {
            throw new RuntimeException("Не вдалося витягти файл з індексом: " + index);
        }
    }

    public int getFileCount() {

        if (format == ArchiveFormat.RAR) {
            return rarEngine.getRecordCount();
        } else {
            return sevenZipEngine.getRecordCount();
        }
    }


    public List<ArchiveItem> listAllFiles() {
        List<ArchiveItem> items = new ArrayList<>();


        int count = getFileCount();


        for (int i = 0; i < count; i++) {
            if (format == ArchiveFormat.RAR) {

                var record = rarEngine.getRecordByIndex(i);
                if (record != null) {

                    items.add(convertRarRecordToArchiveItem(record));
                }
            } else {

                var record = sevenZipEngine.getRecordByIndex(i);
                if (record != null) {

                    items.add(convert7zRecordToArchiveItem(record));
                }
            }
        }

        return items;
    }


    public void closeArchive() {
        if (format == ArchiveFormat.RAR) {
            rarEngine.closeRarFile();
        } else {
            sevenZipEngine.close7zFile();
        }
    }

    public long getArchiveSize(String path) {
        File metaFile = new File(path + ".meta");
        return metaFile.exists() ? metaFile.length() : getFileCount() * 1000L;
    }


    private ArchiveItem convertRarRecordToArchiveItem(LegacyRarEngine.RarRecord record) {
        String name = record.filename;


        LocalDateTime date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(record.timeStamp),
                ZoneId.systemDefault()
        );


        if (record.isFolder) {
            return new ArchivedFolder(name, date);
        } else {
            return new ArchivedFile(name, date,
                    record.sizeBytes,
                    record.packedBytes,
                    "CRC-RAR");
        }
    }


    private ArchiveItem convert7zRecordToArchiveItem(LegacySevenZipEngine.SevenZipRecord record) {
        String name = record.filename;


        LocalDateTime date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(record.timeStamp),
                ZoneId.systemDefault()
        );

        if (record.isFolder) {
            return new ArchivedFolder(name, date);
        } else {
            return new ArchivedFile(name, date,
                    record.sizeBytes,
                    record.packedBytes,
                    "CRC-7Z");
        }
    }
}