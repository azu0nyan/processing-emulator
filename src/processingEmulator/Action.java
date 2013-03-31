package processingEmulator;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Action {

    private boolean success = false;
    protected ReentrantLock creatorsThreadLock;
    private Condition requestCompleted;

    public Action(ReentrantLock creatorsThreadLock, Condition requestCompleted){
        this.creatorsThreadLock = creatorsThreadLock;
        this.requestCompleted = requestCompleted;
    }

    public boolean isLastAction(){//можно ли после этого действия делать новые действия c аккаунтом
        return false;
    }

    protected void requestCompleted(boolean success){
        this.success = success;
        requestCompleted.signal();
        unLock();
    }

    protected void lock(){
        creatorsThreadLock.lock();
    }
    protected void unLock(){
        creatorsThreadLock.unlock();
    }
    public boolean isSuccess(){
        return success;
    }

    public abstract void action();


}
