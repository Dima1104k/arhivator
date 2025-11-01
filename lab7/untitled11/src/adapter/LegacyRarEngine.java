package adapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class LegacyRarEngine {
    // фейкове сховище всіх архівів у пам'яті
    // ключ - шлях до архіву, значення - список файлів у ньому
    private static Map<String, List<RarRecord>> archives = new HashMap<>();


    // поточний відкритий архів
    private String currentPath;
    private List<RarRecord> currentRecords;
    private boolean isOpen = false;

    public int openRarFile(String path) {
        this.currentPath = path;

        // Перевіряємо розширення файлу
        if (!path.endsWith(".rar")) {
            return -1;  // Помилка: не RAR файл
        }

        // завантажити з диску
        File metaFile = new File(path + ".meta");

        if (metaFile.exists()) {
            try {
                currentRecords = loadFromDisk(metaFile);
            } catch (Exception e) {
                return -1;
            }
        } else {
            currentRecords = generateSampleData();
        }

        archives.put(path, currentRecords);
        isOpen = true;
        return 0;
    }

    public int getRecordCount() {
        if (!isOpen) return 0;
        return currentRecords.size();
    }

    public RarRecord getRecordByIndex(int index) {
        if (!isOpen || index < 0 || index >= currentRecords.size()) {
            return null;
        }
        return currentRecords.get(index);
    }

    public int createRarArchive(String path) {
        this.currentPath = path;
        this.currentRecords = new ArrayList<>();
        archives.put(path, currentRecords);
        isOpen = true;

        try {
            saveToDisk();
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public int addFileToRar(String sourceFilePath) {
        if (!isOpen) return -1;

        File file = new File(sourceFilePath);
        if (!file.exists()) return -1;

        try {
            byte[] content = Files.readAllBytes(file.toPath());
            long originalSize = content.length;
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DeflaterOutputStream deflater = new DeflaterOutputStream(byteStream);
            deflater.write(content);
            deflater.close();

            // стиснуті дані
            byte[] compressedContent = byteStream.toByteArray();
            long compressedSize = compressedContent.length;
            // створюємо запис про файл
            RarRecord record = new RarRecord(file.getName(), originalSize,
                    compressedSize, false, System.currentTimeMillis(), compressedContent);

            currentRecords.add(record);
            String dataFileName = currentPath + "." + record.filename + ".data";
            Files.write(Paths.get(dataFileName), record.fileContent);
            saveToDisk();

            return 0;

        } catch (Exception e) {
            return -1;
        }
    }

    public int extractFileByIndex(int index, String outputFolder) {
        if (!isOpen || index < 0 || index >= currentRecords.size()) {
            return -1;
        }

        try {
            RarRecord record = currentRecords.get(index);

            if (record.isFolder) return 0;

            File outputFile = new File(outputFolder, record.filename);
            outputFile.getParentFile().mkdirs();

            ByteArrayInputStream byteStream = new ByteArrayInputStream(record.fileContent);
            InflaterInputStream inflater = new InflaterInputStream(byteStream);
            byte[] decompressedContent = inflater.readAllBytes();
            inflater.close();

            Files.write(outputFile.toPath(), decompressedContent);
            return 0;

        } catch (Exception e) {
            return -1;
        }
    }

    public int deleteRecordByIndex(int index) {
        if (!isOpen || index < 0 || index >= currentRecords.size()) {
            return -1;
        }
        currentRecords.remove(index);
        try {
            saveToDisk();
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public byte[] getFileDataByIndex(int index) {
        if (!isOpen || index < 0 || index >= currentRecords.size()) {
            return null;
        }

        RarRecord record = currentRecords.get(index);
        return record.fileContent;
    }

    public void closeRarFile() {
        isOpen = false;
    }

    // допоміжні штуки
    private List<RarRecord> generateSampleData() {
        List<RarRecord> records = new ArrayList<>();

        records.add(new RarRecord("readme.txt", 1024, 512, false, System.currentTimeMillis(), "This is a sample README file content".getBytes()));

        records.add(new RarRecord("docs/", 0, 0, true,  // це папка
                System.currentTimeMillis(), new byte[0]));
        return records;
    }

    private void saveToDisk() throws IOException {
        File metaFile = new File(currentPath + ".meta");

        StringBuilder sb = new StringBuilder();
        for (RarRecord record : currentRecords) {
            sb.append(record.filename).append("|")
                    .append(record.sizeBytes).append("|")
                    .append(record.packedBytes).append("|")
                    .append(record.isFolder).append("|")
                    .append(record.timeStamp).append("\n");
        }

        Files.write(metaFile.toPath(), sb.toString().getBytes());
    }

    private List<RarRecord> loadFromDisk(File metaFile) throws IOException {
        List<RarRecord> records = new ArrayList<>();
        List<String> lines = Files.readAllLines(metaFile.toPath());

        for (String line : lines) {
            String[] parts = line.split("\\|");
            String dataFileName = currentPath + "." + parts[0] + ".data";
            File dataFile = new File(dataFileName);
            byte[] content = dataFile.exists() ? Files.readAllBytes(dataFile.toPath()) : new byte[0];
            if (parts.length >= 5) {
                records.add(new RarRecord(parts[0],
                        Long.parseLong(parts[1]),
                        Long.parseLong(parts[2]),
                        Boolean.parseBoolean(parts[3]),
                        Long.parseLong(parts[4]),
                        content
                ));
            }
        }

        return records;
    }


    public static class RarRecord {
        public String filename;
        public long sizeBytes;
        public long packedBytes;
        public boolean isFolder;
        public long timeStamp;
        public byte[] fileContent;

        public RarRecord(String filename, long sizeBytes, long packedBytes,
                         boolean isFolder, long timeStamp, byte[] fileContent) {
            this.filename = filename;
            this.sizeBytes = sizeBytes;
            this.packedBytes = packedBytes;
            this.isFolder = isFolder;
            this.timeStamp = timeStamp;
            this.fileContent = fileContent;
        }
    }
}
