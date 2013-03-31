package processingEmulator;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AddMoneyAction extends Action{


    private int id;
    private double money;


    public AddMoneyAction(ReentrantLock creatorsThreadLock, Condition requestCompleted, int id, double money){
        super(creatorsThreadLock, requestCompleted);
        this.id = id;
        this.money = money;
    }

    @Override
    public void action(){
        lock();
        Account tmp = AccountsMap.getInstance().get(id);
        if(money < 0 || tmp == null){
            requestCompleted(false);
        } else{
            tmp.setMoney(tmp.getMoney() + money);
            requestCompleted(true);
        }
    }
}
