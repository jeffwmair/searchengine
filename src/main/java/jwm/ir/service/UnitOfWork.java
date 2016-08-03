package jwm.ir.service;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 2016-08-01.
 */
public class UnitOfWork {

    private final Session session;
    private final List<Operation> operations;
    public UnitOfWork(Session session) {
        this.session = session;
        this.operations = new ArrayList<>();
    }

    public void add(Object entity, Operation.OperationType operationType) {
        this.operations.add(new Operation(operationType, entity));
    }

    public Session getSession() {
        return session;
    }

    /**
     * Persist the unit of work.  That is, call saves, updates, deletes.
     */
    public void persist() {
        for(Operation op : operations) {
            Object entity = op.getEntity();
            Operation.OperationType opType = op.getOperationType();
            if (opType == Operation.OperationType.Save) {
                session.save(entity);
            }
            else if (opType == Operation.OperationType.Update) {
                session.update(entity);
            }
            else if (opType == Operation.OperationType.Delete) {
                session.delete(entity);
            }
            else {
                throw new RuntimeException("Unknown operation type:"+opType);
            }
        }
    }
}
