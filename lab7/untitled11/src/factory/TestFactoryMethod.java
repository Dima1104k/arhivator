package factory;

import java.nio.file.Files;
import java.io.File;
import java.util.*;

public class TestFactoryMethod {
    public static void main(String[] args) {
        System.out.println("FACTORY METHOD ПАТЕРН");
        try {
            createTestFiles();

            List<String> files = List.of(
                    "test_files2/document.txt",
                    "test_files2/data.txt"
            );

            System.out.println("СТВОРЕННЯ АРХІВІВ через Factory Method:\n");
            ArchiveProcessor[] processors = {
                    new ZipArchiveProcessor(),
                    new RarArchiveProcessor()
            };

            String[] paths = {"demo.zip", "demo.rar"};
            String[] names = {"ZIP", "RAR"};


            for (int i = 0; i < processors.length; i++) {
                System.out.println("─" + names[i]);
                processors[i].createArchive(paths[i], files);
                processors[i].showInfo();
                processors[i].testArchive();
                System.out.println();
            }

            System.out.println("ВИТЯГУВАННЯ");
            new File("extracted").mkdirs();
            processors[0].extractFiles("extracted", new ArrayList<>());

            System.out.println("FACTORY METHOD ПРАЦЮЄ");

        } catch (Exception e) {
            System.err.println("ПОМИЛКА: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTestFiles() {
        try {
            File dir = new File("test_files2");
            dir.mkdirs();

            File file1 = new File("test_files2/document.txt");
            File file2 = new File("test_files2/data.txt");
            Files.write(file1.toPath(), "Документ для Factory Method\n".getBytes());
            Files.write(file2.toPath(), "Файл з даними\n".getBytes());

            System.out.println("Тестові файли створено\n");

        } catch (Exception e) {
            System.err.println("Помилка: " + e.getMessage());
        }
    }
}