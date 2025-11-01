import facade.ArchiverFacade;
import repository.*;
import model.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        IArchiveInfoRepository archiveRepo = new PostgresArchiveInfoRepository();
        IOperationLogRepository logRepo = new PostgresOperationLogRepository();
        IOperationDetailRepository detailRepo = new PostgresOperationDetailRepository();
        IArchiveBookmarkRepository bookmarkRepo = new PostgresArchiveBookmarkRepository();

        ArchiverFacade facade = new ArchiverFacade(archiveRepo, logRepo, bookmarkRepo, detailRepo);
        try {
            System.out.println("Створюємо архів...");
            List<String> files = Arrays.asList("file1.txt", "file2.txt", "file3.txt");
            Archive archive = facade.create("test.zip", ArchiveFormat.ZIP, files);

            System.out.println(" Архів створено: " + archive.getFilePath());
            System.out.println("  Формат: " + archive.getFormat());
            System.out.println("  Розмір: " + archive.getTotalSize() + " байт");

            System.out.println("\nПеревіряємо БД...");
            List<ArchiveInfo> recentArchives = facade.listRecentArchives(10);
            System.out.println("Знайдено архівів в БД: " + recentArchives.size());

            for (ArchiveInfo info : recentArchives) {
                System.out.println("  - " + info.getFilePath() + " (" + info.getFormat() + ")");
            }

        } catch (Exception e) {
            System.err.println("Помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}