package processingEmulator;


import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiThreadExecuteUpdateMoney {

    private static MultiThreadExecuteUpdateMoney ourInstance = new MultiThreadExecuteUpdateMoney();
    CopyOnWriteArrayList<ExecuteUpdateMoney> updateMoneyExecutors;

    public static MultiThreadExecuteUpdateMoney getInstance() {
        return ourInstance;
    }

    private MultiThreadExecuteUpdateMoney() {
        updateMoneyExecutors = new CopyOnWriteArrayList<ExecuteUpdateMoney>();
    }
    public int execute(int id, double money) throws SQLException {
        ExecuteUpdateMoney currentExecutor = null;
        try{
            for(ExecuteUpdateMoney executor : updateMoneyExecutors){
                if(executor.lock.tryLock()){
                    currentExecutor = executor;
                    break;
                }
            }
            if(currentExecutor == null){
                currentExecutor = new ExecuteUpdateMoney();
                currentExecutor.lock.lock();
                updateMoneyExecutors.add(currentExecutor);
            }
            long tempTime = System.currentTimeMillis();
            return currentExecutor.action(id, money);

        } finally {
            if(currentExecutor != null){
                currentExecutor.lock.unlock();
            }
        }
    }
    public int getExecutorsCount(){
        return updateMoneyExecutors.size();
    }
    public void clear(){
        updateMoneyExecutors.clear();
    }
}
