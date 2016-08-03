package jwm.ir.service;

/**
 * Created by Jeff on 2016-08-01.
 */
public class Operation {

    public enum OperationType {Save, Update, Delete};
    private final OperationType operationType;
    private final Object entity;

    public OperationType getOperationType() {
        return operationType;
    }

    public Object getEntity() {
        return entity;
    }

    public Operation(OperationType operationType, Object entity) {

        this.operationType = operationType;
        this.entity = entity;
    }
}
