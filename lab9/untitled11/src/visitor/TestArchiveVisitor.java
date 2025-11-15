package visitor;

import model.ArchivedFile;
import model.ArchivedFolder;
import model.ArchiveItem;

import java.util.ArrayList;
import java.util.List;


public class TestArchiveVisitor implements ArchiveVisitor {

    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();


    @Override
    public void visit(ArchivedFile file) {
        System.out.println("TestArchiveVisitor: Тестування файлу " + file.getName());


        // перевірка 1 чи файл нульового розміру
        if (file.getOriginalSize() == 0) {
            warnings.add("ПОПЕРЕДЖЕННЯ: Файл '" + file.getName() + "' має нульовий розмір");
        }

        // перевірка 2 відсутність checksum
        if (file.getChecksum() == null || file.getChecksum().isEmpty()) {
            warnings.add("ПОПЕРЕДЖЕННЯ: Файл '" + file.getName() + "' не має контрольної суми");
        }

        // перевірка 3 стиснений розмір більший за оригінальний
        if (file.getCompressedSize() > file.getOriginalSize()) {
            errors.add("ПОМИЛКА: Файл '" + file.getName() + "' має стиснений розмір більший за оригінальний!");
        }
    }

    @Override
    public void visit(ArchivedFolder folder) {
        System.out.println("TestArchiveVisitor: Перевірка папки " + folder.getName());

        // рекурсивно тестуємо всіх дітей папки
        for (ArchiveItem item : folder.getChildren()) {
            item.accept(this);
        }
        // перевірка папка не має бути порожньою
        if (folder.getChildren().isEmpty() && !folder.getName().equals("/")) {
            warnings.add("ІНФО: Папка '" + folder.getName() + "' порожня");
        }
    }

    public boolean hasIssues() {
        return !errors.isEmpty() || !warnings.isEmpty();
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Звіт тестування ===\n");

        if (!hasIssues()) {
            sb.append("Помилок не виявлено. Архів виглядає цілісним\n");
        } else {
            if (!errors.isEmpty()) {
                sb.append("\nКРИТИЧНІ ПОМИЛКИ:\n");
                for (String err : errors) {
                    sb.append("  • ").append(err).append("\n");
                }
            }

            if (!warnings.isEmpty()) {
                sb.append("\nПОПЕРЕДЖЕННЯ:\n");
                for (String warn : warnings) {
                    sb.append("  • ").append(warn).append("\n");
                }
            }
        }

        return sb.toString();
    }


}