package repository;

import model.ArchiveInfo;
import repository.IArchiveInfoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArchiveInfoRepositoryImpl implements IArchiveInfoRepository {
    private final List<ArchiveInfo> database = new ArrayList<>();

    @Override
    public Optional<ArchiveInfo> findByPath(String path) {
        return database.stream()
                .filter(info -> info.getFilePath().equals(path))
                .findFirst();
    }


    @Override
    public void upsert(ArchiveInfo info) {
        System.out.println("Імітація збереження/оновлення в БД для: " + info.getFilePath());
        Optional<ArchiveInfo> existing = findByPath(info.getFilePath());
        if (existing.isPresent()) {
            database.remove(existing.get());
        }
        database.add(info);
        System.out.println("Запис успішно збережено. В базі зараз " + database.size() + " записів");
    }

    @Override
    public List<ArchiveInfo> listRecent(int limit) {
        if (database.size() <= limit) {
            return new ArrayList<>(database);
        }
        return new ArrayList<>(database.subList(database.size() - limit, database.size()));
    }
}
