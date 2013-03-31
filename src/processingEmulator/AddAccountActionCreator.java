package processingEmulator;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AddAccountActionCreator {
    public static boolean createAndExecute(int id){
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        Condition requestCompleted = lock.newCondition();
        AddAccountAction currentAction = new AddAccountAction(lock, requestCompleted, id);
        ActionsQueue.getInstance().addAction(currentAction);
        try {
            requestCompleted.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentAction.isSuccess();
    }
}
