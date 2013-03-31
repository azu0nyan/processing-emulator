package processingEmulator;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RemoveAccountAction extends Action{

    int id;

    public RemoveAccountAction(ReentrantLock creatorsThreadLock, Condition requestCompleted, int id){
        super(creatorsThreadLock, requestCompleted);
        this.id = id;

    }

    @Override
    public boolean isLastAction(){
        return true;
    }

    @Override
    public void action(){
        lock();
        if(AccountsMap.getInstance().removeAccount(id)){
            requestCompleted(true);
        } else {
            requestCompleted(false);
        }
    }
}
