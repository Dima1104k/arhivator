package repository;

import model.ArchiveInfo;

import java.util.List;
import java.util.Optional;

public interface IArchiveInfoRepository {
    Optional<ArchiveInfo> findByPath(String path);
    void upsert(ArchiveInfo info);
    List<ArchiveInfo> listRecent(int limit);
    void delete(long id);
}
