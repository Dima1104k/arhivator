import iterator.ArchiveIterator;
import iterator.IteratorType;
import model.Archive;
import model.ArchivedFile;
import strategy.RarStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestRarAdapter {
    public static void main(String[] args) {
        System.out.println("ТЕСТ RAR ADAPTERа\n");

        try {

            System.out.println("Створення архіву...");
            RarStrategy rar = new RarStrategy();

            Archive created = rar.create("test.rar",
                    List.of("test_files/test_file1.txt",
                            "test_files/test_file2.txt"));

            System.out.println("Створено: " + created.getFilePath());
            System.out.println("Розмір архіву: " + created.getTotalSize());

            System.out.println("\nФайли в архіві: ");
            ArchiveIterator iterator = created.getIterator(IteratorType.BREADTH_FIRST);
            while (iterator.hasNext()) {
                var item = iterator.next();
                System.out.println("   - " + item.getName());
            }

            int fileCount = 0;
            iterator = created.getIterator(IteratorType.BREADTH_FIRST);
            while (iterator.hasNext()) {
                var item = iterator.next();
                if (item instanceof ArchivedFile) {
                    fileCount++;
                }
            }

            System.out.println("\nВідкриття архіву...");
            Archive opened = rar.open("test.rar");
            System.out.println("Відкрито: " + fileCount + " файлів");


            System.out.println("\nСтворюємо папку для витягування...");
            File extractDir = new File("extracted");
            if (!extractDir.exists()) {
                extractDir.mkdirs();
                System.out.println("Створено папку: " + extractDir.getAbsolutePath());
            }

            System.out.println("\nВитягування файлів...");
            rar.extract(opened, new ArrayList<>(), "extracted");
            System.out.println("Витягнуто!");
            System.out.println("\nТест цілісності файлів...");
            boolean ok = rar.test(opened);

            System.out.println("\nВСЕ ПРАЦЮЄ!");

        } catch (Exception e) {
            System.err.println("\nПОМИЛКА: " + e.getMessage());
            System.err.println("\nДетальна інформація:");
            e.printStackTrace();


        }
    }
}