import strategy.*;
import model.*;
import java.io.File;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        try {
            System.out.println("Factory вибирає потрібну Strategy залежно від формату\n");

            System.out.println("1. Запит на RAR формат:");
            IArchiverStrategy rarStrategy = ArchiverStrategyFactory.createStrategy(ArchiveFormat.RAR);
            System.out.println("Factory обрав: " + rarStrategy.getClass().getSimpleName());

            System.out.println("\n2. Запит на 7Z формат:");
            IArchiverStrategy sevenZipStrategy = ArchiverStrategyFactory.createStrategy(ArchiveFormat.SevenZ);
            System.out.println("Factory обрав: " + sevenZipStrategy.getClass().getSimpleName());


            System.out.println("\n\nADAPTER ПАТЕРН");
            System.out.println("UniversalAdapter адаптує РІЗНІ legacy бібліотеки\n");

            System.out.println("3. RarStrategy створює UniversalAdapter для RAR:");
            Archive rarArchive = rarStrategy.create("demo.rar",
                    List.of("test_files/test_file1.txt", "test_files/test_file2.txt"));
            System.out.println("RAR архів створено: " + rarArchive.getFilePath());

            System.out.println("\n4. SevenZipStrategy створює UniversalAdapter для 7Z:");
            Archive szArchive = sevenZipStrategy.create("demo.7z",
                    List.of("test_files/test_file1.txt", "test_files/test_file2.txt"));
            System.out.println("7Z архів створено: " + szArchive.getFilePath());

            System.out.println("\n\nПЕРЕВІРКА");

            System.out.println("\n5. Відкриття RAR архіву:");
            Archive openedRar = rarStrategy.open("demo.rar");
            System.out.println("Відкрито RAR");

            System.out.println("\n6. Витягування з RAR:");
            new File("extracted_rar").mkdirs();
            rarStrategy.extract(openedRar, new ArrayList<>(), "extracted_rar");
            System.out.println("Витягнуто з RAR");

            System.out.println("\n7. Відкриття 7Z архіву:");
            Archive opened7z = sevenZipStrategy.open("demo.7z");
            System.out.println("Відкрито 7Z");

            System.out.println("\n8. Витягування з 7Z:");
            new File("extracted_7z").mkdirs();
            sevenZipStrategy.extract(opened7z, new ArrayList<>(), "extracted_7z");
            System.out.println(" Витягнуто з 7Z");

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


}