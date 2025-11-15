package repository;

import model.OperationDetail;
import java.util.List;

public interface IOperationDetailRepository {
    void add(OperationDetail detail);
    void addAll(List<OperationDetail> details);
    List<OperationDetail> listByOperation(long operationId);
}