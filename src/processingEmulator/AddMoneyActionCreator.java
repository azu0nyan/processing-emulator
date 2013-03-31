package processingEmulator;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AddMoneyActionCreator {

    public static boolean createAndWaitExecution(int id, double money){
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        Condition requestCompleted = lock.newCondition();
        AddMoneyAction currentAction = new AddMoneyAction(lock, requestCompleted, id, money);
        ActionsQueue.getInstance().addAction(currentAction);
        try {
            requestCompleted.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentAction.isSuccess();

    }
}
