package strategy;

import adapter.UniversalArchiveAdapter;
import model.*;
import java.io.File;
import java.util.*;


public class SevenZipStrategy implements IArchiverStrategy {

    @Override
    public Archive create(String path, List<String> files) {
        System.out.println("SevenZipStrategy: створення 7Z архіву");


        UniversalArchiveAdapter adapter = new UniversalArchiveAdapter(ArchiveFormat.SevenZ);

        try {
            adapter.createArchive(path);

            Archive archive = new Archive(path, ArchiveFormat.SevenZ);

            for (String filePath : files) {
                File file = new File(filePath);
                if (!file.exists()) continue;

                adapter.addFile(filePath);

                archive.getRootFolder().addChild(
                        new ArchivedFile(file.getName(),
                                java.time.LocalDateTime.now(),
                                file.length(), file.length() / 2,
                                "CRC-7Z")
                );
            }

            adapter.closeArchive();
            archive.setTotalSize(adapter.getArchiveSize(path));
            return archive;

        } catch (Exception e) {
            throw new RuntimeException("Помилка створення 7Z", e);
        }
    }

    @Override
    public Archive open(String path) {
        System.out.println("SevenZipStrategy: відкриття 7Z архіву");

        UniversalArchiveAdapter adapter = new UniversalArchiveAdapter(ArchiveFormat.SevenZ);

        try {
            adapter.openArchive(path);

            Archive archive = new Archive(path, ArchiveFormat.SevenZ);

            List<ArchiveItem> items = adapter.listAllFiles();
            for (ArchiveItem item : items) {
                archive.getRootFolder().addChild(item);
            }

            archive.setTotalSize(adapter.getArchiveSize(path));
            adapter.closeArchive();
            return archive;

        } catch (Exception e) {
            throw new RuntimeException("Помилка відкриття 7Z", e);
        }
    }

    @Override
    public void extract(Archive archive, List<String> items, String destination) {
        System.out.println("SevenZipStrategy: витягування з 7Z");

        UniversalArchiveAdapter adapter = new UniversalArchiveAdapter(ArchiveFormat.SevenZ);

        try {
            adapter.openArchive(archive.getFilePath());
            int count = adapter.getFileCount();

            for (int i = 0; i < count; i++) {
                if (items.isEmpty()) {
                    adapter.extractFile(i, destination);
                }
            }

            adapter.closeArchive();
        } catch (Exception e) {
            throw new RuntimeException("Помилка витягування 7Z", e);
        }
    }


    @Override
    public void add(Archive archive, List<String> files) {
        throw new UnsupportedOperationException(" не реалізовано");
    }

    @Override
    public void delete(Archive archive, List<String> itemPaths) {
        throw new UnsupportedOperationException(" не реалізовано");
    }

    @Override
    public boolean test(Archive archive) {
        return true;
    }

    @Override
    public Map<String, String> checksum(Archive archive, List<String> itemPaths, String algorithm) {
        return new HashMap<>();
    }

    @Override
    public List<String> split(Archive archive, int partSizeMB) {
        return new ArrayList<>();
    }

    @Override
    public String join(String firstPartPath) {
        return firstPartPath.replace(".part1.7z", ".7z");
    }
}