package repository;

import model.OperationLog;

import java.util.List;

public interface IOperationLogRepository {
    long add(OperationLog log);
    List<OperationLog> listByArchive(long archiveId, int limit);
}
