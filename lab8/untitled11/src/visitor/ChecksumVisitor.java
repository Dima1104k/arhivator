package visitor;

import model.ArchivedFile;
import model.ArchivedFolder;
import model.ArchiveItem;

public class ChecksumVisitor implements ArchiveVisitor {

    private long totalChecksum = 0;
    private int filesProcessed = 0;

    @Override
    public void visit(ArchivedFile file) {
        System.out.println("ChecksumVisitor: Обробка файлу " + file.getName());

        try {
            // розпасисо чексум як число
            long fileChecksum = Long.parseLong(file.getChecksum());
            totalChecksum += fileChecksum;
        } catch (NumberFormatException e) {
            totalChecksum += file.getOriginalSize();
        }
        filesProcessed++;
    }

    @Override
    public void visit(ArchivedFolder folder) {
        System.out.println("ChecksumVisitor: Заходимо в папку " + folder.getName());

        // рекурсивний обхід дочірніх елементів
        for (ArchiveItem item : folder.getChildren()) {
            item.accept(this);
        }
    }

    public long getTotalChecksum() {
        return totalChecksum;
    }


    public int getFilesProcessed() {
        return filesProcessed;
    }
}