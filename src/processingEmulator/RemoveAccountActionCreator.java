package processingEmulator;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RemoveAccountActionCreator {

    public static boolean createAndWaitExecution(int id){
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        Condition requestCompleted = lock.newCondition();
        RemoveAccountAction currentAction = new RemoveAccountAction(lock, requestCompleted, id);
        ActionsQueue.getInstance().addAction(currentAction);
        try {
            requestCompleted.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentAction.isSuccess();

    }
}
