package adapter;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.DeflaterOutputStream;


public class LegacySevenZipEngine {

    // сховище всіх архівів у пам'яті
    // ключ - шлях до архіву, значення - список файлів
    private static Map<String, List<SevenZipRecord>> archives = new HashMap<>();

    private String currentPath;
    private List<SevenZipRecord> currentRecords;
    private boolean isOpen = false;


    public int open7zFile(String path) {
        this.currentPath = path;


        if (!path.endsWith(".7z")) {
            return -1;
        }


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


    public SevenZipRecord getRecordByIndex(int index) {
        if (!isOpen || index < 0 || index >= currentRecords.size()) {
            return null;
        }
        return currentRecords.get(index);
    }


    public int create7zArchive(String path) {
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


    public int addFileTo7z(String sourceFilePath) {
        if (!isOpen) return -1;

        File file = new File(sourceFilePath);
        if (!file.exists()) return -1;

        try {
            // Читаємо файл з диска
            byte[] content = Files.readAllBytes(file.toPath());
            long originalSize = content.length;

            // РЕАЛЬНА КОМПРЕСІЯ через Deflater
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DeflaterOutputStream deflater = new DeflaterOutputStream(byteStream);
            deflater.write(content);
            deflater.close();

            byte[] compressedContent = byteStream.toByteArray();
            long compressedSize = compressedContent.length;


            SevenZipRecord record = new SevenZipRecord(
                    file.getName(),
                    originalSize,
                    compressedSize,
                    false,
                    System.currentTimeMillis(),
                    content
            );

            currentRecords.add(record);
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
            SevenZipRecord record = currentRecords.get(index);

            if (record.isFolder) return 0;

            File outputFile = new File(outputFolder, record.filename);

            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            Files.write(outputFile.toPath(), record.fileContent);

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
        SevenZipRecord record = currentRecords.get(index);
        return record.fileContent;
    }


    public void close7zFile() {
        isOpen = false;
    }


    private List<SevenZipRecord> generateSampleData() {
        List<SevenZipRecord> records = new ArrayList<>();
        records.add(new SevenZipRecord(
                "readme.txt",
                1024,
                512,
                false,
                System.currentTimeMillis(),
                "Sample 7Z file content".getBytes()
        ));
        return records;
    }

    private void saveToDisk() throws IOException {
        File metaFile = new File(currentPath + ".meta");
        StringBuilder sb = new StringBuilder();

        for (SevenZipRecord record : currentRecords) {
            sb.append(record.filename).append("|")
                    .append(record.sizeBytes).append("|")
                    .append(record.packedBytes).append("|")
                    .append(record.isFolder).append("|")
                    .append(record.timeStamp).append("\n");
        }

        Files.write(metaFile.toPath(), sb.toString().getBytes());
    }

    private List<SevenZipRecord> loadFromDisk(File metaFile) throws IOException {
        List<SevenZipRecord> records = new ArrayList<>();
        List<String> lines = Files.readAllLines(metaFile.toPath());

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                records.add(new SevenZipRecord(
                        parts[0],
                        Long.parseLong(parts[1]),
                        Long.parseLong(parts[2]),
                        Boolean.parseBoolean(parts[3]),
                        Long.parseLong(parts[4]),
                        new byte[0]
                ));
            }
        }

        return records;
    }


    public static class SevenZipRecord {
        public String filename;
        public long sizeBytes;
        public long packedBytes;
        public boolean isFolder;
        public long timeStamp;
        public byte[] fileContent;

        public SevenZipRecord(String filename, long sizeBytes, long packedBytes,
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