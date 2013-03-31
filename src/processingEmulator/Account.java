package processingEmulator;

import java.util.concurrent.LinkedBlockingDeque;

public class Account {

    private int id;
    private volatile double money;

    public Account(int id, double money){
        this.id = id;
        this.money = money;
    }

    public int getId() {
        return id;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money){
        this.money = money;
    }

}

