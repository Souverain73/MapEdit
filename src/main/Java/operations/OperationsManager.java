package operations;

import java.util.LinkedList;

/**
 * Created by Souverain73 on 21.06.2017.
 */
public class OperationsManager {
    LinkedList<Operation> operations = new LinkedList();
    int lastOp = -1;

    public void apply(Operation op){
        operations.push(op);
        op.apply();
    }

    public void undo(){
        if (operations.size()>0)
            operations.pop().undo();
    }
}
