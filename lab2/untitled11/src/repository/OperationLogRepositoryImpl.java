package repository;

import model.OperationLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OperationLogRepositoryImpl implements IOperationLogRepository {
    private final List<OperationLog> logs = new ArrayList<>();
    private long seq = 1;
    @Override
    public void add(OperationLog log) {
        if (log.getId() == 0) {
            log.setId(seq++);
        }
        logs.add(log);
    }

    @Override
    public List<OperationLog> listByArchive(long archiveId, int limit) {
        return logs.stream()
                .filter(l -> l.getArchiveInfoId() == archiveId)
                .sorted(Comparator.comparing(OperationLog::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
