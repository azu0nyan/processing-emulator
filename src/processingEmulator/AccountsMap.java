package processingEmulator;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class AccountsMap {

    ConcurrentHashMap<Integer, Account> accounts;

    private static AccountsMap ourInstance = new AccountsMap();
    private AccountsMapDBUpdater updater;
    public static AccountsMap getInstance() {
        return ourInstance;
    }

    private AccountsMap() {
        accounts = new ConcurrentHashMap<Integer, Account>();
        loadFromDb();
        updater = new AccountsMapDBUpdater();
        updater.start();
    }
    public Account get(int id){
        return accounts.get(id);
    }
    public boolean removeAccount(int id){
        return accounts.remove(id) != null;
    }

    public boolean addAccount(int id, int money){
        Account tmp = new Account(id, money);
        return accounts.putIfAbsent(id, tmp) == null;
    }
    public void stopUpdateDb(){
        updater.stop_();
    }
    public void loadFromDb(){
        try {
            ResultSet rs = MySQLConnection.getInstance().executeQuery("SELECT * FROM " + Settings.tableName);
            while (rs.next()){
                int id = rs.getInt("id");
                double money = rs.getDouble("value");
                Account tmp = new Account(id, money);
                accounts.putIfAbsent(id, tmp);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateDB(){
        ResultSet rs = MySQLConnection.getInstance().executeQuery("SELECT * FROM " + Settings.tableName);
        ArrayList<Integer> ids = new ArrayList<Integer>();
        try {
            while (rs.next()){
                int id = rs.getInt("id");
                ids.add(id);
            }

            for (Integer id : ids){ //удаляем лишние
                if(accounts.get(id) == null){
                    MultiThreadExecuteRemoveAccount.getInstance().execute(id);
                }
            }
            for(Integer id : accounts.keySet()){
                if (ids.contains(id)){
                    MultiThreadExecuteUpdateMoney.getInstance().execute(id,accounts.get(id).getMoney());
                } else {
                    MultiThreadExecuteAddAccount.getInstance().execute(id);
                    MultiThreadExecuteUpdateMoney.getInstance().execute(id,accounts.get(id).getMoney());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public LinkedList<SimpleAccount> getSimpleAccounts(){
        LinkedList<SimpleAccount> res = new LinkedList<SimpleAccount>();
        for(Integer i : accounts.keySet()){
            res.add(new SimpleAccount(i, accounts.get(i).getMoney()));
        }
        return res;
    }

}
