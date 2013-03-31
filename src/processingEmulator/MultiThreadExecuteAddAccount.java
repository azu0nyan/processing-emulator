package processingEmulator;


import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiThreadExecuteAddAccount {

    private static MultiThreadExecuteAddAccount ourInstance = new MultiThreadExecuteAddAccount();
    CopyOnWriteArrayList<ExecuteAddAccount> addAccountExecutors;


    public static MultiThreadExecuteAddAccount getInstance() {
        return ourInstance;
    }

    private MultiThreadExecuteAddAccount() {
        addAccountExecutors = new CopyOnWriteArrayList<ExecuteAddAccount>();
    }

    public int execute(int id) throws SQLException {
        ExecuteAddAccount currentExecutor = null;
        try{
            for(ExecuteAddAccount executor : addAccountExecutors){
                if(executor.lock.tryLock()){
                    currentExecutor = executor;
                    break;
                }
            }
            if(currentExecutor == null){
                currentExecutor = new ExecuteAddAccount();
                currentExecutor.lock.lock();
                addAccountExecutors.add(currentExecutor);
            }
            return currentExecutor.action(id);

        } finally {
            if(currentExecutor != null){
                currentExecutor.lock.unlock();
            }
        }
    }
    public int getExecutorsCount(){
        return addAccountExecutors.size();
    }
    public void clear(){
        addAccountExecutors.clear();
    }
}
