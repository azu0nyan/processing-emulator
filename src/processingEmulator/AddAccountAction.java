package processingEmulator;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AddAccountAction extends Action{

    private int id;

    public AddAccountAction(ReentrantLock creatorsThreadLock, Condition requestCompleted, int id){
        super(creatorsThreadLock, requestCompleted);
        this.id = id;
    }

    public void action(){
        lock();
        if(AccountsMap.getInstance().addAccount(id, 0)){
            requestCompleted(true);
        } else {
            requestCompleted(false);
        }

    }
}
