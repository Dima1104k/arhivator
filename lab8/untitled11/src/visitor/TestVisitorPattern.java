package visitor;

import model.*;

import java.time.LocalDateTime;

public class TestVisitorPattern {

    public static void main(String[] args) {

        ArchivedFolder root = new ArchivedFolder("archive.zip", LocalDateTime.now());

        // додаємо нормальні файли
        root.addChild(new ArchivedFile("readme.txt", LocalDateTime.now(),
                1024, 512, "12345"));
        root.addChild(new ArchivedFile("image.png", LocalDateTime.now(),
                50000, 45000, "67890"));

        // додаємо пошкоджений файл (нульовий розмір, немає checksum)
        root.addChild(new ArchivedFile("corrupted.dat", LocalDateTime.now(),
                0, 0, null));

        // створюємо папку з файлами
        ArchivedFolder docs = new ArchivedFolder("documents", LocalDateTime.now());
        docs.addChild(new ArchivedFile("doc1.pdf", LocalDateTime.now(),
                20000, 18000, "11111"));
        docs.addChild(new ArchivedFile("doc2.docx", LocalDateTime.now(),
                30000, 25000, "22222"));
        root.addChild(docs);

        // додаємо порожню папку
        ArchivedFolder emptyFolder = new ArchivedFolder("empty_folder", LocalDateTime.now());
        root.addChild(emptyFolder);


        System.out.println("ТЕСТУВАННЯ ПАТЕРНУ VISITOR");
        System.out.println("========================================\n");


        System.out.println("1. ПЕРЕВІРКА CHECKSUM:");
        System.out.println("----------------------------------------");
        ChecksumVisitor checksumVisitor = new ChecksumVisitor();
        root.accept(checksumVisitor);
        System.out.println("\nРезультат:");
        System.out.println("Загальна checksum: " + checksumVisitor.getTotalChecksum());
        System.out.println("Оброблено файлів: " + checksumVisitor.getFilesProcessed());


        System.out.println("\n\n2.ТЕСТУВАННЯ НА ПОШКОДЖЕННЯ:");
        System.out.println("----------------------------------------");
        TestArchiveVisitor testVisitor = new TestArchiveVisitor();
        root.accept(testVisitor);
        System.out.println("\n" + testVisitor.getReport());

        System.out.println("========================================");
        System.out.println("ТЕСТУВАННЯ ЗАВЕРШЕНО");

    }
}


