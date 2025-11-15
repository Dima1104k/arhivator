package factory;

import model.Archive;
import strategy.IArchiverStrategy;

import java.util.List;

public abstract class ArchiveProcessor {

    protected IArchiverStrategy strategy;
    protected Archive currentArchive;


    protected abstract IArchiverStrategy createStrategy();


    public Archive createArchive(String path, List<String> files) {
        System.out.println("\n  ArchiveProcessor: створення архіву " + path);

        // виклик фабричного методу
        strategy = createStrategy();

        System.out.println("Використовується: " + strategy.getClass().getSimpleName());

        currentArchive = strategy.create(path, files);

        System.out.println("Архів створено!");
        return currentArchive;
    }


    public Archive openArchive(String path) {
        System.out.println("\nArchiveProcessor: відкриття " + path);

        if (strategy == null) {
            strategy = createStrategy();
        }

        try {
            currentArchive = strategy.open(path);
            System.out.println("Архів відкрито!");
            return currentArchive;
        } catch (Exception e) {
            System.err.println("Помилка: " + e.getMessage());
            return null;
        }
    }


    public void extractFiles(String destination, List<String> items) {
        if (currentArchive == null) {
            throw new IllegalStateException("Архів не відкритий");
        }

        System.out.println("\nArchiveProcessor: витягування до " + destination);

        strategy.extract(currentArchive, items, destination);

        System.out.println("Файли витягнуто");
    }

    public void showInfo() {
        if (currentArchive == null) {
            System.out.println("Немає відкритого архіву");
            return;
        }

        System.out.println("\nІНФОРМАЦІЯ:");
        System.out.println(" Шлях: " + currentArchive.getFilePath());
        System.out.println(" Формат: " + currentArchive.getFormat());
        System.out.println(" Розмір: " + currentArchive.getTotalSize() + " байт");
        System.out.println(" Strategy: " + strategy.getClass().getSimpleName());
    }


    public boolean testArchive() {
        if (currentArchive == null) {
            throw new IllegalStateException("Архів не відкритий");
        }

        System.out.println("\nArchiveProcessor: тестування...");
        boolean result = strategy.test(currentArchive);
        System.out.println(result ? " Тест OK" : "  Тест мимо");
        return result;
    }
}
