package processingEmulator;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RemoveMoneyActionCreator {

    public static boolean createAndWaitExecution(int id, double money){
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        Condition requestCompleted = lock.newCondition();
        RemoveMoneyAction currentAction = new RemoveMoneyAction(lock, requestCompleted, id, money);
        ActionsQueue.getInstance().addAction(currentAction);
        try {
             requestCompleted.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentAction.isSuccess();

    }
}
