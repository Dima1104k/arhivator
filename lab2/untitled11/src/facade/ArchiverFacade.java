package facade;
import model.*;
import repository.IArchiveBookmarkRepository;
import repository.IArchiveInfoRepository;
import repository.IOperationDetailRepository;
import repository.IOperationLogRepository;
import strategy.ArchiverStrategyFactory;
import strategy.IArchiverStrategy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ArchiverFacade {
    private final IArchiveInfoRepository archiveRepo;
    private final IOperationLogRepository logRepo;

    private Archive currentOpenArchive;
    private IArchiverStrategy currentStrategy;

    private final IArchiveBookmarkRepository bookmarkRepo;
    private final IOperationDetailRepository detailRepo;
    public ArchiverFacade(IArchiveInfoRepository archiveRepo, IOperationLogRepository logRepo,
                          IArchiveBookmarkRepository bookmarkRepo, IOperationDetailRepository detailRepo) {
        this.archiveRepo = archiveRepo;
        this.logRepo = logRepo;
        this.bookmarkRepo = bookmarkRepo;
        this.detailRepo = detailRepo;
    }

    public Archive create(String path, ArchiveFormat format, List<String> files) {
        try {
            currentStrategy = ArchiverStrategyFactory.createStrategy(format);
            currentOpenArchive = currentStrategy.create(path, files);
            ArchiveInfo newInfo = new ArchiveInfo(0L, path, format.name(), currentOpenArchive.getTotalSize(), LocalDateTime.now(), LocalDateTime.now());
            archiveRepo.upsert(newInfo);
            log(OperationType.CREATE, OperationStatus.SUCCESS, "Створено архів", path);
            return currentOpenArchive;
        } catch (Exception e) {
            log(OperationType.CREATE, OperationStatus.FAILURE, e.getMessage(), path);
            throw new RuntimeException("Помилка створення архіву", e);
        }
    }

    public Archive open(String path) {
        try {
            currentStrategy = ArchiverStrategyFactory.createForPath(path);
            currentOpenArchive = currentStrategy.open(path);
            ArchiveInfo info = archiveRepo.findByPath(path)
                    .orElse(new ArchiveInfo(0L, path, currentOpenArchive.getFormat().name(), currentOpenArchive.getTotalSize(), LocalDateTime.now(), LocalDateTime.now()));
            info.setLastAccessedAt(LocalDateTime.now());
            archiveRepo.upsert(info);
            log(OperationType.OPEN, OperationStatus.SUCCESS, "Відкрито архів", path);
            return currentOpenArchive;
        } catch (Exception e) {
            log(OperationType.OPEN, OperationStatus.FAILURE, e.getMessage(), path);
            throw new RuntimeException("Помилка відкриття архіву", e);
        }
    }

    public void addFilesToCurrentArchive(List<String> files) {
        ensureArchiveOpen();
        try {
            currentStrategy.add(currentOpenArchive, files);
            log(OperationType.ADD, OperationStatus.SUCCESS, "Додано файли", currentOpenArchive.getFilePath());
        } catch (Exception e) {
            log(OperationType.ADD, OperationStatus.FAILURE, e.getMessage(), currentOpenArchive.getFilePath());
            throw new RuntimeException(e);
        }
    }

    public void extractFromCurrentArchive(List<String> items, String destination) {
        ensureArchiveOpen();
        OperationLog logEntry = startLog(OperationType.EXTRACT);
        List<OperationDetail> details = new ArrayList<>();

        try {
            currentStrategy.extract(currentOpenArchive, items, destination);
            details.add(new OperationDetail(0, logEntry.getId(), "file1.txt", OperationType.EXTRACT, OperationStatus.SUCCESS, "OK"));
            details.add(new OperationDetail(0, logEntry.getId(), "file2.txt", OperationType.EXTRACT, OperationStatus.FAILURE, "Access denied"));

            finishLog(logEntry, OperationStatus.PARTIAL_SUCCESS, "Виконано розпакування з помилками", details);
        } catch (Exception e) {
            finishLog(logEntry, OperationStatus.FAILURE, e.getMessage(), null);
            throw new RuntimeException(e);
        }
    }
    public void deleteFromCurrentArchive(List<String> itemPaths) {
        ensureArchiveOpen();
        try {
            currentStrategy.delete(currentOpenArchive, itemPaths);
            log(OperationType.DELETE, OperationStatus.SUCCESS, "Видалено елементи", currentOpenArchive.getFilePath());
        } catch (Exception e) {
            log(OperationType.DELETE, OperationStatus.FAILURE, e.getMessage(), currentOpenArchive.getFilePath());
            throw new RuntimeException(e);
        }
    }
    public boolean testCurrentArchive() {
        ensureArchiveOpen();
        try {
            boolean result = currentStrategy.test(currentOpenArchive);
            log(OperationType.TEST, OperationStatus.SUCCESS, "Тест завершено: " + (result ? "OK" : "Помилки"), currentOpenArchive.getFilePath());
            return result;
        } catch (Exception e) {
            log(OperationType.TEST, OperationStatus.FAILURE, e.getMessage(), currentOpenArchive.getFilePath());
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> checksumCurrentArchive(List<String> itemPaths, String algorithm) {
        ensureArchiveOpen();
        try {
            Map<String, String> checksums = currentStrategy.checksum(currentOpenArchive, itemPaths, algorithm);
            log(OperationType.CHECKSUM, OperationStatus.SUCCESS, "Розраховано контрольні суми", currentOpenArchive.getFilePath());
            return checksums;
        } catch (Exception e) {
            log(OperationType.CHECKSUM, OperationStatus.FAILURE, e.getMessage(), currentOpenArchive.getFilePath());
            throw new RuntimeException(e);
        }
    }
    public List<String> splitCurrentArchive(int partSizeMB) {
        ensureArchiveOpen();
        try {
            List<String> parts = currentStrategy.split(currentOpenArchive, partSizeMB);
            log(OperationType.SPLIT, OperationStatus.SUCCESS, "Архів розбито на частини", currentOpenArchive.getFilePath());
            return parts;
        } catch (Exception e) {
            log(OperationType.SPLIT, OperationStatus.FAILURE, e.getMessage(), currentOpenArchive.getFilePath());
            throw new RuntimeException(e);
        }
    }

    public String joinArchiveFromParts(String firstPartPath) {
        try {
            IArchiverStrategy strategy = ArchiverStrategyFactory.createForPath(firstPartPath);
            String resultPath = strategy.join(firstPartPath);

            ArchiveFormat format = ArchiveFormat.fromPath(resultPath);
            ArchiveInfo newInfo = new ArchiveInfo(0L, resultPath, format.name(), 0L, LocalDateTime.now(), LocalDateTime.now());
            archiveRepo.upsert(newInfo);

            log(OperationType.JOIN, OperationStatus.SUCCESS, "Частини успішно з'єднано в " + resultPath, resultPath);
            return resultPath;
        } catch (Exception e) {
            log(OperationType.JOIN, OperationStatus.FAILURE, e.getMessage(), firstPartPath);
            throw new RuntimeException(e);
        }
    }
    public void addBookmarkToCurrentArchive(String itemPath, String displayName, String note) {
        ensureArchiveOpen();
        long archiveId = archiveRepo.findByPath(currentOpenArchive.getFilePath())
                .map(archiveInfo -> archiveInfo.getId()).orElseThrow();

        ArchiveBookmark bookmark = new ArchiveBookmark(0, archiveId, itemPath, displayName, false, note, LocalDateTime.now());
        bookmarkRepo.add(bookmark);
        System.out.println("Додано закладку на " + itemPath);
    }

    public List<ArchiveBookmark> getBookmarksForCurrentArchive() {
        ensureArchiveOpen();
        long archiveId = archiveRepo.findByPath(currentOpenArchive.getFilePath())
                .map(archiveInfo -> archiveInfo.getId()).orElse(0L);
        return bookmarkRepo.listByArchive(archiveId);
    }

    public Archive getCurrentOpenArchive() {
        return currentOpenArchive;
    }

    public List<ArchiveInfo> listRecentArchives(int limit) {
        return archiveRepo.listRecent(limit);
    }
    public List<OperationLog> getOperationLogsForCurrentArchive(int limit) {
        ensureArchiveOpen();
        long archiveId = archiveRepo.findByPath(currentOpenArchive.getFilePath())
                .map(archiveInfo -> archiveInfo.getId()).orElse(0L);
        return logRepo.listByArchive(archiveId, limit);
    }
    public List<OperationDetail> getDetailsForOperation(long operationId) {
        System.out.println("Запит деталей для операції з ID: " + operationId);
        return detailRepo.listByOperation(operationId);
    }

    private void log(OperationType type, OperationStatus status, String message, String archivePath) {
        long archiveId = archiveRepo.findByPath(archivePath)
                .map(archiveInfo -> archiveInfo.getId()).orElse(0L);
        logRepo.add(new OperationLog(archiveId, type, status, message));
    }

    private OperationLog startLog(OperationType type) {
        long archiveId = archiveRepo.findByPath(currentOpenArchive.getFilePath())
                .map(archiveInfo -> archiveInfo.getId()).orElse(0L);

        OperationLog logEntry = new OperationLog(archiveId, type, OperationStatus.PARTIAL, "In progress...");
        logRepo.add(logEntry);
        return logEntry;
    }

    private void finishLog(OperationLog logEntry, OperationStatus finalStatus, String message, List<OperationDetail> details) {
        logEntry.setStatus(finalStatus);
        logEntry.setMessage(message);
        // logRepo.update(logEntry);

        if (details != null && !details.isEmpty()) {
            detailRepo.addAll(details);
        }
    }

    private void ensureArchiveOpen() {
        if (currentOpenArchive == null) {
            throw new IllegalStateException("Жоден архів не відкрито");
        }
    }
}
