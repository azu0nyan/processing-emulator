package processingEmulator;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TransferMoneyActionCreator {

    public static boolean createAndWaitExecution(int from, int to, double money){
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        Condition requestCompleted = lock.newCondition();
        TransferMoneyAction currentAction = new TransferMoneyAction(lock, requestCompleted, from, to, money);
        ActionsQueue.getInstance().addAction(currentAction);
        try {
            requestCompleted.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentAction.isSuccess();
    }

}
