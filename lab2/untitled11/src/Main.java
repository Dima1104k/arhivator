import facade.ArchiverFacade;
import model.*;
import repository.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        IArchiveInfoRepository archiveRepo = new ArchiveInfoRepositoryImpl();
        IOperationLogRepository logRepo = new OperationLogRepositoryImpl();
        IArchiveBookmarkRepository bookmarkRepo = new ArchiveBookmarkRepositoryImpl();
        IOperationDetailRepository detailRepo = new OperationDetailRepositoryImpl();
        ArchiverFacade facade = new ArchiverFacade(archiveRepo, logRepo, bookmarkRepo, detailRepo);


        System.out.println("Створення архіву");
        facade.create("C:\\docs.zip", ArchiveFormat.ZIP, List.of("file1.txt", "file2.txt"));

        System.out.println("\nВідкриття архіву");
        Archive opened = facade.open("C:\\docs.zip");
        System.out.println("Відкрито: " + opened.getFilePath());

        System.out.println("\nТестування архіву");
        boolean result = facade.testCurrentArchive();
        System.out.println("Результат тесту: " + result);
        facade.addFilesToCurrentArchive(List.of("new_file.txt"));
        System.out.println("\nВиконання операції з деталізацією");
        facade.extractFromCurrentArchive(List.of("file1.txt", "file2.txt"), "C:\\output");
        List<ArchiveInfo> recentArchives = facade.listRecentArchives(5);
        System.out.println("Список недавніх архівів:");
        for (ArchiveInfo info : recentArchives) {
            System.out.println(info.getFilePath() + " (останній доступ: " + info.getLastAccessedAt() + ")");
        }
        System.out.println("\nДодавання та перегляд закладок");
        facade.addBookmarkToCurrentArchive("file1.txt", "Важливий документ", "Перевірити перед відправкою");

        List<ArchiveBookmark> bookmarks = facade.getBookmarksForCurrentArchive();
        System.out.println("Закладки для поточного архіву (кількість): " + bookmarks.size());
        System.out.println("Історія операцій для поточного відкритого архіву (" + facade.getCurrentOpenArchive().getFilePath() + "):");
        List<OperationLog> logs = facade.getOperationLogsForCurrentArchive(10);
        for (OperationLog log : logs) {
            System.out.println( log.getTimestamp() + " | " + log.getOperationType() + " | " + log.getStatus() + " | " + log.getMessage());
        }
    }
}