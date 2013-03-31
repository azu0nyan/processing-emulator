package processingEmulator.test;


import processingEmulator.*;

import java.util.Random;

public class TestHaoticUser extends Thread{
    HighLoadTest test;
    int rps;//requests per second
    long lifeTime;
    long createTime;
    Random r;
    int actionsCounter = 0;
    int mode;//1 add/remove/transfer money
             //2 add/remove/transfer money add/remove accounts rarely
             //3 add/remove/transfer money add/remove accounts

    public TestHaoticUser(HighLoadTest test, long lifeTime, int rps, int mode){
        this.rps = rps;
        this.test = test;
        r = new Random();
        this.lifeTime = lifeTime;
        this.mode = mode;
        createTime = System.currentTimeMillis();
    }
    @Override
    public void run() {
        while (System.currentTimeMillis() - createTime < lifeTime && !test.stopTest){
            actionsCounter++;
            doHaoticAction();
            try {
                Thread.currentThread().sleep(1000 / Math.max(rps, 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void doHaoticAction() {
        int rand = 0;
        if(mode == 1){
            rand = r.nextInt(3);
        } else if(mode == 2){
            if(r.nextInt(100) == 0){
                rand = r.nextInt(2) + 3;
            } else {
                rand = r.nextInt(3);
            }

        } else {
            rand = r.nextInt(5);
        }
        switch (rand){
            case 0:{
                removeMoney();
                break;
            }
            case 1:{
                addMoney();
                break;
            }
            case 2:{
                transferMoney();
                break;
            }
            case 3:{
                addAccount();
                break;
            }
            case 4:{
                removeAccount();
                break;
            }

        }
    }
    public void addMoney(){
        int id = test.getRandomExistingId();
        double money = r.nextInt(100);
        //ouble money = r.nextInt(Math.max((int)test.correctAccounts[id].getBalance(),1));
        if(AddMoneyActionCreator.createAndWaitExecution(id, money)){
            test.correctAccounts[id].lock.lock();
            test.correctAccounts[id].modifyBalanse(money);
            test.correctAccounts[id].lock.unlock();
            /*System.out.println(actionsCounter + " addMoney id = " + id + " money = " + money +
                   " balance = " + test.correctAccounts[id].getBalance());         */
            test.completedRequests++;
        } else {
            test.failedRequests++;
            //System.out.println(actionsCounter + " addMoney id = " + id + " money = " + money + " FAIL");
        }
    }
    public void removeMoney(){
        int id = test.getRandomExistingId();
        //double money = r.nextInt(Math.max((int)test.correctAccounts[id].getBalance()));
        double money = r.nextInt(100);
        if(RemoveMoneyActionCreator.createAndWaitExecution(id, money)){
            test.correctAccounts[id].lock.lock();
            test.correctAccounts[id].modifyBalanse(-money);
            test.correctAccounts[id].lock.unlock();
            /*System.out.println(actionsCounter + " removeMoney id = " + id + " money = " + money +
                    " balance = " + test.correctAccounts[id].getBalance());    */
            test.completedRequests++;
        } else {
            //System.out.println(actionsCounter + " removeMoney id = " + id + " money = " + money + " FAIL");
            test.failedRequests++;
        }
    }
    public void transferMoney(){
        int from = test.getRandomExistingId();
        int to = test.getRandomExistingId();
        //double money = r.nextInt((int)test.correctAccounts[from].getBalance());
        double money = r.nextInt(100);
        if(TransferMoneyActionCreator.createAndWaitExecution(from, to, money)){
            test.correctAccounts[from].lock.lock();
            test.correctAccounts[from].modifyBalanse(-money);
            test.correctAccounts[from].lock.unlock();
            test.correctAccounts[to].lock.lock();
            test.correctAccounts[to].modifyBalanse(money);
            test.correctAccounts[to].lock.unlock();
            /*System.out.println(actionsCounter + " transferMoney from = " + from + " to = "+ to  + " money = " + money +
                    " fromBalance = " + test.correctAccounts[from].getBalance() +
                    " toBalance = " + test.correctAccounts[to].getBalance() );    */
            test.completedRequests++;
        } else {
            //System.out.println(actionsCounter + " transferMoney from = " + from + " to = "+ to  + " money = " + money + " FAIL");
            test.failedRequests++;
        }
    }
    public void addAccount(){
        int id = test.getRandomNonExistingId();
        if(AddAccountActionCreator.createAndExecute(id)){
            test.correctAccounts[id].lock.lock();
            test.correctAccounts[id].add();
            test.correctAccounts[id].lock.unlock();
            test.accountsCount++;
            //System.out.println(actionsCounter + " addAccount id = " + id);
            test.completedRequests++;
        } else {
            //System.out.println(actionsCounter + " addAccount id = " + id + " FAIL");
            test.failedRequests++;
        }
    }
    public void removeAccount(){
        int id = test.getRandomExistingId();
        if(RemoveAccountActionCreator.createAndWaitExecution(id)){
            test.correctAccounts[id].lock.lock();
            test.correctAccounts[id].remove();
            test.correctAccounts[id].lock.unlock();
            test.accountsCount--;
            //System.out.println(actionsCounter + " removeAccount id = " + id);
            test.completedRequests++;
        } else {
            //System.out.println(actionsCounter + " removeAccount id = " + id + " FAIL");
            test.failedRequests++;
        }
    }
}
