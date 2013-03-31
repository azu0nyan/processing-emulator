package processingEmulator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiThreadExecuteSelectAccount {

    private static MultiThreadExecuteSelectAccount ourInstance = new MultiThreadExecuteSelectAccount();
    CopyOnWriteArrayList<ExecuteSelectAccount> selectAccountExecutors;


    public static MultiThreadExecuteSelectAccount getInstance() {
        return ourInstance;
    }

    private MultiThreadExecuteSelectAccount() {
        selectAccountExecutors = new CopyOnWriteArrayList<ExecuteSelectAccount>();
    }

    public ResultSet execute(int id) throws SQLException {
        ExecuteSelectAccount currentExecutor = null;
        try{
            for(ExecuteSelectAccount executor : selectAccountExecutors){
                if(executor.lock.tryLock()){
                    currentExecutor = executor;
                    break;
                }
            }
            if(currentExecutor == null){
                currentExecutor = new ExecuteSelectAccount();
                currentExecutor.lock.lock();
                selectAccountExecutors.add(currentExecutor);
            }
            return currentExecutor.action(id);

        } finally {
            if(currentExecutor != null){
                currentExecutor.lock.unlock();
            }
        }
    }

    public int getExecutorsCount(){
        return selectAccountExecutors.size();
    }
    public void clear(){
        selectAccountExecutors.clear();
    }
}
