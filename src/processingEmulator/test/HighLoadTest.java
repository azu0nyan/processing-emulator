package processingEmulator.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import processingEmulator.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HighLoadTest {
    volatile boolean stopTest = false;
    int maxAccountId = 100;
    volatile int accountsCount = 0;
    int startAccounts = 100;
    int usersCount = 100;// > 100
    long userStartDelay = 10;
    int rps = 5;//запросов/сек на юзера
    volatile int failedRequests = 0;
    volatile int completedRequests = 0;
    //TreeMap<Integer, Double> correctAccounts;
    TestAccountLocker [] correctAccounts;
    Random r;

    @Before
    public void initTest() throws Exception {
        AccountsMap.getInstance().stopUpdateDb();
        Thread.currentThread().sleep(1000);
        r = new Random();
        failedRequests = 0;
        completedRequests = 0;
        for(int i = 0; i < startAccounts; i++){
            RemoveAccountActionCreator.createAndWaitExecution(i);
            AddAccountActionCreator.createAndExecute(i);
            AddMoneyActionCreator.createAndWaitExecution(i, 100);
        }
        for(int i = startAccounts; i < maxAccountId; i++){
            RemoveAccountActionCreator.createAndWaitExecution(i);
        }
        AccountsMap.getInstance().updateDB();
        /*for(int i = 0; i < maxAccountId; i++){
            System.out.print(i + " " + AccountsMap.getInstance().get(i).getMoney() + ",");
        }   */
        System.out.println();
    }

    @Test
    public void testAddingRemovingTransferMoney() throws Exception {
        //Init
        System.out.println(" ");
        System.out.println("Test 1");
        readCorrectAccounts();

        //test
        Thread user = null;
        for(int i = 0; i < usersCount; i++){
            user = new TestHaoticUser(this, 5000, rps, 1);
            user.start();
            Thread.currentThread().sleep(userStartDelay);
        }
        Thread.currentThread().sleep(7000);
        stopTest = true;

        //res
        AccountsMap.getInstance().updateDB();
        boolean isEquals = true;
        ResultSet rs = MySQLConnection.getInstance().executeQuery("SELECT * FROM " + Settings.tableName);
        try {
            while(rs.next()){
                Integer id = rs.getInt("id");
                Double money = rs.getDouble("value");
                if(id < maxAccountId && money.doubleValue() != correctAccounts[id].getBalance()){
                    System.out.println("Wrong balance id = " + id + " dbBalance = " + money.doubleValue() + " localeBalance = " +
                            correctAccounts[id].getBalance());
                    isEquals = false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while checking test result!");
            e.printStackTrace();
        }
        assertTrue("HighLoad test failed local result not equal to DB", isEquals && checkPositive(accountsToTreeMap()));
    }

    @Test
    public void testAddingRemovingTransferMoneyAddingRemovingAccounts() throws Exception {
        //Init
        System.out.println(" ");
        System.out.println("Test 2");
        readCorrectAccounts();

        //test

        Thread user = null;
        for(int i = 0; i < usersCount; i++){
            user = new TestHaoticUser(this, 5000, rps, 3);
            user.start();
            Thread.currentThread().sleep(userStartDelay);
        }
        Thread.currentThread().sleep(7000);
        stopTest = true;

        //res
        AccountsMap.getInstance().updateDB();
        ResultSet rs = MySQLConnection.getInstance().executeQuery("SELECT * FROM " + Settings.tableName);
        TreeMap<Integer, Double> dbValues = new TreeMap<Integer, Double>();
        //int currentId = nextId(0);//cледующее валидно т.к данные идут по возростанию id
        try {
            while(rs.next()){
                Integer id = rs.getInt("id");
                Double money = rs.getDouble("value");
                if(id < maxAccountId){
                    dbValues.put(id, money);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while loading test accounts!");
            e.printStackTrace();
        }
        assertTrue("HighLoad test failed local result not equal to DB",
                accountsToTreeMap().equals(dbValues) && checkPositive(accountsToTreeMap()));
    }
    private TreeMap<Integer, Double> accountsToTreeMap(){
        TreeMap<Integer, Double> res = new TreeMap<Integer, Double>();
        for(int i = 0; i < maxAccountId; i++){
            if(correctAccounts[i].isAlive()){
                res.put(i, correctAccounts[i].getBalance());
            }
        }
        return res;
    }
    private boolean checkPositive(TreeMap<Integer, Double> map){
        LinkedList<Double> l = new LinkedList<Double>(map.values());
        for(Double d : l){
            if(d < 0){
                return  false;
            }
        }
        return true;
    }
    private int nextId(int x) {
        for(int i = x; i < maxAccountId; i++){
            if(correctAccounts[x].isAlive()){
                return x;
            }
        }
        return -1;
    }

    @After
    public void after(){
        System.out.println("Requests");
        System.out.println("Failed:" + failedRequests );
        System.out.println("Completed:" + completedRequests);
        /*System.out.println("AddAccountThreads:" + MultiThreadExecuteAddAccount.getInstance().getExecutorsCount());
        System.out.println("RemoveAccountThreads:" + MultiThreadExecuteRemoveAccount.getInstance().getExecutorsCount());
        System.out.println("SelectAccountThreads:" + MultiThreadExecuteSelectAccount.getInstance().getExecutorsCount());
        System.out.println("UpdateMoneyThreads:" + MultiThreadExecuteUpdateMoney.getInstance().getExecutorsCount());   */
        printCorrectBalance();
        printMapBalance();
        printDBBalance();
    }
    public void printMapBalance(){
        System.out.println("Map Balance");
        for(int i = 0; i < maxAccountId; i++){
            if(AccountsMap.getInstance().get(i) != null){
                System.out.print(i + ":" + AccountsMap.getInstance().get(i).getMoney() + " ");
            }
        }
        System.out.println();
    }
    public void printCorrectBalance(){
        System.out.println("Correct Balance");
        for(int i = 0; i < maxAccountId; i++){
            if(correctAccounts[i].isAlive()){
                System.out.print(i + ":" + correctAccounts[i].getBalance() + " ");
            }
        }
        System.out.println();
    }

    public void printDBBalance(){
        System.out.println("DB Balance");
        ResultSet rs = MySQLConnection.getInstance().executeQuery("SELECT * FROM " + Settings.tableName);
        try {
            while(rs.next()){
                Integer id = rs.getInt("id");
                Double money = rs.getDouble("value");
                if(id < maxAccountId){
                    System.out.print(id + ":" + money + " ");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while loading test accounts!");
            e.printStackTrace();
        }
        System.out.println();

    }
    public void readCorrectAccounts(){
        correctAccounts = new TestAccountLocker[maxAccountId];
        for(int i = 0; i < maxAccountId; i++){
            correctAccounts[i] = new TestAccountLocker(false, 0);
        }
        ResultSet rs = MySQLConnection.getInstance().executeQuery("SELECT * FROM " + Settings.tableName);
        try {
            while(rs.next()){
                Integer id = rs.getInt("id");
                Double money = rs.getDouble("value");
                if(id < maxAccountId){
                    correctAccounts[id].add();
                    correctAccounts[id].modifyBalanse(money);
                    accountsCount++;
                }
            }
            // accountsCount = rs.getFetchSize();
        } catch (SQLException e) {
            System.out.println("Error while loading test accounts!");
            e.printStackTrace();
        }
        printCorrectBalance();
    }
    public int getRandomExistingId(){
        if(accountsCount == 0){
            return 0;
        }
        int n = r.nextInt(accountsCount);
        int prevExistingId = 0;
        for(int i = 0; i < accountsCount && i < maxAccountId; i++){
            if(correctAccounts[i].isAlive()){
                if(n == 0){
                    return i;
                } else {
                    prevExistingId = i;
                    n--;
                }
            }
        }
        return  prevExistingId;
    }
    public int getRandomNonExistingId(){
        if(maxAccountId - accountsCount <= 0){
            return 0;
        }
        int n = r.nextInt(maxAccountId - accountsCount);
        int prevNonExistingId = 0;
        for(int i = 0; i < maxAccountId - accountsCount && i < maxAccountId; i++){
            if(!correctAccounts[i].isAlive()){
                if(n == 0){
                    return i;
                } else {
                    prevNonExistingId = i;
                    n--;
                }
            }
        }
        return  prevNonExistingId;
    }
    public void removeAccount(int id){
        synchronized (correctAccounts[id]){
            correctAccounts[id] = null;
        }
    }

}
