package repository;

import model.OperationDetail;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OperationDetailRepositoryImpl implements IOperationDetailRepository {
    private final List<OperationDetail> details = new ArrayList<>();
    private long nextId = 1;

    @Override
    public void add(OperationDetail detail) {
        if (detail.getDetailID() == 0) {
            detail.setDetailID(nextId++);
        }
        details.add(detail);
    }

    @Override
    public void addAll(List<OperationDetail> detailList) {
        for (OperationDetail detail : detailList) {
            add(detail);
        }
        System.out.println("Додано " + detailList.size() + " деталей операції");
    }

    @Override
    public List<OperationDetail> listByOperation(long operationId) {
        System.out.println("Пошук деталей для операції з ID: " + operationId);
        return details.stream()
                .filter(d -> d.getLogID() == operationId)
                .collect(Collectors.toList());
    }
}