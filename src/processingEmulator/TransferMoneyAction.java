package processingEmulator;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TransferMoneyAction extends Action{

    private int from;
    private int to;
    private double money;

    public TransferMoneyAction(ReentrantLock creatorsThreadLock, Condition requestCompleted,
                               int from, int to, double money){
        super(creatorsThreadLock, requestCompleted);
        this.from = from;
        this.to = to;
        this.money = money;
    }

    @Override
    public void action() {
        lock();
        Account fromAccount = AccountsMap.getInstance().get(from);
        Account toAccount = AccountsMap.getInstance().get(to);
        if(money < 0 || fromAccount == null || toAccount == null || fromAccount.getMoney() < money){
            requestCompleted(false);
        } else {
            fromAccount.setMoney(fromAccount.getMoney() - money);
            toAccount.setMoney(toAccount.getMoney() + money);
            requestCompleted(true);
        }
    }
}
