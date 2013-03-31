package processingEmulator.test;

import java.util.concurrent.locks.ReentrantLock;

public class TestAccountLocker {

    public ReentrantLock lock;
    private volatile double balance;//нужен ли тут volatile
    private boolean isAlive;

    public TestAccountLocker(boolean isAlive, double balance){
        lock = new ReentrantLock(true);
        this.isAlive = isAlive;
        this.balance = balance;
    }
    boolean  isAlive(){
        return isAlive;
    }
    double getBalance(){
        return balance;
    }
    public void remove(){
        isAlive = false;
        balance = 0;
    }
    public void add(){
        isAlive = true;
        balance = 0;
    }
    public void modifyBalanse(double money){
        balance += money;
    }

}
